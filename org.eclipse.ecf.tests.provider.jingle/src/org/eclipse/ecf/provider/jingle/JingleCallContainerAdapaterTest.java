/*******************************************************************************
 * Copyright (c) 2007 Moritz Post and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.ecf.provider.jingle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPNamespace;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;
import org.eclipse.ecf.tests.internal.provider.jingle.Config;
import org.eclipse.ecf.tests.internal.provider.jingle.XMPPContainerFactory;
import org.eclipse.osgi.util.NLS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for the {@link JingleCallSessionContainerAdapater}.
 * 
 * @author Moritz Post
 */
public class JingleCallContainerAdapaterTest {

	private JingleCallSessionContainerAdapater jingleCallContainerAdapater;

	/** Flag to denote picked up calls. */
	private boolean callPickedUp;

	/**
	 * The timeout period until a made call has to be accepted by the remote
	 * site.
	 */
	private static final int CALL_TIMEOUT = 30000;

	/**
	 * Denotes how often it should be checked if a call has been picked up
	 * remotely.
	 */
	private static final int CHECK_INTERVAL = 500;

	@Before
	public void setUp() throws Exception, JingleException {
		XMPPContainer xmppContainer = XMPPContainerFactory.getInstance().getXMPPContainer();
		jingleCallContainerAdapater = JingleCallSessionContainerAdapater.getInstance(xmppContainer);
	}

	@Test
	public void testGetReceiverNamespace() throws Exception {
		assertTrue(jingleCallContainerAdapater.getReceiverNamespace() instanceof Namespace);
	}

	@Test
	public void sendCallRequestIDArrayICallSessionListenerMap() throws IDCreateException {

		IIDFactory idFactory = IDFactory.getDefault();
		XMPPNamespace xmppNamespace = (XMPPNamespace) idFactory.getNamespaceByName(NLS.bind(
				Config.XMPP_NAMESPACE, null));
		XMPPID xmppId1 = (XMPPID) idFactory.createID(xmppNamespace, "user1@server");
		XMPPID xmppId2 = (XMPPID) idFactory.createID(xmppNamespace, "user2@server");
		ID[] ids = new ID[] { xmppId1, xmppId2 };

		// check invalid behavior

		try {
			jingleCallContainerAdapater.sendCallRequest(ids, null, null);
			fail("Should have thrown an Exception");
		} catch (CallException e) {
		}

		ids = new ID[] {};
		try {
			jingleCallContainerAdapater.sendCallRequest(ids, null, null);
			fail("Should have thrown an Exception");
		} catch (Exception e) {
		}

		// correct setup
		ids = new ID[] { xmppId1 };
		try {
			jingleCallContainerAdapater.sendCallRequest(ids, null, null);
			fail("An exception should have been thrown");
		} catch (CallException e) {
			// is this the expected exception?
			if (!e.getMessage().equals("The listener may not be null")) {
				fail("The Wrong exception has been returned. Got: " + e.getLocalizedMessage());
			}
		}
	}

	@Test
	@Ignore
	public void sendCallRequestIDICallSessionListenerMap() throws IDCreateException, CallException {

		ICallSessionListener callSessionListener = new ICallSessionListener() {

			@Override
			public void handleCallSessionEvent(ICallSessionEvent event) {
				ICallSession callSession = event.getCallSession();
				// if call got picked up end session
				if (callSession.getState() == CallSessionState.ACTIVE) {
					try {
						callSession.sendTerminate();
					} catch (CallException e) {
						e.printStackTrace();
					}
					JingleCallContainerAdapaterTest.this.callPickedUp = true;
				}
			}

		};

		Namespace xmppNamespace = IDFactory.getDefault().getNamespaceByName(
				NLS.bind(Config.XMPP_NAMESPACE, null));
		XMPPID xmppId = (XMPPID) xmppNamespace.createInstance(new String[] { NLS.bind(
				Config.XMPP_ACCOUNT_REMOTE_USER, null) });

		// make the test call (The other side has to pick up within 10 sec.)
		jingleCallContainerAdapater.sendCallRequest(xmppId, callSessionListener, null);

		int timeout = 0;

		// let the executing thread wait until the placed call is picked up or a
		// timeout has been reached
		try {
			while (!this.callPickedUp && timeout < CALL_TIMEOUT) {
				Thread.sleep(CHECK_INTERVAL);
				timeout += CHECK_INTERVAL;
			}
			if (!this.callPickedUp) {
				fail("The call request made was not answered.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getAdapter() {
		assertNull(jingleCallContainerAdapater.getAdapter(null));
		assertTrue(XMPPContainer.class.isInstance(jingleCallContainerAdapater
				.getAdapter(XMPPContainer.class)));
	}

	@Test
	public void testGetUserID() throws IDCreateException {
		// create id for a user to login
		IIDFactory idFactory = IDFactory.getDefault();
		Namespace xmppNamespace = idFactory.getNamespaceByName(NLS
				.bind(Config.XMPP_NAMESPACE, null));
		XMPPID id = (XMPPID) idFactory.createID(xmppNamespace, NLS.bind(Config.XMPP_ACCOUNT_USER,
				null));
		assertEquals(id.getFQName(), jingleCallContainerAdapater.getUserID().getFQName());
	}
}
