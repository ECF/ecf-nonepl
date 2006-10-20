/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
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
import org.eclipse.ecf.core.comm.ConnectionInstantiationException;
import org.eclipse.ecf.core.comm.ISynchAsynchConnection;
import org.eclipse.ecf.core.comm.SynchConnectionEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.IQueueEnqueue;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.provider.datashare.DatashareContainerAdapter;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.SOConfig;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.SOContext;
import org.eclipse.ecf.provider.jms.Trace;
import org.eclipse.ecf.provider.jms.channel.ClientChannel;
import org.eclipse.ecf.provider.jms.channel.DisconnectRequest;
import org.eclipse.ecf.provider.jms.identity.JMSNamespace;

public class JMSClientSOContainer extends ClientSOContainer {
	public static final Trace trace = Trace.create("clientcontainer");
	public static final int DEFAULT_KEEPALIVE = JMSServerSOContainer.DEFAULT_KEEPALIVE;
	int keepAlive = 0;

	DatashareContainerAdapter adapter = null;
	
	public void trace(String msg) {
		if (trace != null && Trace.ON) {
			trace.msg(msg);
		}
	}
	public Object getAdapter(Class clazz) {
		if (clazz.equals(IChannelContainerAdapter.class)) {
			synchronized (this) {
				if (adapter == null) {
					adapter = new DatashareContainerAdapter(this);
				}
			}
			return adapter;
		} else return super.getAdapter(clazz);
	}
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(JMSNamespace.JMS_NAMESPACE_NAME);
	}
	public void dumpStack(String msg, Throwable t) {
		if (trace != null && Trace.ON) {
			trace.dumpStack(t, msg);
		}
	}

	public JMSClientSOContainer() throws Exception {
		this(DEFAULT_KEEPALIVE);
		this.adapter = new DatashareContainerAdapter(this);
	}

	public JMSClientSOContainer(int ka) throws Exception {
		super(new SOContainerConfig(IDFactory.getDefault().createGUID()));
		keepAlive = ka;
		this.adapter = new DatashareContainerAdapter(this);
	}

	public JMSClientSOContainer(String userhost, int ka) throws Exception {
		super(new SOContainerConfig(IDFactory.getDefault().createStringID(
				userhost)));
		keepAlive = ka;
		this.adapter = new DatashareContainerAdapter(this);
	}

	protected ISynchAsynchConnection createConnection(ID remoteSpace, Object data)
			throws ConnectionInstantiationException {
		ISynchAsynchConnection c = new ClientChannel(getReceiver(), keepAlive);
		return c;
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

	protected SOContext createSharedObjectContext(SOConfig soconfig,
			IQueueEnqueue queue) {
		return new JMSContainerContext(soconfig.getSharedObjectID(), soconfig
				.getHomeContainerID(), this, soconfig.getProperties(), queue);
	}

	protected Serializable processSynch(SynchConnectionEvent e)
			throws IOException {
		debug("processSynch(" + e + ")");
		Object req = e.getData();
		if (req instanceof DisconnectRequest) {
			handleDisconnectRequest((DisconnectRequest) req);
		}
		return null;
	}

	protected void handleDisconnectRequest(DisconnectRequest request) {
		ID fromID = request.getSenderID();
		if (fromID == null)
			return;
		debug("handleDisconnectRequest(" + request + ")");
		ISynchAsynchConnection conn = getConnection();
		memberLeave(fromID, conn);
		// Notify listeners
		fireContainerEvent(new ContainerDisconnectedEvent(getID(),
				fromID));
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