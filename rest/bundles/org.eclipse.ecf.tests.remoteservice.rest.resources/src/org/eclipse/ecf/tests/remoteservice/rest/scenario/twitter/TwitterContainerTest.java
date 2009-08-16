/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.tests.remoteservice.rest.scenario.twitter;

import junit.framework.TestCase;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.rest.RestService;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.rest.util.RestRemoteCall;
import org.json.JSONArray;

public class TwitterContainerTest extends TestCase {
	
	private static final String URL_TWITTER = "http://twitter.com";
	private TwitterContainer container;
	private IRemoteService remoteService;
	
	protected void setUp() throws Exception {
		if(container == null) {
			ID restId = IDFactory.getDefault().createID(RestNamespace.NAME, URL_TWITTER);
			container = new TwitterContainer(restId);
		}
	}	
	
	public void testContainerCreation() {		
		assertNotNull(container);
	}
	
	public void testGetService() {
		assertNotNull(container);
		remoteService = container.getRemoteService(RestService.class.getName());
		assertTrue(remoteService instanceof RestService);
	}
	
	public void testGetProxy() {
		remoteService = container.getRemoteService(RestService.class.getName());
		assertNotNull(remoteService);
		try {
			Object twitterService = remoteService.getProxy();
			assertTrue(twitterService instanceof TwitterService);
		} catch (ECFException e) {
			fail();
		}
	}
	
	public void testCallSync() {
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		remoteService = container.getRemoteService(RestService.class.getName());
		assertNotNull(remoteService);
		try {
			Object response = remoteService.callSync(new RestRemoteCall("getUserTimeline"));
			assertTrue(response instanceof JSONArray);
		} catch (ECFException e) {
			fail();
		}
	}
	
	public void testGetTimeline() {
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		remoteService = container.getRemoteService(RestService.class.getName());
		assertNotNull(remoteService);
		try {
			remoteService.callSync(new RestRemoteCall("getUserTimeline"));
			TwitterService service = (TwitterService) remoteService.getProxy();
			IUserTimeline timeline = service.getTimeline();
			IUserStatus[] userStatuses = timeline.getUserStatuses();
			// There is an Twitter bug which sometimes random the statuses. So
			// the count param seems to work inconsistently.
			assertEquals(2, userStatuses.length);
		} catch (ECFException e) {
			fail();
		}
	}

}
