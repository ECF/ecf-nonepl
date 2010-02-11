package com.composent.android.server.internal.sharednotepad;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;

public class ServerNotepadSharedObject extends BaseSharedObject {

	protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
		try {
			msg.invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void handleUpdateMsg(ID senderID, String username, String content) {
		System.out.println("handleUpdateMsg senderID="+senderID+" username="+username +" content="+content);
	}

}
