package com.example.android.genericclient;

import org.eclipse.ecf.core.identity.ID;

public interface ISharedNotepadListener {

	public void receiveUpdate(ID clientID, String username, String content);
	
}
