package com.example.android.genericclient;

import org.eclipse.ecf.core.identity.ID;

public interface ISharedNotepadService {

	public ISharedNotepadClient createAndConnectClient(String targetId, String username, String originalLocalContent, ISharedNotepadListener listener);
	
	public ISharedNotepadClient getClient(ID clientID);
	
}
