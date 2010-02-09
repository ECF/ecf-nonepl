package com.example.android.notepad.sharednotepadclient;

import java.io.IOException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddException;

import com.example.android.sharedobjectservice.ISharedObjectContainerService;

public class SharedNotepadClient implements ISharedNotepadClient {

	private ISharedObjectContainer clientContainer;
	private NotepadSharedObject notepadSharedObject;
	
	public SharedNotepadClient(ISharedObjectContainerService containerService, String username, String originalContent, ISharedNotepadListener listener) {
    	try {
    		clientContainer = containerService.createClientContainer();
    	} catch (ContainerCreateException e) {
    		e.printStackTrace();
    	}
    	//  Then create/add NotepadSharedObject
    	notepadSharedObject = new NotepadSharedObject(username, originalContent, listener);
    	try {
			clientContainer.getSharedObjectManager().addSharedObject(IDFactory.getDefault().createStringID("com.composent.genericclient.notepad.sharedobject"),notepadSharedObject,null);
		} catch (SharedObjectAddException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(String targetId) throws ContainerConnectException {
		// connect
		try {
			clientContainer.connect(IDFactory.getDefault().createStringID(targetId), null);
		} catch (ContainerConnectException e) {
			e.printStackTrace();
		}
	}
	
	
	public void close() {
		if (clientContainer != null) {
			clientContainer.dispose();
			clientContainer = null;
			notepadSharedObject = null;
		}
	}

	public ID getClientID() {
		return clientContainer.getID();
	}

	public ID getConnectedID() {
		return clientContainer.getConnectedID();
	}

	public String getLocalOriginalContent() {
		return notepadSharedObject.getLocalOriginalContent();
	}

	public ISharedNotepadListener getSharedNotepadListener() {
		return notepadSharedObject.getSharedNotepadListener();
	}

	public String getUsername() {
		return notepadSharedObject.getUsername();
	}

	public void sendUpdate(String content) throws IOException {
		notepadSharedObject.sendUpdate(content);
	}
}
