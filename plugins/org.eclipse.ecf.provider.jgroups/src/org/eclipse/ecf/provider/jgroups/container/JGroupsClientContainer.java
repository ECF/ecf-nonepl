/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.container;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection;
import org.eclipse.ecf.provider.jgroups.connection.IChannelConfigurator;
import org.eclipse.ecf.provider.jgroups.connection.JGroupsClientConnection;
import org.eclipse.ecf.provider.jgroups.connection.JGroupsManagerConnection;
import org.eclipse.ecf.provider.jgroups.connection.MChannelConfigurator;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsNamespace;

import urv.machannel.MChannel;

/**
 * Trivial container implementation. Note that container adapter implementations can be
 * provided by the container class to expose appropriate adapters.
 */
public class JGroupsClientContainer extends ClientSOContainer {

	private IChannelConfigurator channelConfigurator;

	public JGroupsClientContainer(SOJGContainerConfig config) throws IDCreateException {
		super(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(JGroupsNamespace.NAME);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#createConnection(org.eclipse.ecf.core.identity.ID, java.lang.Object)
	 */
	protected ISynchAsynchConnection createConnection(ID remoteSpace, Object data) throws ConnectionCreateException {
		channelConfigurator= new MChannelConfigurator( ((SOJGContainerConfig) getConfig()).getStackName());
		ISynchAsynchConnection clientConnection=  new JGroupsClientConnection(getReceiver(), channelConfigurator);
		return clientConnection;
	}

	public MChannel getJChannel() {
		synchronized (getConnectLock()) {
			if (isConnected())
				return ((AbstractJGroupsConnection) getConnection()).getJChannel();
			return null;
		}
	}

}
