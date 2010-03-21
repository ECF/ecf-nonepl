package com.example.android.notepad.sharednotepadclient;

import java.io.IOException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddException;

import android.location.LocationManager;
import android.net.Uri;

import com.example.android.sharedobjectservice.ISharedObjectContainerService;

public class SharedNotepadClient implements ISharedNotepadClient {

	private final ID notepadSharedObjectID = IDFactory.getDefault().createStringID("com.composent.android.sharednotepad.sharedobject");
	private ISharedObjectContainer clientContainer;
	private NotepadSharedObject notepadSharedObject;
	
	public SharedNotepadClient(ISharedObjectContainerService containerService, String username, String originalContent, ISharedNotepadListener listener, LocationManager locationManager) {
    	try {
    		clientContainer = containerService.createClientContainer();
    	} catch (ContainerCreateException e) {
    		e.printStackTrace();
    	}
    	//  Then create/add NotepadSharedObject
    	notepadSharedObject = new NotepadSharedObject(username, originalContent, listener, locationManager);
    	try {
			clientContainer.getSharedObjectManager().addSharedObject(notepadSharedObjectID,notepadSharedObject,null);
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

	public void sendUpdate(String uri) throws IOException {
		notepadSharedObject.sendUpdate(uri);
	}
}
