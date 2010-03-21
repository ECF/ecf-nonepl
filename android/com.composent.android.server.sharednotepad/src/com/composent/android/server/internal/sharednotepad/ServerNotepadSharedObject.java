package com.composent.android.server.internal.sharednotepad;

import java.io.IOException;

import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.core.util.IEventProcessor;

public class ServerNotepadSharedObject extends BaseSharedObject {

	private static final String HANDLE_UPDATE_MSG = "handleUpdateMsg";
	private static final String HANDLE_INTENT_MSG = "handleIntentMsg";
	
	protected void initialize() throws SharedObjectInitException {
		super.initialize();
		addEventProcessor(new IEventProcessor() {
			public boolean processEvent(Event event) {
				if (event instanceof IContainerConnectedEvent) {
					IContainerConnectedEvent cce = (IContainerConnectedEvent) event;
					handleConnected(cce.getTargetID());
				}
				return false;
			}});
	}
	
	protected void handleConnected(ID targetID) {
		// XXX testing...this sends a message to the newly connected client
		try {
			sendSharedObjectMsgTo(null, SharedObjectMsg.createMsg(HANDLE_UPDATE_MSG, new Object[] { getLocalContainerID(), "server", "startup", "ecftcp://10.0.2.2:3282" }));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
		try {
			msg.invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void handleUpdateMsg(ID senderID, String username, String uri) {
		System.out.println("handleUpdateMsg senderID="+senderID+" username="+username +" uri="+uri);
	}

	protected void handleIntentMsg(ID senderID, String username, String uri, String data) {
		System.out.println("handleIntentMsg senderID="+senderID+" username="+username +" uri="+uri +" extra="+data);
		try {
			sendSharedObjectMsgTo(null, SharedObjectMsg.createMsg(HANDLE_INTENT_MSG, new Object[] { getLocalContainerID(), username, uri, data }));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void handleLocationMsg(ID senderID, String username, Double latitude, Double longitude, Double altitude) {
		System.out.println("handleLocationMsg senderID="+senderID+" username="+username+" lat="+latitude+" lon="+longitude+" alt="+altitude);
	}

}
