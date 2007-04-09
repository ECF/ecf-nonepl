/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.IPresenceSender;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.roster.AbstractRosterManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.roster.IRosterSubscriptionSender;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterEntry;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Profile;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

/**
 * 
 */
public class SkypeRosterManager extends AbstractRosterManager implements
		IRosterManager {

	private ContactList contactList;
	private Profile profile;
	private SkypeUserID userID;
	private IUser user;
	private SkypeContainer container;

	private ConnectorListener connectorListener = new ConnectorListener() {

		public void messageReceived(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			System.out.println("SkypeRosterManager.messageReceived(time="
					+ event.getTime() + "," + event.getSource() + ","
					+ event.getMessage());
		}

		public void messageSent(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			System.out.println("SkypeRosterManager.messageSent(time="
					+ event.getTime() + "," + event.getSource() + ","
					+ event.getMessage());

		}

		public void statusChanged(ConnectorStatusEvent event) {
		}

	};

	IPresenceSender presenceSender = new IPresenceSender() {
		public void sendPresenceUpdate(ID targetID, IPresence presence)
				throws ECFException {
			// TODO Auto-generated method stub

		}
	};

	IRosterSubscriptionSender rosterSubscriptionSender = new IRosterSubscriptionSender() {
		public void sendRosterAdd(String user, String name, String[] groups)
				throws ECFException {
			// TODO Auto-generated method stub

		}

		public void sendRosterRemove(ID userID) throws ECFException {
			// TODO Auto-generated method stub

		}
	};

	protected IUser createUser(Friend f) throws SkypeException {
		Map properties = new HashMap();
		properties.put("Country", f.getCountry());
		properties.put("City", f.getCity());
		properties.put("Home Phone", f.getHomePhoneNumber());
		properties.put("Mobile Phone", f.getMobilePhoneNumber());
		properties.put("Mood Message", f.getMoodMessage());
		properties.put("Birthday", f.getBirthDay());
		SkypeUserID userID = new SkypeUserID(f.getId());
		String name = f.getFullName();
		String displayName = (name == null || name.equals("")) ? userID
				.getName() : name;
		return new org.eclipse.ecf.core.user.User(userID, displayName,
				properties);
	}

	protected Map createProperties(Friend friend) throws SkypeException {
		// XXX todo
		Map props = new HashMap();
		props.put("Cell Phone", friend.getMobilePhoneNumber());
		props.put("Home Page", friend.getHomePageAddress());
		return props;
	}

	protected IPresence createPresence(Friend friend) throws SkypeException {
		User.Status status = friend.getStatus();
		IPresence.Type type = (status.equals(User.Status.OFFLINE)) ? IPresence.Type.UNAVAILABLE
				: IPresence.Type.AVAILABLE;
		IPresence.Mode mode = null;
		if (status.equals(User.Status.AWAY))
			mode = IPresence.Mode.AWAY;
		else if (status.equals(User.Status.DND))
			mode = IPresence.Mode.DND;
		else if (status.equals(User.Status.NA))
			mode = IPresence.Mode.EXTENDED_AWAY;
		else
			mode = IPresence.Mode.AVAILABLE;
		// XXX this doesn't work yet in Skype4Java apparently...getAvatar()
		// current throws exception.
		/*
		 * BufferedImage bi = friend.getAvatar(); ByteArrayOutputStream bos =
		 * new ByteArrayOutputStream(); ImageIO.write(bi,"jpg",bos); byte []
		 * imageBytes = bos.toByteArray();
		 */
		byte[] imageBytes = null;
		return new Presence(type, type.toString(), mode,
				createProperties(friend), imageBytes);
	}

	public SkypeRosterManager(SkypeContainer skypeContainer,
			Profile skypeProfile, SkypeUserID userID,
			org.eclipse.ecf.core.user.User user) throws SkypeException, ConnectorException {
		this.container = skypeContainer;
		contactList = Skype.getContactList();
		this.profile = skypeProfile;
		this.userID = userID;
		this.user = user;
		roster = new Roster(container, user);
		Friend[] friends = contactList.getAllFriends();
		for (int i = 0; i < friends.length; i++) {
			((Roster) roster).addItem(new RosterEntry(roster,
					createUser(friends[i]), createPresence(friends[i])));
		}

		Connector.getInstance().addConnectorListener(connectorListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#getPresenceSender()
	 */
	public IPresenceSender getPresenceSender() {
		return presenceSender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#addPresenceListener(org.eclipse.ecf.presence.IPresenceListener)
	 */
	public void addPresenceListener(IPresenceListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#removePresenceListener(org.eclipse.ecf.presence.IPresenceListener)
	 */
	public void removePresenceListener(IPresenceListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#getRosterSubscriptionSender()
	 */

	public IRosterSubscriptionSender getRosterSubscriptionSender() {
		return rosterSubscriptionSender;
	}

	/**
	 * 
	 */
	protected void dispose() {
		contactList = null;
		try {
			Connector.getInstance().removeConnectorListener(connectorListener);
		} catch (Exception e) {
			// XXX log error
		}
	}

}
