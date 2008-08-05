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

package org.eclipse.ecf.provider.twitter.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.ecf.provider.twitter.identity.TwitterID;
import org.eclipse.ecf.provider.twitter.identity.TwitterNamespace;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 */
public class TwitterContainer extends AbstractContainer implements IPresenceService {

	public final static int MAX_STATUS_LENGTH = 140;

	private final Object connectLock = new Object();

	private final TwitterChatManager chatManager;
	private final TwitterRosterManager rosterManager;
	private final TwitterAccountManager accountManager;

	TwitterID targetID;
	Twitter twitter;

	private final ID containerID;

	public TwitterContainer(ID containerID) {
		this.containerID = containerID;
		this.chatManager = new TwitterChatManager(this);
		this.rosterManager = new TwitterRosterManager(this);
		this.accountManager = new TwitterAccountManager(this);
	}

	Twitter getTwitter() throws ECFException {
		synchronized (connectLock) {
			if (twitter == null)
				throw new ECFException("Not connected");
			return twitter;
		}
	}

	TwitterID getTargetID() {
		return targetID;
	}

	void sendTwitterUpdate(String body) throws ECFException {
		try {
			this.getTwitter().update(body);
		} catch (final TwitterException e) {
			throw new ECFException(e);
		}
	}

	void sendTwitterMessage(String to, String body) throws ECFException {
		try {
			this.getTwitter().sendDirectMessage(to, body);
		} catch (final TwitterException e) {
			throw new ECFException(e);
		}
	}

	List getTwitterFriends() throws ECFException {
		try {
			return this.getTwitter().getFriends();
		} catch (final TwitterException e) {
			throw new ECFException(e);
		}
	}

	TwitterUser[] getTwitterUsersFromFriends() throws ECFException {
		final List friends = getTwitterFriends();
		final List result = new ArrayList();
		for (final Iterator i = friends.iterator(); i.hasNext();) {
			final twitter4j.User twitterUser = (twitter4j.User) i.next();
			final TwitterUser tu = new TwitterUser(twitterUser);
			result.add(tu);
		}
		return (TwitterUser[]) result.toArray(new TwitterUser[] {});
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
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getRosterManager()
	 */
	public IRosterManager getRosterManager() {
		return rosterManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
	public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
		fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));
		final String password = getPasswordFromConnectContext(connectContext);
		synchronized (connectLock) {
			if (twitter != null)
				throw new ContainerConnectException("Already connected");
			if (targetID == null)
				throw new ContainerConnectException("targetID cannot be null");
			if (!(targetID instanceof TwitterID))
				throw new ContainerConnectException("targetID of wrong type");
			try {
				this.twitter = new Twitter(targetID.getName(), password);
				if (!this.twitter.verifyCredentials())
					throw new ContainerConnectException("Cound not authenticate");
				this.targetID = (TwitterID) targetID;
				// Create user
				final User localUser = new User(targetID, this.twitter.getUserId() + " [Twitter]");
				rosterManager.setUser(localUser);
				final TwitterUser[] twitterUsers = getTwitterUsersFromFriends();
				rosterManager.addTwitterFriendsToRoster(twitterUsers);
			} catch (final ContainerConnectException e) {
				this.targetID = null;
				throw e;
			} catch (final Exception e) {
				this.targetID = null;
				throw new ContainerConnectException(e);
			}
		}
		fireContainerEvent(new ContainerConnectedEvent(getID(), this.targetID));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
	public void disconnect() {
		final ID id = this.targetID;
		fireContainerEvent(new ContainerDisconnectingEvent(getID(), id));
		synchronized (connectLock) {
			if (twitter != null) {
				twitter = null;
				targetID = null;
			}
		}
		fireContainerEvent(new ContainerDisconnectedEvent(getID(), id));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(TwitterNamespace.NAME);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
	public ID getConnectedID() {
		return targetID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return containerID;
	}

	/**
	 * @param status
	 * @throws ECFException 
	 */
	public void sendStatusUpdate(String status) throws ECFException {
		chatManager.getChatMessageSender().sendChatMessage(targetID, status);
	}

}
