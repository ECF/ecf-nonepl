/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Pavel Samolisov - adapted for OSCAR container
 *****************************************************************************/

package org.eclipse.ecf.tests.provider.oscar.api.remoteservice;

import java.util.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.tests.provider.oscar.OSCAR;
import org.eclipse.ecf.tests.remoteservice.AbstractRemoteServiceTest;
import org.osgi.framework.InvalidSyntaxException;

public class RemoteServiceTest extends AbstractRemoteServiceTest {

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.presence.AbstractPresenceTestCase#getClientContainerName()
	 */
	protected String getClientContainerName() {
		return OSCAR.CONTAINER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#getServerConnectID(int)
	 */
	protected ID getServerConnectID(int client) {
		try {
			return IDFactory.getDefault().createID(getClient(client).getConnectNamespace(), getUsername(client));
		} catch (final IDCreateException e) {
			throw new RuntimeException("Сannot create connect id for client " + 1, e); //$NON-NLS-1$
		}
	}

	protected IRemoteServiceReference[] getRemoteServiceReferences(IRemoteServiceContainerAdapter adapter,
			String clazz, String filter) {
		try {
			return adapter.getRemoteServiceReferences(new ID[] {getClient(0).getConnectedID()}, clazz, filter);
		} catch (final InvalidSyntaxException e) {
			fail("Should not happen"); //$NON-NLS-1$
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		setClientCount(2);
		clients = createClients();
		for (int i = 0; i < clientCount; i++) {
			connectClient(i);
			sleep(3000);
		}
		setupRemoteServiceAdapters();
		addRemoteServiceListeners();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		cleanUpClients();
		super.tearDown();
	}

	/**
	 * Customize properties - add recepient (<code>getClient(1)</code>). We can send messages
	 * via OSCAR only for specific recepient.
	 */
	protected Dictionary customizeProperties(Dictionary props) {
		Dictionary result = new Hashtable();
		if (props != null) {
			for (Enumeration en = props.keys(); en.hasMoreElements();) {
				Object key = en.nextElement();
				result.put(key, props.get(key));
			}
		}

		result.put(Constants.SERVICE_REGISTRATION_TARGETS, getClient(1).getConnectedID());
		return result;
	}
}
