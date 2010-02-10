/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.oscar;

import java.io.IOException;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.oscar.Messages;
import org.eclipse.ecf.internal.provider.oscar.OSCARPlugin;
import org.eclipse.ecf.internal.provider.oscar.icqlib.OSCARConnection;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.ecf.provider.oscar.identity.OSCARID;
import ru.caffeineim.protocols.icq.contacts.Contact;
import ru.caffeineim.protocols.icq.contacts.Group;

public class OSCARRosterManager extends AbstractRosterManager {

	public static final String EMPTY = ""; //$NON-NLS-1$

	private final List presenceListeners = new ArrayList();

	private OSCARContainer container = null;

	public OSCARRosterManager(Roster roster, OSCARContainer container) {
		super(roster);
		this.container = container;
	}

	// TODO:
	/*
	 * protected void handlePresenceEvent(PresenceEvent evt) {
	 *   // info about contact login/logout
	 *   // make vcard with contact's info
	 *   updatePresence(fromID, newPresence);
	 *	 firePresenceListeners(fromID, newPresence);
	 * }
	 *
	 */

	public void notifyRosterUpdate(IRosterItem changedItem) {
		fireRosterUpdate(changedItem);
	}

	public void notifyRosterAdd(IRosterEntry entry) {
		fireRosterAdd(entry);
	}

	public void notifyRosterRemove(IRosterEntry entry) {
		fireRosterRemove(entry);
	}

	public void disconnect() {
		getRoster().getItems().clear();
		super.disconnect();
		fireRosterUpdate(roster);
	}

	public void setUser(IUser user) {
		final Roster myroster = (Roster) getRoster();
		myroster.setUser(user);
		notifyRosterUpdate(myroster);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#addPresenceListener(
	 * 			org.eclipse.ecf.presence.roster.IPresenceListener)
	 */
	public void addPresenceListener(IPresenceListener listener) {
		synchronized (presenceListeners) {
			presenceListeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.presence.roster.IRosterManager#removePresenceListener(
	 * 			org.eclipse.ecf.presence.roster.IPresenceListener)
	 */
	public void removePresenceListener(IPresenceListener listener) {
		synchronized (presenceListeners) {
			presenceListeners.remove(listener);
		}
	}

	public void makeRoster(Group root) {
		if (root == null)
			trace(Messages.OSCAR_ROSTER_EXCEPTION_ROOT_GROUP_NULL);

		addUniqueToRoster(createRosterEntries(root.getContainedItems().iterator(), roster));
	}

	protected OSCARID createIDFromName(String userId) {
		try {
			return new OSCARID(container.getConnectNamespace(), userId);
		} catch (final Exception e) {
			trace(Messages.OSCAR_ROSTER_EXCEPTION_ID_CREATE, e);
			return null;
		}
	}

	private void addUniqueToRoster(IRosterItem[] newItems) {
		Collection existingItems = roster.getItems();
		synchronized (existingItems) {
			for (int i = 0; i < newItems.length; i++) {
				if (!existingItems.contains(newItems[i]))
					existingItems.add(newItems[i]);
			}
		}
		notifyRosterUpdate(roster);
	}

	private IRosterItem[] createRosterEntries(Iterator grps, IRosterItem parent) {
		final List result = new ArrayList();
		if (grps.hasNext()) {
			for (; grps.hasNext();) {
				final Object o = grps.next();
				if (o instanceof Group) {
					// Get group
					final Group group = (Group) o;

					if (group == null || group.getId() == null || EMPTY.equals(group.getId()))
						continue;

					// See if group is already in roster
					RosterGroup rosterGroup = findRosterGroup(parent, group.getId());

					// Set flag if not
					final boolean groupFound = rosterGroup != null;
					if (!groupFound)
						rosterGroup = new RosterGroup(parent, group.getId());

					for (Iterator items = group.getContainedItems().iterator(); items.hasNext();) {
						final Object i = items.next();
						if (i instanceof Contact) {
							final Contact contact = (Contact) i;
							final User user = new User(createIDFromName(contact.getId()), contact.getNickName());
							if (findRosterEntry(rosterGroup, user) == null) {
								// Now create new roster entry
								new RosterEntry(rosterGroup, user, new Presence(IPresence.Type.UNAVAILABLE,
										IPresence.Type.UNAVAILABLE.toString(), IPresence.Mode.AWAY));
							}
						}
					}

					// Only add localGrp if not already in list
					if (!groupFound)
						result.add(rosterGroup);
				}
			}
		}

		return (IRosterItem[]) result.toArray(new IRosterItem[] {});
	}

	private RosterEntry findRosterEntry(RosterGroup rosterGroup, IUser user) {
		if (rosterGroup != null)
			return findRosterEntry(rosterGroup.getEntries(), user);

		return findRosterEntry(roster.getItems(), user);
	}

	private RosterEntry findRosterEntry(Collection entries, IUser user) {
		for (final Iterator i = entries.iterator(); i.hasNext();) {
			final Object o = i.next();
			if (o instanceof RosterEntry) {
				final RosterEntry entry = (RosterEntry) o;
				if (entry.getUser().getID().equals(user.getID()))
					return entry;
			}
		}
		return null;
	}

	protected RosterGroup findRosterGroup(Object parent, String grp) {
		final Collection items = roster.getItems();
		for (final Iterator i = items.iterator(); i.hasNext();) {
			final IRosterItem item = (IRosterItem) i.next();
			if (item.getName().equals(grp))
				return (RosterGroup) item;
		}
		return null;
	}

	protected void trace(String msg) {
		OSCARPlugin.log(msg);
	}

	protected void trace(String msg, Throwable t) {
		OSCARPlugin.log(msg, t);
	}

	protected void traceAndThrowECFException(String msg, Throwable t) throws ECFException {
		OSCARPlugin.log(msg, t);
		throw new ECFException(msg, t);
	}

	private OSCARConnection getConnectionOrThrowIfNull() throws IOException {
		final OSCARConnection conn = container.getOSCARConnection();
		if (conn == null)
			throw new IOException(Messages.OSCAR_CONNECTION_EXCEPTION_NO_CONNECTED);
		return conn;
	}

	IPresenceSender rosterPresenceSender = new IPresenceSender() {
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.IPresenceSender#sendPresenceUpdate(org.eclipse.ecf.core.identity.ID,
		 * 			org.eclipse.ecf.presence.IPresence)
		 */
		public void sendPresenceUpdate(ID toID, IPresence presence) throws ECFException {
			// TODO
			/*try {
				getConnectionOrThrowIfNull().sendPresenceUpdate(toID, createPresence(presence));
			} catch (final IOException e) {
				traceAndThrowECFException(Messages.OSCAR_ROSTER_EXCEPTION_SEND_PRESENCE_UPDATE, e);
			}*/
		}

	};

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.presence.roster.AbstractRosterManager#getPresenceSender()
	 */
	public IPresenceSender getPresenceSender() {
		return rosterPresenceSender;
	}

	IRosterSubscriptionSender rosterSubscriptionSender = new IRosterSubscriptionSender() {
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.roster.IRosterSubscriptionSender#sendRosterAdd(java.lang.String, java.lang.String,
		 * 			java.lang.String[])
		 */
		public void sendRosterAdd(String uin, String nick, String[] groups) throws ECFException {
			try {
				if (groups == null || groups.length < 1)
					throw new ECFException(Messages.OSCAR_ROSTER_EXCEPTION_GROUP_NULL);

				getConnectionOrThrowIfNull().sendRosterAdd(uin, groups[0]);
			} catch (final Exception e) {
				traceAndThrowECFException(Messages.OSCAR_ROSTER_EXCEPTION_SEND_ADD, e);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.roster.IRosterSubscriptionSender#sendRosterRemove(
		 * 			org.eclipse.ecf.core.identity.ID)
		 */
		public void sendRosterRemove(ID userID) throws ECFException {
			try {
				if (!(userID instanceof OSCARID))
					throw new ECFException(Messages.OSCARID_EXCEPTION_INVALID_UID);

				final OSCARID oscarID = (OSCARID) userID;
				getConnectionOrThrowIfNull().sendRosterRemove(oscarID.getUin());
			} catch (final Exception e) {
				traceAndThrowECFException(Messages.OSCAR_ROSTER_EXCEPTION_SEND_REMOVE, e);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.presence.roster.AbstractRosterManager#getRosterSubscriptionSender()
	 */
	public IRosterSubscriptionSender getRosterSubscriptionSender() {
		return rosterSubscriptionSender;
	}
}
