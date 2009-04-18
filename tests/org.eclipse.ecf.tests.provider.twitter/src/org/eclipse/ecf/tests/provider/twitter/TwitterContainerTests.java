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

package org.eclipse.ecf.tests.provider.twitter;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.ecf.provider.twitter.container.TwitterContainer;
import org.eclipse.ecf.provider.twitter.search.IResultTweetList;
import org.eclipse.ecf.provider.twitter.search.ITweetSearch;
import org.eclipse.ecf.provider.twitter.search.ITweetSearchCompleteEvent;
import org.eclipse.ecf.provider.twitter.search.ITweetSearchListener;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

/**
 *
 */
public class TwitterContainerTests extends ContainerAbstractTestCase {

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#getClientContainerName()
	 */
	protected String getClientContainerName() {
		return Twitter.CONTAINER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		clients = createClients();
		serverID = IDFactory.getDefault().createID(clients[0].getConnectNamespace(), getUsername(0));
		connectClients();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		disconnectClients();
		cleanUpClients();
	}

	public void testGetAdapter() throws Exception {
		final IContainer client = getClient(0);
		final IPresenceContainerAdapter presenceAdapter = (IPresenceContainerAdapter) client.getAdapter(IPresenceContainerAdapter.class);
		assertNotNull(presenceAdapter);
		final IRosterManager rosterManager = presenceAdapter.getRosterManager();
		final IRoster roster = rosterManager.getRoster();
		assertNotNull(roster);
	}
	
	public void testTimeLine() throws ECFException{
		final IContainer client = getClient(0);
		TwitterContainer container = (TwitterContainer)client.getAdapter(TwitterContainer.class);
		List l = container.getFriendsTimeline();
	}
	
	public void testTimeLineSince() throws ECFException, ParseException{
		final IContainer client = getClient(0);
		TwitterContainer container = (TwitterContainer)client.getAdapter(TwitterContainer.class);
		List l = container.getFriendsTimeline(2);
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			IStatus object = (IStatus) iterator.next();
			System.out.println(object.getText());
			
		}
	}

	public void testUserTimeLine() throws ECFException{
		System.out.println("");
		final IContainer client = getClient(0);
		TwitterContainer container = (TwitterContainer)client.getAdapter(TwitterContainer.class);
		List l = container.getUserTimeline();
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			IStatus object = (IStatus) iterator.next();
			System.out.println(object.getText());
			
		}
	}
	
	public void testTweetSearchSync() throws ECFException{
		final IContainer client = getClient(0);
		TwitterContainer container = (TwitterContainer)client.getAdapter(TwitterContainer.class);
		ITweetSearch search = container.getTweetSearch();
		assertNotNull(search);
		IResultTweetList results = search.search("eclipse");
		assertTrue(results.getTotal() > 0);		
	}
	
	public void testTweetSearchAsync() throws ECFException{
		ITweetSearchListener listener = new ITweetSearchListener(){
		
			public void handleTweetSearchEvent(ITweetSearchCompleteEvent event) {
				IResultTweetList result = event.getSearchResult();
				assertTrue(result.getTotal() > 0);
			}
		};
		
		final IContainer client = getClient(0);
		TwitterContainer container = (TwitterContainer)client.getAdapter(TwitterContainer.class);
		ITweetSearch search = container.getTweetSearch();
		assertNotNull(search);
		search.search("ecf", listener);
				
	}

	
}
