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
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Profile;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.connector.ConnectorException;

/**
 *
 */
public class SkypeContainer extends ClientSOContainer implements IContainer, IPresenceContainerAdapter {

	SkypeAccountManager accountManager = null;
	SkypeRosterManager rosterManager = null;
	SkypeChatManager chatManager = null;
	SkypeChatRoomManager chatRoomManager = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#dispose()
	 */
	public void dispose() {
		super.dispose();
		if (accountManager != null) {
			accountManager.dispose();
			accountManager = null;
		}
		if (rosterManager != null) {
			rosterManager.dispose();
			rosterManager = null;
		}
		if (chatManager != null) {
			chatManager.dispose();
			chatManager = null;
		}
		if (chatRoomManager != null) {
			chatRoomManager.dispose();
			chatRoomManager = null;
		}
	}
	/**
	 * @param config
	 */
	public SkypeContainer() throws SkypeException, ConnectorException {
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
		Profile skypeProfile = Skype.getProfile();
		SkypeUserID userID = new SkypeUserID(
				skypeProfile.getId());
		String fullName = skypeProfile.getFullName();
		fullName = (fullName == null || fullName.equals(""))?userID.getUser():fullName;
		org.eclipse.ecf.core.user.User user = new org.eclipse.ecf.core.user.User(userID, fullName);

		accountManager = new SkypeAccountManager(this,skypeProfile,userID,user);
		rosterManager = new SkypeRosterManager(this,skypeProfile,userID,user);
		chatManager = new SkypeChatManager();
		chatRoomManager = new SkypeChatRoomManager();

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

}
