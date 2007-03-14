/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jms.container;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activemq.broker.BrokerClient;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.JmsPlugin;
import org.eclipse.ecf.provider.comm.IConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.ServerSOContainer;
import org.eclipse.ecf.provider.jms.channel.ClientChannelProxy;
import org.eclipse.ecf.provider.jms.channel.ConnectRequest;
import org.eclipse.ecf.provider.jms.channel.ServerChannel;

public class JMSServerSOContainer extends ServerSOContainer {

	public static final int DEFAULT_KEEPALIVE = 30000;

	private static final int HANDLE_CONNECT_REQUEST_EXCEPTION = 36001;

	private static final int MEMBER_LEAVE_ERROR_CODE = 36002;

	// Keep alive value
	protected int keepAlive = DEFAULT_KEEPALIVE;

	protected boolean isSingle = false;

	protected IConnectHandlerPolicy joinPolicy = null;

	protected int getKeepAlive() {
		return keepAlive;
	}

	ISynchAsynchConnection serverChannel;

	// JMS client ID -> BrokerClient
	Map clients = new HashMap();

	// ECF ID -> JMS client ID
	Map idMap = new HashMap();

	public void addClient(BrokerClient client) {
		synchronized (getGroupMembershipLock()) {
			String key = client.getClientID();
			if (key == null)
				return;
			else if (!clients.containsKey(key)) {
				clients.put(key, client);
			}
		}
	}

	public void removeClient(BrokerClient client) {
		synchronized (getGroupMembershipLock()) {
			String key = client.getClientID();
			if (key == null)
				return;
			else
				clients.remove(key);
		}
	}

	protected BrokerClient getClient(String clientID) {
		synchronized (getGroupMembershipLock()) {
			return (BrokerClient) clients.get(clientID);
		}
	}

	protected void addIDMap(ID ecfID, String clientID) {
		synchronized (getGroupMembershipLock()) {
			idMap.put(ecfID, clientID);
		}
	}

	protected void removeIDMap(ID ecfID) {
		synchronized (getGroupMembershipLock()) {
			idMap.remove(ecfID);
		}
	}

	protected String getIDMap(ID ecfID) {
		synchronized (getGroupMembershipLock()) {
			return (String) idMap.get(ecfID);
		}
	}

	protected ID getIDForClientID(String clientID) {
		if (clientID == null)
			return null;
		synchronized (getGroupMembershipLock()) {
			for (Iterator i = idMap.keySet().iterator(); i.hasNext();) {
				ID key = (ID) i.next();
				String value = (String) idMap.get(key);
				if (clientID.equals(value)) {
					return key;
				}
			}
		}
		return null;
	}

	public BrokerClient getClient(ID clientID) {
		synchronized (getGroupMembershipLock()) {
			return getClient(getIDMap(clientID));
		}
	}

	public JMSServerSOContainer(ISharedObjectContainerConfig config,
			int keepAlive) {
		super(config);
		this.keepAlive = keepAlive;
	}

	public void initialize() throws IOException, URISyntaxException {
		serverChannel = new ServerChannel(receiver, keepAlive);
		serverChannel.start();
	}

	public void dispose() {
		super.dispose();
		serverChannel.stop();
	}

	protected Serializable processSynch(SynchEvent e) throws IOException {
		debug("processSynch(" + e + ")");
		Object req = e.getData();
		if (req instanceof ConnectRequest) {
			return handleConnectRequest((ConnectRequest) req, (ServerChannel) e
					.getConnection());
		}
		return null;
	}

	protected void traceAndLogExceptionCatch(int code, String method,
			Throwable e) {
		Trace
				.catching(JmsPlugin.PLUGIN_ID,
						JmsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
						method, e);
		JmsPlugin.getDefault().log(
						new Status(IStatus.ERROR, JmsPlugin.PLUGIN_ID, code,
								method, e));
	}

	protected void handleConnectException(ContainerMessage mess,
			ServerChannel serverChannel, Exception e) {
		/*
		 * traceAndLogExceptionCatch(HANDLE_CONNECT_EXCEPTION, method, e)
		 * dumpStack("connect exception with message " + mess + " from channel " +
		 * serverChannel, e);
		 */
	}

	protected Object checkJoin(SocketAddress saddr, ID fromID, String target,
			Serializable data) throws Exception {
		if (joinPolicy != null) {
			return this.joinPolicy.checkConnect(saddr, fromID, getID(), target,
					data);
		}
		return null;
	}

	protected ClientChannelProxy createClientChannelProxy(BrokerClient client,
			ServerChannel channel, ID remoteID) {
		return new ClientChannelProxy(this, channel, remoteID);
	}

	protected String removeLeadingSlashes(String path) {
		String name = path;
		while (name.indexOf('/') != -1) {
			name = name.substring(1);
		}
		return name;
	}

	protected Serializable handleConnectRequest(ConnectRequest request,
			ServerChannel channel) {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"handleConnectRequest", new Object[] { request, channel });
		Object data = request.getData();
		ContainerMessage mess = null;
		try {
			mess = (ContainerMessage) data;
			if (mess == null) {
				throw new InvalidObjectException("containermessage is null");
			}
			ID remoteID = mess.getFromContainerID();
			if (remoteID == null)
				throw new InvalidObjectException("remote id is null");
			ContainerMessage.JoinGroupMessage jgm = (ContainerMessage.JoinGroupMessage) mess
					.getData();
			if (jgm == null)
				throw new IOException("join group message is null");
			ID memberIDs[] = null;
			Serializable[] messages = new Serializable[2];
			synchronized (getGroupMembershipLock()) {
				if (isClosing) {
					Exception e = new InvalidObjectException(
							"container is closing");
					throw e;
				}
				// Now check to see if this request is going to be allowed
				URI uri = new URI(request.getTargetID().getName());
				String path = new URI(uri.getSchemeSpecificPart()).getPath();
				Serializable d = jgm.getData();
				checkJoin(channel, remoteID, path, d);

				// add to id map
				addIDMap(remoteID, request.getSenderJMSID());
				BrokerClient client = getClient(remoteID);
				if (client == null) {
					removeIDMap(remoteID);
					throw new ConnectException("broker client is null");
				}
				ClientChannelProxy clientProxy = createClientChannelProxy(
						client, channel, remoteID);
				if (addNewRemoteMember(remoteID, clientProxy)) {
					// Get current membership
					memberIDs = getGroupMemberIDs();
					// Notify existing remotes about new member
					messages[1] = serializeObject(ContainerMessage
							.createViewChangeMessage(getID(), null,
									getNextSequenceNumber(),
									new ID[] { remoteID }, true, null));
					// Start messaging to new member
					clientProxy.start();
				} else {
					removeIDMap(remoteID);
					ConnectException e = new ConnectException(
							"server refused connection");
					throw e;
				}
			}
			// notify listeners
			fireContainerEvent(new ContainerConnectedEvent(this.getID(),
					remoteID));

			messages[0] = serializeObject(ContainerMessage
					.createViewChangeMessage(getID(), remoteID,
							getNextSequenceNumber(), memberIDs, true, null));

			return messages;

		} catch (Exception e) {
			traceAndLogExceptionCatch(HANDLE_CONNECT_REQUEST_EXCEPTION,
					"handleConnectRequest", e);
			return null;
		}
	}

	protected void forwardExcluding(ID from, ID excluding, ContainerMessage data)
			throws IOException {
		// no forwarding necessary
	}

	protected void forwardToRemote(ID from, ID to, ContainerMessage data)
			throws IOException {
		// no forwarding necessary
	}

	protected void queueContainerMessage(ContainerMessage mess)
			throws IOException {
		serverChannel.sendAsynch(mess.toContainerID, serializeObject(mess));
	}

	public void clientRemoved(BrokerClient client) {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"clientRemoved", new Object[] { client });
		// OK, get ID for client...
		ID remoteID = getIDForClientID(client.getClientID());
		if (remoteID != null) {
			IConnection conn = getConnectionForID(remoteID);
			memberLeave(remoteID, conn);
		}
		Trace.exiting(JmsPlugin.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "clientRemoved");
	}

	protected void memberLeave(ID target, IConnection conn) {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"memberLeave", new Object[] { target, conn });
		if (target == null)
			return;
		if (removeRemoteMember(target)) {
			try {
				queueContainerMessage(ContainerMessage.createViewChangeMessage(
						getID(), null, getNextSequenceNumber(),
						new ID[] { target }, false, null));
			} catch (IOException e) {
				traceAndLogExceptionCatch(MEMBER_LEAVE_ERROR_CODE,
						"memberLeave", e);
			}
		}
		if (conn != null)
			killConnection(conn);
		Trace.exiting(JmsPlugin.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "memberLeave");
	}

}