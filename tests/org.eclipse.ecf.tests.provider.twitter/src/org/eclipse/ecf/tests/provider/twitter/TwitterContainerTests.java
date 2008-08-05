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

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
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
}