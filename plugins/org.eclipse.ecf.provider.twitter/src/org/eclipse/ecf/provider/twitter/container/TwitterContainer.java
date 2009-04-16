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
import java.util.Date;
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
import org.eclipse.ecf.internal.provider.twitter.search.TweetSearch;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.search.ICriteria;
import org.eclipse.ecf.presence.search.IRestriction;
import org.eclipse.ecf.presence.search.ISearch;
import org.eclipse.ecf.presence.search.IUserSearchListener;
import org.eclipse.ecf.presence.search.IUserSearchManager;
import org.eclipse.ecf.presence.search.UserSearchException;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.ecf.provider.twitter.identity.TwitterID;
import org.eclipse.ecf.provider.twitter.identity.TwitterNamespace;
import org.eclipse.ecf.provider.twitter.search.ITweetSearch;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserWithStatus;

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

	public List getTwitterFriends() throws ECFException {
		try {
			return this.getTwitter().getFriends();
		} catch (final TwitterException e) {
			throw new ECFException("Exception getting twitter friends", e);
		}
	}

	Date lastFriendsTimelineDate;
	List lastFriendsStatuses = new ArrayList();

	public List getTwitterFriendsTimeline() throws ECFException {
		try {
			// XXX this seems to *always* return the list of all statuses in the last 24 hours
			// Even if a 'since' date is provided, it returns all statuses within last 24 hours...strange
			//final List results = (lastFriendsTimelineDate == null) ? this.getTwitter().getFriendsTimeline(targetID.getName()) : this.getTwitter().getFriendsTimeline(targetID.getName(), lastFriendsTimelineDate);
			// XXX in the mean time, we'll simply keep track/hold onto old statuses...and do a manual
			// diff
			final List results = getTwitterFriendsTimelineDiff();
			lastFriendsTimelineDate = new Date();
			return results;
		} catch (final TwitterException e) {
			throw new ECFException("Exception getting friends timeline", e);
		}
	}

	List getTwitterFriendsTimelineDiff() throws TwitterException, ECFException {
		final List twitterList = this.getTwitter().getFriendsTimeline();
		return diffFriendsTimeline(twitterList);
	}

	List diffFriendsTimeline(List twitterList) {
		for (final Iterator i = twitterList.iterator(); i.hasNext();) {
			final Status s = (Status) i.next();
			if (lastFriendsContains(s)) {
				i.remove();
			}
		}
		lastFriendsStatuses.addAll(twitterList);
		return twitterList;
	}

	boolean lastFriendsContains(Status newStatus) {
		for (final Iterator i = lastFriendsStatuses.iterator(); i.hasNext();) {
			final Status oldStatus = (Status) i.next();
			if (oldStatus.getId() == newStatus.getId())
				return true;
		}
		return false;
	}

	public TwitterUser[] getTwitterUsersFromFriends() throws ECFException {
		final List friends = getTwitterFriends();
		final List result = new ArrayList();
		for (final Iterator i = friends.iterator(); i.hasNext();) {
			final twitter4j.User twitterUser = (twitter4j.User) i.next();
			final TwitterUser tu = new TwitterUser(twitterUser);
			result.add(tu);
		}
		return (TwitterUser[]) result.toArray(new TwitterUser[] {});
	}
	
	/**
	 * I added this method so that I could get myself as a TwitterUser
	 * @return
	 * @throws ECFException
	 */
	public TwitterUser getConnectedUser() throws ECFException
	{
		try {
			UserWithStatus user = getTwitter().getUserDetail(twitter.getUserId());
			
			TwitterUser twitterUser = new TwitterUser(user);
			return twitterUser;
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
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
				// Set local user in chat manager...this creates roster
				rosterManager.setUser(localUser);
				// Then get twitter friends from server
				refreshTwitterFriends();
				// Then get friend's status from server
				refreshTwitterStatuses();
				// Start refresh thread
				startAutoRefresher();
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

	private AutoRefreshThread autoRefreshThread;

	class AutoRefreshThread extends Thread {

		int refreshDelay = 60000;
		boolean done = false;
		Object lock = new Object();

		public AutoRefreshThread(String name) {
			setName(name);
			setDaemon(true);
		}

		public void run() {
			synchronized (lock) {
				while (!done) {
					try {
						lock.wait(refreshDelay);
						if (getTwitter() == null)
							return;
						refreshTwitterFriends();
						refreshTwitterStatuses();
					} catch (final ECFException e) {
						// XXX todo...this would be caused by some twitter failure...should probably
						// disconnect or leave up to user
					} catch (final InterruptedException e) {
						// ignore
					}
				}
			}
		}

		public void finish() {
			synchronized (lock) {
				this.done = true;
				lock.notify();
			}
		}
	}

	void startAutoRefresher() {
		if (autoRefreshThread == null) {
			autoRefreshThread = new AutoRefreshThread("Twitter AutoRefresh");
			autoRefreshThread.start();
		}
	}

	void stopAutoRefresher() {
		if (autoRefreshThread != null) {
			autoRefreshThread.finish();
			autoRefreshThread = null;
		}
	}

	void refreshTwitterFriends() throws ECFException {
		final TwitterUser[] twitterUsers = getTwitterUsersFromFriends();
		// Add to roster
		rosterManager.addTwitterFriendsToRoster(twitterUsers);
	}

	public void refreshTwitterStatuses() throws ECFException {
		// Then get friend's status messages
		final List statuses = getTwitterFriendsTimeline();
		// Add notify message listeners
		chatManager.handleStatusMessages((Status[]) statuses.toArray(new Status[] {}));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
	public void disconnect() {
		final ID id = this.targetID;
		fireContainerEvent(new ContainerDisconnectingEvent(getID(), id));
		synchronized (connectLock) {
			stopAutoRefresher();
			lastFriendsStatuses.clear();
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

	/**
	 * 
	 * @return the object to match the tweets {@link ITweetSearch}
	 * @throws ECFException
	 */
	public ITweetSearch getTweetSearch() throws ECFException{
		return new TweetSearch(getTwitter());
	}
	
	public IUserSearchManager getUserSearchManager() {
		return new IUserSearchManager(){

			public ICriteria createCriteria() {
				// TODO Auto-generated method stub
				return null;
			}

			public IRestriction createRestriction() {
				// TODO Auto-generated method stub
				return null;
			}

			public String[] getUserPropertiesFields() throws ECFException {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			public ISearch search(ICriteria criteria)
					throws UserSearchException {
				// TODO Auto-generated method stub
				return null;
			}

			public void search(ICriteria criteria, IUserSearchListener listener) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}


}
