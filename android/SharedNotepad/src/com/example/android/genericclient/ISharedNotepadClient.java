package com.example.android.genericclient;

import java.io.IOException;

import org.eclipse.ecf.core.identity.ID;

public interface ISharedNotepadClient {

	public String getUsername();
	public String getLocalOriginalContent();
	
	public ISharedNotepadListener getSharedNotepadListener();

	public void sendUpdate(String content) throws IOException;
	
	public ID getClientID();
	public ID getConnectedID();
	
}
