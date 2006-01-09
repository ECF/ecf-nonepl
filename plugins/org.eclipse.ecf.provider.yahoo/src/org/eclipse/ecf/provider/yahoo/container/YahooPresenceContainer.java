/**
 * Copyright (c) 2002-2005 IBM Corporation and others.
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
package org.eclipse.ecf.provider.yahoo.container;

import java.io.IOException;
import java.util.Vector;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IMessageListener;
import org.eclipse.ecf.presence.IMessageSender;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceContainer;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.IPresenceSender;
import org.eclipse.ecf.presence.IRosterEntry;
import org.eclipse.ecf.presence.ISubscribeListener;
import org.eclipse.ecf.presence.IMessageListener.Type;
import org.eclipse.ecf.presence.chat.IChatRoomManager;
import org.eclipse.ecf.presence.impl.Presence;
import org.eclipse.ecf.provider.yahoo.Activator;

import ymsg.network.Session;
import ymsg.network.StatusConstants;
import ymsg.network.YahooUser;
import ymsg.network.event.SessionEvent;
import ymsg.network.event.SessionFriendEvent;

public class YahooPresenceContainer implements IPresenceContainer {

	private Session session;
    private Vector messageListeners = new Vector();
    private Vector presenceListeners = new Vector();
    private Vector subscribeListeners = new Vector();
	
	public YahooPresenceContainer(Session session) {
		this.session = session;
	}

	public void addSubscribeListener(ISubscribeListener listener) {
		subscribeListeners.add(listener);
	}

	public void addPresenceListener(IPresenceListener listener) {
		presenceListeners.add(listener);
	}

	public void addMessageListener(IMessageListener listener) {
		messageListeners.add(listener);
	}

	public IPresenceSender getPresenceSender() {
		return null;
	}

	public IMessageSender getMessageSender() {
		return new IMessageSender() {
			public void sendMessage(
					ID fromID, 
					ID toID, 
					Type type, 
					String subject,
					String messageBody) {
				try {
					session.sendMessage(toID.getName(), messageBody);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	public IAccountManager getAccountManager() {
		return null;
	}

	public IChatRoomManager getChatRoomManager() {
		return null;
	}

	public Object getAdapter(Class adapter) { return null; }

	/**
	 * Notifies any listeners that a message has been received from a yahoo user
	 * 
	 * @param event
	 */
	public void handleMessageReceived(SessionEvent event) {
		for(int i = 0; i < messageListeners.size(); i++) {
			IMessageListener l = (IMessageListener) messageListeners.get(i);
			ID from = makeIDFromName(event.getFrom());
			ID to = makeIDFromName(event.getTo());
			l.handleMessage(
					from, 
					to, 
					IMessageListener.Type.NORMAL, 
					event.getFrom(), 
					event.getMessage());
		}
	}
	

	/**
	 * Notifies any listeners that a friends status has changed
	 * 
	 * @param event
	 */
	public void handleFriendsUpdateReceived(SessionFriendEvent event) {
		for(int i = 0; i < presenceListeners.size(); i++) {
			IPresenceListener l = (IPresenceListener) presenceListeners.get(i);
			ID from = makeIDFromName(event.getFrom());
			IPresence presence = createPresence(from.getName());
			l.handlePresence(from, presence);
		}
	}
	
	/** 
	 * @param name
	 * @return A proper ID based on a yahoo name
	 */
    protected ID makeIDFromName(String name) {
        ID result = null;
        try {
            result = IDFactory.getDefault().createID(
            		IDFactory.getDefault().getNamespaceByName(Activator.NAMESPACE_IDENTIFIER),
            		new Object[] { name });
            return result;
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    protected void fireRosterEntry(IRosterEntry entry) {
        for (int i = 0; i < presenceListeners.size(); i++) {
            IPresenceListener l = (IPresenceListener) presenceListeners.get(i);
            l.handleRosterEntry(entry);
        }
    }
    
    protected void fireContainerJoined(ID container) {
        for (int i = 0; i < presenceListeners.size(); i++) {
            IPresenceListener l = (IPresenceListener) presenceListeners.get(i);
            l.handleContainerJoined(container);
        }
    }
    
    protected void fireContainerDeparted(ID container) {
        for (int i = 0; i < presenceListeners.size(); i++) {
            IPresenceListener l = (IPresenceListener) presenceListeners.get(i);
            l.handleContainerDeparted(container);
        }
    }
    
	/**
	 * Note: This method is simplistic
	 * 
	 * @param userID
	 * @return Determines the presence of a YMSG user
	 */
	public IPresence createPresence(String userID) {
		YahooUser user = session.getUser(userID);
		long status = user.getStatus();
		Presence presence = new Presence(IPresence.Type.UNAVAILABLE);
		if(status == StatusConstants.STATUS_AVAILABLE) {
			presence = new Presence(IPresence.Type.AVAILABLE);
		} else if(status == StatusConstants.STATUS_BRB || status == StatusConstants.STATUS_BUSY) {
			presence = new Presence(IPresence.Type.UNAVAILABLE, "User is away", IPresence.Mode.AWAY);
		} 
		return presence;
	}

}
