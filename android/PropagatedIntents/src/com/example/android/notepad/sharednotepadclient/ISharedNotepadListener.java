package com.example.android.notepad.sharednotepadclient;

import org.eclipse.ecf.core.identity.ID;

public interface ISharedNotepadListener {

	public void receiveUpdate(ID clientID, String username, String uri);
	
}
