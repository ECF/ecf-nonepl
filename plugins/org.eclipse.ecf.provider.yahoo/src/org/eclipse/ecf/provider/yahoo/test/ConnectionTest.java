/**
 * Copyright (c) 2002-2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	- Initial API and implementation
 *  	- Chris Aniszczyk <zx@us.ibm.com>
 *   	- Borna Safabakhsh <borna@us.ibm.com> 
 *   
 * $Id$
 */
package org.eclipse.ecf.provider.yahoo.test;

import java.io.IOException;

import ymsg.network.AccountLockedException;
import ymsg.network.LoginRefusedException;
import ymsg.network.Session;
import ymsg.network.YahooGroup;
import ymsg.network.event.SessionChatEvent;
import ymsg.network.event.SessionConferenceEvent;
import ymsg.network.event.SessionErrorEvent;
import ymsg.network.event.SessionEvent;
import ymsg.network.event.SessionExceptionEvent;
import ymsg.network.event.SessionFileTransferEvent;
import ymsg.network.event.SessionFriendEvent;
import ymsg.network.event.SessionListener;
import ymsg.network.event.SessionNewMailEvent;
import ymsg.network.event.SessionNotifyEvent;

public class ConnectionTest implements SessionListener {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Session session = new Session();
		
		ConnectionTest test = new ConnectionTest();
		
		session.addSessionListener(test);
		try {
			session.login("xxx", "yyy");
		} catch (AccountLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginRefusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		YahooGroup[] groups = session.getGroups();
		for(int i = 0; i < groups.length; i++) {
			System.out.println(groups[i]);
			System.out.println(groups[i].getMembers());
		}
		System.out.println(session.getGroups());
		try {
			session.logout();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void fileTransferReceived(SessionFileTransferEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void connectionClosed(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void listReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void messageReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void buzzReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void offlineMessageReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void errorPacketReceived(SessionErrorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void inputExceptionThrown(SessionExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void newMailReceived(SessionNewMailEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void notifyReceived(SessionNotifyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contactRequestReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contactRejectionReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void conferenceInviteReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void conferenceInviteDeclinedReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void conferenceLogonReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void conferenceLogoffReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void conferenceMessageReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void friendsUpdateReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void friendAddedReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void friendRemovedReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void chatLogonReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void chatLogoffReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void chatMessageReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void chatUserUpdateReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void chatConnectionClosed(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
