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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.IPresenceSender;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.roster.AbstractRosterManager;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.roster.IRosterSubscriptionSender;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterEntry;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import com.skype.connector.AbstractConnectorListener;
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
	private SkypeContainer container;

	Vector presenceListeners = new Vector();

	private ConnectorListener rosterChangeConnectorListener = new AbstractConnectorListener() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.skype.connector.AbstractConnectorListener#messageReceived(com.skype.connector.ConnectorMessageEvent)
		 */
		public void messageReceived(ConnectorMessageEvent event) {
			String message = event.getMessage();
			if (message.startsWith("USER ")) {
				String data = message.substring("USER ".length());
				String skypeId = data.substring(0, data.indexOf(' '));
				data = data.substring(data.indexOf(' ') + 1);
				String propertyName = data.substring(0, data.indexOf(' '));
				String propertyValue = data.substring(data.indexOf(' ') + 1);
				if (propertyName.equals("BUDDYSTATUS")) {
					handleBuddyStatusChange(skypeId, propertyValue);
				}
			}
		}

	};

	private void handleBuddyStatusChange(String skypeId, String propertyValue) {
		int buddyStatus = 0;
		try {
			buddyStatus = Integer.parseInt(propertyValue);
		} catch (Exception e) {
		}
		switch (buddyStatus) {
		case 0:
			break;
		case 1:
			fireRosterChange(skypeId, false);
			break;
		case 2:
			break;
		case 3:
			fireRosterChange(skypeId, true);
		}
	}

	/**
	 * @param skypeId
	 * @param b
	 */
	private void fireRosterChange(String skypeId, boolean addToRoster) {
		synchronized (presenceListeners) {
			for (Iterator i = presenceListeners.iterator(); i.hasNext();) {
				IPresenceListener l = (IPresenceListener) i.next();
				if (addToRoster)
					l.handleRosterEntryAdd(createRosterEntry(User
							.getInstance(skypeId)));
				else
					l.handleRosterEntryRemove(new RosterEntry(roster,
							new org.eclipse.ecf.core.user.User(new SkypeUserID(
									skypeId)), new Presence()));
			}
		}
	}

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

	protected IUser createUser(User f) {
		SkypeUserID userID = new SkypeUserID(f.getId());
		Map properties = new HashMap();
		String name = null;
		try {
			properties.put("Country", f.getCountry());
			properties.put("City", f.getCity());
			properties.put("Home Phone", f.getHomePhoneNumber());
			properties.put("Mobile Phone", f.getMobilePhoneNumber());
			properties.put("Mood Message", f.getMoodMessage());
			properties.put("Birthday", f.getBirthDay());
			name = f.getFullName();
		} catch (SkypeException e) {
		}
		return new org.eclipse.ecf.core.user.User(userID, (name == null || name
				.equals("")) ? userID.getName() : name, properties);
	}

	protected Map createProperties(User friend) throws SkypeException {
		// XXX todo
		Map props = new HashMap();
		props.put("Cell Phone", friend.getMobilePhoneNumber());
		props.put("Home Page", friend.getHomePageAddress());
		return props;
	}

	protected IPresence createPresence(User friend) {
		IPresence.Type type = null;
		IPresence.Mode mode = null;
		Map properties = null;
		String moodMessage = null;
		try {
			User.Status status = friend.getStatus();
			type = (status.equals(User.Status.OFFLINE)) ? IPresence.Type.UNAVAILABLE
					: IPresence.Type.AVAILABLE;
			if (status.equals(User.Status.AWAY))
				mode = IPresence.Mode.AWAY;
			else if (status.equals(User.Status.DND))
				mode = IPresence.Mode.DND;
			else if (status.equals(User.Status.NA))
				mode = IPresence.Mode.EXTENDED_AWAY;
			else
				mode = IPresence.Mode.AVAILABLE;
			properties = createProperties(friend);
			moodMessage = friend.getMoodMessage();
		} catch (SkypeException e) {
			type = IPresence.Type.UNAVAILABLE;
			mode = IPresence.Mode.AWAY;
		}
		// XXX this doesn't work yet in Skype4Java apparently...getAvatar()
		// current throws exception.
		/*
		 * BufferedImage bi = friend.getAvatar(); ByteArrayOutputStream bos =
		 * new ByteArrayOutputStream(); ImageIO.write(bi,"jpg",bos); byte []
		 * imageBytes = bos.toByteArray();
		 */
		byte[] imageBytes = null;
		return new Presence(type, (moodMessage == null) ? "" : moodMessage,
				mode, properties, imageBytes);
	}

	protected void handleFriendPropertyChangeEvent(PropertyChangeEvent evt) {
		System.out.println("handlePropertyChangeEvent(source="
				+ evt.getSource());
		System.out.println("   propid=" + evt.getPropagationId());
		System.out.println("   propname=" + evt.getPropertyName());
		System.out.println("   oldvalue=" + evt.getOldValue());
		System.out.println("   newvalue=" + evt.getNewValue());
		Object src = evt.getSource();
		Object newValue = evt.getNewValue();
		if (src instanceof Friend && newValue instanceof User.Status)
			fireUpdatePresenceListeners((Friend) src, (User.Status) evt
					.getNewValue());
	}

	protected void fireUpdatePresenceListeners(Friend friend,
			User.Status newstatus) {
		IRosterEntry entry = updateRosterEntry(friend, newstatus);
		if (entry != null) {
			synchronized (presenceListeners) {
				for (Iterator i = presenceListeners.iterator(); i.hasNext();) {
					IPresenceListener l = (IPresenceListener) i.next();
					l.handlePresence(entry.getUser().getID(), entry
							.getPresence());
					l.handleRosterEntryUpdate(entry);
				}
			}
		}
	}

	protected IRosterEntry updateRosterEntry(Friend friend, User.Status status) {
		Collection items = roster.getItems();
		SkypeUserID friendID = new SkypeUserID(friend.getId());
		for (Iterator i = items.iterator(); i.hasNext();) {
			IRosterItem item = (IRosterItem) i.next();
			if (item instanceof IRosterEntry) {
				IRosterEntry entry = (IRosterEntry) item;
				if (entry.getUser().getID().equals(friendID)) {
					// Found it...so remove
					((RosterEntry) entry)
							.setPresence(createPresenceFromNewStatus(entry,
									status));
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * @param status
	 * @return
	 */
	private IPresence createPresenceFromNewStatus(IRosterEntry existingEntry,
			User.Status status) {
		IPresence existingPresence = existingEntry.getPresence();
		Map props = existingPresence.getProperties();
		String moodMessage = existingPresence.getStatus();
		byte[] image = existingPresence.getPictureData();
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

		return new Presence(type, (moodMessage == null) ? "" : moodMessage,
				mode, props, image);
	}

	private IRosterEntry createRosterEntry(User friend) {
		return new RosterEntry(roster, createUser(friend),
				createPresence(friend));
	}

	public SkypeRosterManager(SkypeContainer skypeContainer,
			org.eclipse.ecf.core.user.User user) throws SkypeException,
			ConnectorException {
		this.container = skypeContainer;
		this.roster = new Roster(container, user);
	}

	protected void fillRoster() throws SkypeException, ConnectorException {
		Connector.getInstance().addConnectorListener(connectorListener);
		Connector.getInstance().addConnectorListener(
				rosterChangeConnectorListener);
		contactList = Skype.getContactList();
		Friend[] friends = contactList.getAllFriends();
		for (int i = 0; i < friends.length; i++) {
			final Friend friend = friends[i];
			((Roster) roster).addItem(createRosterEntry(friend));
			// Add property listener
			friend.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					handleFriendPropertyChangeEvent(evt);
				}
			});
			fireUpdatePresenceListeners(friend, friend.getStatus());
		}
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
		presenceListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#removePresenceListener(org.eclipse.ecf.presence.IPresenceListener)
	 */
	public void removePresenceListener(IPresenceListener listener) {
		presenceListeners.remove(listener);
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
		try {
			Connector.getInstance().removeConnectorListener(
					rosterChangeConnectorListener);
		} catch (Exception e) {
			// XXX log error
		}
	}

}
