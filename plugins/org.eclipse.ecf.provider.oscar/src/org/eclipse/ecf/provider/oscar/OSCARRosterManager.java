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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.oscar.Messages;
import org.eclipse.ecf.internal.provider.oscar.OSCARPlugin;
import org.eclipse.ecf.internal.provider.oscar.icqlib.OSCARConnection;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.ecf.provider.oscar.identity.OSCARID;

public class OSCARRosterManager extends AbstractRosterManager {

	private final List presenceListeners = new ArrayList();

	private OSCARContainer container = null;

	public OSCARRosterManager(Roster roster, OSCARContainer container) {
		super(roster);
		this.container = container;
	}

	public void notifySubscriptionListener(ID fromID, IPresence presence) {
		this.fireSubscriptionListener(fromID, presence.getType());
	}

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
}
