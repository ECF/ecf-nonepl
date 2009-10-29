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
import org.eclipse.ecf.internal.provider.jgroups.connection.AbstractJGroupsConnection;
import org.eclipse.ecf.internal.provider.jgroups.connection.JGroupsClientConnection;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsNamespace;
import org.jgroups.Channel;

/**
 * Trivial container implementation. Note that container adapter implementations
 * can be provided by the container class to expose appropriate adapters.
 */
public class JGroupsClientContainer extends ClientSOContainer {

	public JGroupsClientContainer(SOContainerConfig config)
			throws IDCreateException {
		super(config);
	}

	@Override
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(JGroupsNamespace.NAME);
	}

	@Override
	protected ISynchAsynchConnection createConnection(ID remoteSpace,
			Object data) throws ConnectionCreateException {
		JGroupsClientConnection jgConnection = null;
		try {
			jgConnection = new JGroupsClientConnection(
					getReceiver());
		} catch (ECFException e) {
			// TODO [pierre]
			e.printStackTrace();
		}
		return jgConnection;
	}

	public Channel getJChannel() {
		synchronized (getConnectLock()) {
			if (isConnected())
				return ((AbstractJGroupsConnection) getConnection())
						.getJChannel();
			return null;
		}
	}
}
