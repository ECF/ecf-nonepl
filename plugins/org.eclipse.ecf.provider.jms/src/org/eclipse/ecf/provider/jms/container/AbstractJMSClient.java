/*******************************************************************************
 * Copyright (c) 2004 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.container;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.Map;

import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.datashare.DatashareContainerAdapter;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.SOConfig;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.SOContext;
import org.eclipse.ecf.provider.jms.channel.DisconnectRequestMessage;
import org.eclipse.ecf.provider.jms.identity.JMSNamespace;

/**
 * Abstract JMS Client. Subclasses should be created to create concrete
 * instances of a JMS Client container.
 */
public abstract class AbstractJMSClient extends ClientSOContainer {

	private int keepAlive = 0;

	private DatashareContainerAdapter adapter = null;

	public Object getAdapter(Class clazz) {
		if (clazz.equals(IChannelContainerAdapter.class)) {
			return adapter;
		} else
			return super.getAdapter(clazz);
	}

	protected int getKeepAlive() {
		return keepAlive;
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(JMSNamespace.NAME);
	}

	public AbstractJMSClient(int keepAlive) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createGUID()),
				keepAlive);
	}

	public AbstractJMSClient(ISharedObjectContainerConfig config, int keepAlive) {
		super(config);
		this.keepAlive = keepAlive;
		this.adapter = new DatashareContainerAdapter(this);
	}

	protected void handleContainerMessage(ContainerMessage mess)
			throws IOException {
		if (mess == null) {
			debug("got null container message...ignoring");
			return;
		}
		Object data = mess.getData();
		if (data instanceof ContainerMessage.CreateMessage) {
			handleCreateMessage(mess);
		} else if (data instanceof ContainerMessage.CreateResponseMessage) {
			handleCreateResponseMessage(mess);
		} else if (data instanceof ContainerMessage.SharedObjectMessage) {
			handleSharedObjectMessage(mess);
		} else if (data instanceof ContainerMessage.SharedObjectDisposeMessage) {
			handleSharedObjectDisposeMessage(mess);
		} else {
			debug("got unrecognized container message...ignoring: " + mess);
		}
	}

	class JMSContainerContext extends SOContext {

		public JMSContainerContext(ID objID, ID homeID, SOContainer cont,
				Map props, IQueueEnqueue queue) {
			super(objID, homeID, cont, props, queue);
		}
	}

	protected SOContext createSharedObjectContext(SOConfig soconfig,
			IQueueEnqueue queue) {
		return new JMSContainerContext(soconfig.getSharedObjectID(), soconfig
				.getHomeContainerID(), this, soconfig.getProperties(), queue);
	}

	protected Serializable processSynch(SynchEvent e) throws IOException {
		Object req = e.getData();
		if (req instanceof DisconnectRequestMessage) {
			handleDisconnectRequest((DisconnectRequestMessage) req);
		}
		return null;
	}

	protected void handleDisconnectRequest(DisconnectRequestMessage request) {
		ID fromID = request.getSenderID();
		if (fromID == null)
			return;
		ISynchAsynchConnection conn = getConnection();
		handleLeave(fromID, conn);
		// Notify listeners
		fireContainerEvent(new ContainerDisconnectedEvent(getID(), fromID));
	}

	protected ID handleConnectResponse(ID originalTarget, Object serverData)
			throws Exception {
		Object cr = null;
		if (serverData instanceof byte[]) {
			cr = deserializeContainerMessage((byte[]) serverData);
		} else if (serverData instanceof ContainerMessage) {
			cr = serverData;
		} else {
			throw new ConnectException("server provided invalid response");
		}
		return super.handleConnectResponse(originalTarget, cr);
	}
}