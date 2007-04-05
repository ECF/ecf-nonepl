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

import org.eclipse.ecf.core.IContainable;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;

/**
 *
 */
public class SkypeContainer extends ClientSOContainer implements IContainer, IPresenceContainerAdapter, IContainable {

	IAccountManager accountManager = null;
	IRosterManager rosterManager = null;
	IChatManager chatManager = null;
	IChatRoomManager chatRoomManager = null;
	
	/**
	 * @param config
	 */
	public SkypeContainer() {
		super(new ISharedObjectContainerConfig() {

			public Object getAdapter(Class clazz) {
				return null;
			}

			public Map getProperties() {
				return new HashMap();
			}

			public ID getID() {
				try {
					return IDFactory.getDefault().createGUID();
				} catch (IDCreateException e) {
					return null;
				}
			}});
		accountManager = new SkypeAccountManager(this);
		rosterManager = new SkypeRosterManager(this);
		chatManager = new SkypeChatManager(this);
		chatRoomManager = new SkypeChatRoomManager(this);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getAccountManager()
	 */
	public IAccountManager getAccountManager() {
		return accountManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getChatManager()
	 */
	public IChatManager getChatManager() {
		return chatManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getChatRoomManager()
	 */
	public IChatRoomManager getChatRoomManager() {
		return chatRoomManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getRosterManager()
	 */
	public IRosterManager getRosterManager() {
		return rosterManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#createConnection(org.eclipse.ecf.core.identity.ID, java.lang.Object)
	 */
	protected ISynchAsynchConnection createConnection(ID remoteSpace,
			Object data) throws ConnectionCreateException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainable#getContainers()
	 */
	public IContainer[] getContainers() {
		return new IContainer[] { this };
	}

}
