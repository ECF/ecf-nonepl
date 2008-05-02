/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.aol.acc.container;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.aol.acc.identity.AIMID;
import org.eclipse.ecf.provider.aol.acc.identity.AIMNamespace;

import com.aol.acc.AccClientInfo;
import com.aol.acc.AccException;
import com.aol.acc.AccPreferencesHook;
import com.aol.acc.AccSession;

/**
 *
 */
public class AIMContainer extends AbstractContainer implements IPresenceContainerAdapter {

	private static final String key = System.getProperty("AIMKEY", "");

	private AccSession session;

	private final ID id;
	private AIMID targetID;

	private AccEventsListener eventsListener;

	private PollThread poll;

	private class PollThread extends Thread {

		private boolean running = true;

		public void run() {
			while (running) {
				try {
					AccSession.pump(50);
				} catch (final Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(50);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

		public void dispose() {
			running = false;
		}
	}

	/**
	 * @param id
	 */
	public AIMContainer(ID id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.BaseContainer#getConnectedID()
	 */
	public ID getConnectedID() {
		return targetID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.BaseContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
	public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
		if (!(targetID instanceof AIMID))
			throw new ContainerConnectException("targetID not of correct type");
		final AIMID tID = (AIMID) targetID;

		final String password = super.getPasswordFromConnectContext(connectContext);

		fireContainerEvent(new ContainerConnectingEvent(getID(), tID));

		try {
			synchronized (this) {
				session = new AccSession();
				eventsListener = new AccEventsListener(this);
				session.setEventListener(eventsListener);
				// set key
				final AccClientInfo info = session.getClientInfo();
				info.setDescription(key);

				// set screen name
				session.setIdentity(tID.getName());

				// setup file sharing
				session.setPrefsHook(new Prefs());

				session.signOn(password);

				targetID = tID;

				poll = new PollThread();
			}
			fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));
			poll.start();
		} catch (final AccException e) {
			throw new ContainerConnectException("AIM connect exception", e);
		} catch (final Exception e) {
			throw new ContainerConnectException("AIM connect exception", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.BaseContainer#disconnect()
	 */
	public void disconnect() {
		fireContainerEvent(new ContainerDisconnectingEvent(getID(), getConnectedID()));
		final ID tID = getConnectedID();
		synchronized (this) {
			if (session != null) {
				poll.dispose();
				poll = null;
				session.removeEventListener();
				eventsListener = null;
				try {
					session.signOff();
				} catch (final AccException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session = null;
			}
		}
		fireContainerEvent(new ContainerDisconnectedEvent(getID(), tID));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getAccountManager()
	 */
	public IAccountManager getAccountManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getChatManager()
	 */
	public IChatManager getChatManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getChatRoomManager()
	 */
	public IChatRoomManager getChatRoomManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getRosterManager()
	 */
	public IRosterManager getRosterManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		// TODO Auto-generated method stub
		return IDFactory.getDefault().getNamespaceByName(AIMNamespace.NAME);
	}

	public class Prefs extends AccPreferencesHook {

		HashMap<String, String> map = new HashMap<String, String>();

		public String getValue(String specifier) {
			return (map.get(specifier));
		}

		public String getDefaultValue(String specifier) {
			return null;
		}

		public void setValue(String specifier, String value) {
			map.put(specifier, value);
		}

		public void reset(String specifier) {
			map.put(specifier, null);
		}

		public String[] getChildSpecifiers(String specifier) {
			final Vector v = new Vector();
			for (final String s : map.keySet()) {
				if (s.startsWith(specifier) && !s.equals(specifier)) {
					v.add(s);
				}
			}

			if (v.size() > 0) {
				return (String[]) v.toArray(new String[0]);
			} else {
				return null;
			}

		}
	}

}
