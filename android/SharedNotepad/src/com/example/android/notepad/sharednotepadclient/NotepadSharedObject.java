package com.example.android.notepad.sharednotepadclient;

import java.io.IOException;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;

public class NotepadSharedObject extends BaseSharedObject {

	private static final String HANDLE_UPDATE_MSG = "handleUpdateMsg";

	private ISharedNotepadListener listener;
	
	private String username;
	private String localOriginalContent;
	
	public NotepadSharedObject(String username, String localOriginalContent, ISharedNotepadListener listener) {
		this.username = username;
		this.localOriginalContent = localOriginalContent;
		this.listener = listener;
	}

	protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
		try {
			msg.invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getUsername() {
		return username;
	}
	
	public String getLocalOriginalContent() {
		return localOriginalContent;
	}
	
	public ISharedNotepadListener getSharedNotepadListener() {
		return listener;
	}

	public ID getConnectedID() {
		return super.getConnectedID();
	}
	
	public ID getClientID() {
		return getLocalContainerID();
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	// Message sender
	public void sendUpdate(String content) throws IOException {
		sendSharedObjectMsgTo(null, SharedObjectMsg.createMsg(HANDLE_UPDATE_MSG, new Object[] { getLocalContainerID(), username, content }));
	}

	// Message receiver
	protected void handleUpdateMsg(ID senderID, String username, String content) {
		if (listener != null) {
			listener.receiveUpdate(senderID, username, content);
		}
	}

}