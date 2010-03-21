package com.example.android.notepad.sharednotepadclient;

import org.eclipse.ecf.core.identity.ID;

import android.os.Bundle;

public interface ISharedNotepadListener {

	public void receiveUpdate(ID clientID, String username, String uri, Bundle data);
	
}
