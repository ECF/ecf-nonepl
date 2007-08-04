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
import org.eclipse.ecf.internal.provider.yahoo.Activator;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.provider.yahoo.identity.YahooID;
import org.eclipse.ecf.provider.yahoo.util.YahooSessionListener;

import ymsg.network.AccountLockedException;
import ymsg.network.LoginRefusedException;
import ymsg.network.Session;
import ymsg.network.StatusConstants;

public class YahooContainer extends AbstractContainer {

	/** Represents the YMSG session object provided by the ymsg library */
	private Session session;

	/** Entity identifier within the ECF namespace for the local Container entity */
	private ID localID;

	/**
	 * Entity identifier within the Yahoo! namespace for the target server or
	 * group entity
	 */
	private YahooID targetYahooID;

	/**
	 * Presence container instance used for adapting this container to a present
	 * container
	 */
	private YahooPresenceContainer presenceContainer;

	public YahooContainer(ID id) {
		this.localID = id;
		session = new Session();
		presenceContainer = new YahooPresenceContainer(this,session);
	}

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		String password = getPasswordFromConnectContext(connectContext);
		fireContainerEvent(new ContainerConnectingEvent(this.getID(), targetID,
				connectContext));
		this.targetYahooID = (YahooID) targetID;
		try {
			session.setStatus(StatusConstants.STATUS_AVAILABLE);
			session.login(targetYahooID.getUsername(), password);
		} catch (AccountLockedException e) {
			throw new ContainerConnectException("Account locked", e);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Illegal state", e);
		} catch (LoginRefusedException e) {
			throw new ContainerConnectException("Login refused", e);
		} catch (IOException e) {
			throw new ContainerConnectException("Unknown IOException", e);
		}
		session.addSessionListener(new YahooSessionListener(this,
				presenceContainer));
		presenceContainer.populateRoster(targetYahooID, session.getGroups());
		fireContainerEvent(new ContainerConnectedEvent(this.getID(), targetID));
	}

	public ID getConnectedID() {
		return this.targetYahooID;
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				Activator.NAMESPACE_IDENTIFIER);
	}

	public synchronized void disconnect() {
		if (session != null) {
			fireContainerEvent(new ContainerDisconnectingEvent(this.getID(),
					targetYahooID));
			try {
				session.logout();
			} catch (Exception e) {
			} finally {
				session = null;
			}
			// notify listeners
			fireContainerEvent(new ContainerDisconnectedEvent(this.getID(),
					targetYahooID));
		}
		targetYahooID = null;
	}

	public Object getAdapter(Class serviceType) {
		if (serviceType.equals(IPresenceContainerAdapter.class)) {
			return presenceContainer;
		}
		return super.getAdapter(serviceType);
	}

	public void dispose() {
		disconnect();
		super.dispose();
	};

	public ID getID() {
		return this.localID;
	}

}
