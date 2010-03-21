package com.example.android.notepad.sharednotepadclient;

import java.io.IOException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;

import android.os.Bundle;

public interface ISharedNotepadClient {

	public void connect(String targetId) throws ContainerConnectException;
	
	public String getUsername();
	public String getLocalOriginalContent();
	public ISharedNotepadListener getSharedNotepadListener();
	public ID getClientID();
	public ID getConnectedID();
	public void close();

	public void sendUpdate(String uri, Bundle bundle) throws IOException;
	
}
