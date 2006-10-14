/*******************************************************************************
 * Copyright (c) 2005 Ed Burnette, Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ed Burnette, Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.rcpchat.client;

import java.net.URISyntaxException;
import java.util.Hashtable;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainer;

public class Client {
	public static final String WORKSPACE_NAME = "<workspace>";
    public static final String GENERIC_CONTAINER_CLIENT_NAME = "ecf.generic.client";
	static Hashtable clients = new Hashtable();

	PresenceContainerUI presenceContainerUI = null;
	
	/**
	 * Create a new container instance, and connect to a remote server or group.
	 * 
	 * @param containerType the container type used to create the new container instance.  Must not be null.
	 * @param uri the uri that is used to create a targetID for connection.  Must not be null.
	 * @param nickname an optional String nickname.  May be null.
	 * @param connectData optional connection data.  May be null.
	 * @throws Exception
	 */
	public void createAndConnectClient(final String containerType, String uri,
			String nickname, final Object connectData)
			throws Exception {
		// Create the new container 
		final IContainer client = ContainerFactory
				.getDefault().createContainer(containerType);
		// Create the targetID 
		ID targetID = IDFactory.getDefault().createID(client.getConnectNamespace(), uri);
		// Setup username
		String username = setupUsername(targetID,nickname);
	     // Check for IPresenceContainer....if it is, setup presence UI, if not setup shared object container
		IPresenceContainer pc = (IPresenceContainer) client
				.getAdapter(IPresenceContainer.class);
		if (pc != null) {
			// Setup presence UI
			presenceContainerUI = new PresenceContainerUI(pc);
			presenceContainerUI.setup(client, targetID, username);
		} else throw new NullPointerException("IPresenceContainer interface not exposed by client with type "+containerType);
		// Now connect
		client.connect(targetID, getJoinContext(username, connectData));
	}


	protected IConnectContext getJoinContext(final String username,
			final Object password) {
		return ConnectContextFactory.createUsernamePasswordConnectContext(username, password);
	}
	protected String setupUsername(ID targetID, String nickname) throws URISyntaxException {
		String username = null;
		if (nickname != null) {
			username = nickname;
		} else {
			username = targetID.toURI().getUserInfo();
			if (username == null || username.equals(""))
				username = System.getProperty("user.name");
		}
		return username;
	}


}
