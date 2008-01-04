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

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
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

import com.skype.Friend;
import com.skype.Profile;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import com.skype.Profile.Status;
import com.skype.connector.AbstractConnectorListener;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

/**
 * 
 */
public class SkypeRosterManager extends AbstractRosterManager implements IRosterManager {

	private static final boolean debug = false;

	private final SkypeContainer container;

	List presenceListeners = new ArrayList();

	private final ConnectorListener rosterChangeConnectorListener = new AbstractConnectorListener() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.skype.connector.AbstractConnectorListener#messageReceived(com.skype.connector.ConnectorMessageEvent)
		 */
		public void messageReceived(ConnectorMessageEvent event) {
			String message = event.getMessage();
			if (message.startsWith("USER ")) { //$NON-NLS-1$
				String data = message.substring("USER ".length()); //$NON-NLS-1$
				String skypeId = data.substring(0, data.indexOf(' '));
				data = data.substring(data.indexOf(' ') + 1);
				String propertyName = data.substring(0, data.indexOf(' '));
				String propertyValue = data.substring(data.indexOf(' ') + 1);
				if (propertyName.equals("BUDDYSTATUS")) { //$NON-NLS-1$
					handleBuddyStatusChange(skypeId, propertyValue);
				}
			}
		}

	};

	private void handleBuddyStatusChange(String skypeId, String propertyValue) {
		int buddyStatus = 0;
		try {
			buddyStatus = Integer.parseInt(propertyValue);
		} catch (final Exception e) {
		}
		switch (buddyStatus) {
			case 0 :
				break;
			case 1 :
				fireRosterChange(skypeId, false);
				break;
			case 2 :
				break;
			case 3 :
				fireRosterChange(skypeId, true);
		}
	}

	/**
	 * @param skypeId
	 * @param add
	 */
	private void fireRosterChange(String skypeId, boolean add) {
		if (add) {
			final IRosterEntry entry = addEntryToRoster(User.getInstance(skypeId));
			fireRosterAdd(entry);
			fireRosterUpdate(entry);
		} else {
			final IRosterEntry entry = getEntryForFriend(skypeId);
			if (entry != null) {
				fireRosterRemove(entry);
				fireSubscriptionListener(entry.getUser().getID(), IPresence.Type.UNSUBSCRIBED);
				fireRosterUpdate(entry);
			}
		}
	}

	private final ConnectorListener connectorListener = new ConnectorListener() {

		public void messageReceived(ConnectorMessageEvent event) {
			if (debug) {
				System.out.println("SkypeRosterManager.messageReceived(time=" + event.getTime() + "," + event.getSource() + "," + event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		public void messageSent(ConnectorMessageEvent event) {
			if (debug) {
				System.out.println("SkypeRosterManager.messageSent(time=" + event.getTime() + "," + event.getSource() + "," + event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

		}

		public void statusChanged(ConnectorStatusEvent event) {
		}

	};

	IPresenceSender presenceSender = new IPresenceSender() {
		public void sendPresenceUpdate(ID targetID, IPresence presence) throws ECFException {
			Assert.isNotNull(presence);
			Profile profile = Skype.getProfile();
			if (profile == null)
				throw new ECFException(Messages.SkypeRosterManager_EXCEPTION_PROFILE_NOT_AVAILABLE);
			try {
				profile.setStatus(createStatusForPresence(presence));
			} catch (SkypeException e) {
				throw new ECFException(Messages.SkypeRosterManager_EXCEPTION_USER_STATUS, e);
			}
		}
	};

	Status createStatusForPresence(IPresence presence) {
		final IPresence.Mode mode = presence.getMode();
		final IPresence.Type type = presence.getType();
		if (type.equals(IPresence.Type.AVAILABLE)) {
			if (mode.equals(IPresence.Mode.AWAY))
				return Profile.Status.AWAY;
			else if (mode.equals(IPresence.Mode.DND))
				return Profile.Status.DND;
			else if (mode.equals(IPresence.Mode.INVISIBLE))
				return Profile.Status.INVISIBLE;
			else if (mode.equals(IPresence.Mode.EXTENDED_AWAY))
				return Profile.Status.NA;
			else
				return Profile.Status.ONLINE;
		} else
			return Profile.Status.OFFLINE;
	}

	IRosterSubscriptionSender rosterSubscriptionSender = new IRosterSubscriptionSender() {
		public void sendRosterAdd(String user, String name, String[] groups) throws ECFException {
			// TODO Auto-generated method stub

		}

		public void sendRosterRemove(ID userID) throws ECFException {
			// TODO Auto-generated method stub

		}
	};

	public SkypeRosterManager(SkypeContainer skypeContainer, org.eclipse.ecf.core.user.User user) throws SkypeException, ConnectorException {
		this.container = skypeContainer;
		this.roster = new Roster(container, user);
	}

	protected IUser createUser(User f) {
		final SkypeUserID userID = new SkypeUserID(f.getId());
		final Map properties = new HashMap();
		String name = null;
		try {
			properties.put("Country", f.getCountry()); //$NON-NLS-1$
			properties.put("City", f.getCity()); //$NON-NLS-1$
			properties.put("Home Phone", f.getHomePhoneNumber()); //$NON-NLS-1$
			properties.put("Mobile Phone", f.getMobilePhoneNumber()); //$NON-NLS-1$
			properties.put("Mood Message", f.getMoodMessage()); //$NON-NLS-1$
			properties.put("Birthday", f.getBirthDay()); //$NON-NLS-1$
			name = f.getFullName();
		} catch (final SkypeException e) {
		}
		return new org.eclipse.ecf.core.user.User(userID, (name == null || name.equals("")) ? userID.getName() : name, properties); //$NON-NLS-1$
	}

	protected Map createProperties(User friend) throws SkypeException {
		// XXX todo
		final Map props = new HashMap();
		props.put("Cell Phone", friend.getMobilePhoneNumber()); //$NON-NLS-1$
		props.put("Home Page", friend.getHomePageAddress()); //$NON-NLS-1$
		return props;
	}

	protected IPresence createPresence(User friend) {
		IPresence.Type type = null;
		IPresence.Mode mode = null;
		Map properties = null;
		String moodMessage = null;
		try {
			final User.Status status = friend.getStatus();
			type = createPresenceType(status);
			mode = createPresenceMode(status);
			properties = createProperties(friend);
			moodMessage = friend.getMoodMessage();
		} catch (final SkypeException e) {
			type = IPresence.Type.UNAVAILABLE;
			mode = IPresence.Mode.AWAY;
		}
		byte[] imageBytes = null;
		try {
			final BufferedImage bi = friend.getAvatar();
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", bos); //$NON-NLS-1$
			imageBytes = bos.toByteArray();
		} catch (final Exception e) {
			Activator.log(Messages.SkypeRosterManager_EXCEPTION_GETTING_AVATAR, e);
		}
		return new Presence(type, (moodMessage == null) ? "" : moodMessage, mode, properties, imageBytes); //$NON-NLS-1$
	}

	protected void handleFriendPropertyChangeEvent(PropertyChangeEvent evt) {
		final Object src = evt.getSource();
		final Object newValue = evt.getNewValue();
		if (src instanceof Friend && newValue instanceof User.Status) {
			final IRosterEntry entry = updateExistingRosterEntry((Friend) src, (User.Status) evt.getNewValue());
			if (entry != null)
				fireRosterUpdate(entry);
		}
	}

	private IRosterEntry updateExistingRosterEntry(Friend friend, User.Status status) {
		final IRosterEntry entry = getEntryForFriend(friend.getId());
		if (entry != null) {
			((RosterEntry) entry).setPresence(createPresenceForExistingEntry(entry, status));
			return entry;
		}
		return null;
	}

	private IRosterEntry getEntryForFriend(String friendId) {
		final SkypeUserID friendID = new SkypeUserID(friendId);
		for (final Iterator i = roster.getItems().iterator(); i.hasNext();) {
			final IRosterItem item = (IRosterItem) i.next();
			if (item instanceof IRosterEntry) {
				final IRosterEntry entry = (IRosterEntry) item;
				if (entry.getUser().getID().equals(friendID)) {
					return entry;
				}
			}
		}
		return null;
	}

	private IPresence.Type createPresenceType(User.Status status) {
		return (status.equals(User.Status.OFFLINE)) ? IPresence.Type.UNAVAILABLE : IPresence.Type.AVAILABLE;
	}

	private IPresence.Mode createPresenceMode(User.Status status) {
		IPresence.Mode mode = null;
		if (status.equals(User.Status.AWAY))
			mode = IPresence.Mode.AWAY;
		else if (status.equals(User.Status.DND))
			mode = IPresence.Mode.DND;
		else if (status.equals(User.Status.NA))
			mode = IPresence.Mode.EXTENDED_AWAY;
		else
			mode = IPresence.Mode.AVAILABLE;
		return mode;
	}

	/**
	 * @param status
	 * @return
	 */
	private IPresence createPresenceForExistingEntry(IRosterEntry existingEntry, User.Status status) {
		final IPresence existingPresence = existingEntry.getPresence();
		final Map props = existingPresence.getProperties();
		final String moodMessage = existingPresence.getStatus();
		final byte[] image = existingPresence.getPictureData();
		return new Presence(createPresenceType(status), (moodMessage == null) ? "" : moodMessage, createPresenceMode(status), props, image); //$NON-NLS-1$
	}

	private IRosterEntry createRosterEntry(User friend) {
		return new RosterEntry(roster, createUser(friend), createPresence(friend));
	}

	private IRosterEntry addEntryToRoster(User friend) {
		final IRosterEntry entry = createRosterEntry(friend);
		((Roster) roster).addItem(entry);
		return entry;
	}

	protected void fillRoster() throws SkypeException, ConnectorException {
		Connector.getInstance().addConnectorListener(connectorListener);
		Connector.getInstance().addConnectorListener(rosterChangeConnectorListener);
		final Friend[] friends = Skype.getContactList().getAllFriends();
		for (int i = 0; i < friends.length; i++) {
			final IRosterEntry entry = addEntryToRoster(friends[i]);
			// Add property listener
			friends[i].addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					handleFriendPropertyChangeEvent(evt);
				}
			});
			fireRosterAdd(entry);
			fireRosterUpdate(entry);
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

	public void disconnect() {
		try {
			Connector.getInstance().removeConnectorListener(connectorListener);
		} catch (final Exception e) {
		}
		try {
			Connector.getInstance().removeConnectorListener(rosterChangeConnectorListener);
		} catch (final Exception e) {
		}
		presenceListeners.clear();
		super.disconnect();
	}

	/**
	 * 
	 */
	protected void dispose() {
	}

}
