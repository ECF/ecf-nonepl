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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.rest.IRestCall;
import org.eclipse.ecf.remoteservice.rest.RestContainer;
import org.eclipse.ecf.remoteservice.rest.RestService;
import org.eclipse.ecf.remoteservice.rest.RestServiceRegistration;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.rest.resource.rss.RssParser.RssFeed;
import org.eclipse.ecf.remoteservice.rest.util.DeleteRestCall;
import org.eclipse.ecf.remoteservice.rest.util.GetRestCall;
import org.eclipse.ecf.remoteservice.rest.util.PostRestCall;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TwitterTest extends TestCase {
	
	private static final String URL_TWITTER = "http://twitter.com";
	private RestContainer container;
	private IRemoteServiceRegistration registration;
	
	protected void setUp() throws Exception {
		if(container == null){
			ID restId = IDFactory.getDefault().createID(RestNamespace.NAME, URL_TWITTER);
			container = (RestContainer) ContainerFactory.getDefault().createContainer(RestContainer.NAME, restId);
		}
	}
	
	public void testCreation() {
		assertNotNull(container);
	}
	
	public void testRegisterService() {
		registration = container.registerRemoteService(new String[]{IRemoteService.class.getName()}, new RestService(), null);
		assertTrue(registration instanceof RestServiceRegistration);		
	}
	
	public void testGetPublicTimeline() {
		final String publicTimeline = "statuses/public_timeline.xml";
		RestService remoteService = registerService();
		try {
			IRestCall call = new GetRestCall(new URI(publicTimeline), "ecf.rest.resource.xml", null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof Document);	
		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testGetUserTimeline() {
		final String userTimeline = "/statuses/user_timeline.xml";
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		
		RestService remoteService = registerService();		
		try {
			IRestCall call = new GetRestCall(new URI(userTimeline), "ecf.rest.resource.xml", new Object[]{"count=1"}, null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof Document);
		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testUpdateStatus() {
		final String updateStatus = "statuses/update.xml";
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		
		RestService remoteService = registerService();
		assertTrue(remoteService instanceof RestService);
		try {
			Random ran = new Random();			
			Object[] params = new Object[]{"status=a message from REST-ECF" + ran.nextInt()};
			IRestCall call = new PostRestCall(new URI(updateStatus), "ecf.rest.resource.xml", params, null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof Document);

		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
	}
	
	public void testDeleteStatus() {
		String statusId = getStatusId();
		assertNotNull(statusId);
		final String deleteStatus = "statuses/destroy/"+statusId+".xml";
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		
		RestService remoteService = registerService();
		assertTrue(remoteService instanceof RestService);
		try {
			IRestCall call = new DeleteRestCall(new URI(deleteStatus), "ecf.rest.resource.xml", null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof Document);

		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
		
	}

	private String getStatusId() {
		final String updateStatus = "statuses/update.xml";
		IConnectContext context = ConnectContextFactory.createUsernamePasswordConnectContext("eclipsedummy", "eclipse");
		container.setConnectContextForAuthentication(context);
		
		RestService remoteService = registerService();
		assertTrue(remoteService instanceof RestService);
		try {
			Random ran = new Random();			
			Object[] params = new Object[]{"status=a message to delete from REST-ECF" + ran.nextInt()};
			IRestCall call = new PostRestCall(new URI(updateStatus), "ecf.rest.resource.xml", params, null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof Document);
			Document dom = (Document)response;	
			Element status = dom.getDocumentElement();
			status.normalize();
			NodeList childNodes = status.getChildNodes();
			String id = null;
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);				
				String nodeName = item.getNodeName();
				if(nodeName.equals("id")) {
					item.normalize();
					return item.getFirstChild().getNodeValue();					
				}
			}
			assertNotNull(id);

		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
		return null;
	}
	
	public void testGetJsonPublicTimeline() {		
		final String publicTimeline = "statuses/public_timeline.json";
		RestService remoteService = registerService();
		assertTrue(remoteService instanceof RestService);
		try {
			IRestCall call = new GetRestCall(new URI(publicTimeline), "ecf.rest.resource.json.org", null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof JSONArray);
		} catch (ECFException e) {
			fail();
		}  catch (URISyntaxException e) {
			fail();
		} 
	}
	
	public void testGetRssPublicTimeline() {		
		final String publicTimeline = "statuses/public_timeline.rss";
		RestService remoteService = registerService();
		assertTrue(remoteService instanceof RestService);		
		try {
			IRestCall call = new GetRestCall(new URI(publicTimeline), "ecf.rest.resource.rss", null, 10000);
			Object response = remoteService.callSync(call);
			assertTrue(response instanceof RssFeed);
			
		} catch (ECFException e) {
			fail();
		} catch (URISyntaxException e) {
			fail();
		}
	}

	private RestService registerService() {					
		IRemoteService remoteService = container.getRemoteService(IRemoteService.class.getName());
		if(remoteService == null) {
			registration = container.registerRemoteService(new String[]{IRemoteService.class.getName()}, new RestService(), null);
			return (RestService) container.getRemoteService(IRemoteService.class.getName());
		}
		return (RestService) remoteService;
	}

}
