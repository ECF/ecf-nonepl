/****************************************************************************
 * Copyright (c) 2004 2007 Composent, Inc. and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.Activator;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.Messages;
import org.eclipse.ecf.provider.comm.IConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.ServerSOContainer;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSServerChannel;
import org.eclipse.ecf.provider.jms.channel.ConnectRequestMessage;
import org.eclipse.ecf.provider.jms.channel.DisconnectRequestMessage;

/**
 * Abstract JMS Server. Subclasses should be created to create concrete
 * instances of a JMS Server container.
 */
public abstract class AbstractJMSServer extends ServerSOContainer {

	public static final int DEFAULT_KEEPALIVE = 30000;

	private static final int HANDLE_CONNECT_REQUEST_EXCEPTION = 36001;

	private static final int MEMBER_LEAVE_ERROR_CODE = 36002;

	private IConnectHandlerPolicy joinPolicy = null;

	private ISynchAsynchConnection serverChannel;

	// JMS client ID -> BrokerClient
	private Map clients = new HashMap();

	// ECF ID -> JMS client ID
	private Map idMap = new HashMap();

	public AbstractJMSServer(JMSContainerConfig config) {
		super(config);
	}

	/**
	 * Start this server. Subclasses must override this method to start a JMS
	 * server.
	 * 
	 * @throws ECFException
	 *             if some problem with starting the server (e.g. port already
	 *             taken)
	 */
	public abstract void start() throws ECFException;

	protected JMSContainerConfig getJMSContainerConfig() {
		return (JMSContainerConfig) getConfig();
	}

	protected void setConnection(ISynchAsynchConnection channel) {
		this.serverChannel = channel;
	}

	protected ISynchAsynchConnection getConnection() {
		return serverChannel;
	}

	protected IConnectHandlerPolicy getConnectHandlerPolicy() {
		return joinPolicy;
	}

	protected void setConnectHandlerPolicy(IConnectHandlerPolicy policy) {
		this.joinPolicy = policy;
	}

	protected Object addClient(String clientID, Object client) {
		if (clientID == null || clientID.equals("")) //$NON-NLS-1$
			return null;
		synchronized (getGroupMembershipLock()) {
			return clients.put(clientID, client);
		}
	}

	protected Object removeClient(String clientID) {
		if (clientID == null || clientID.equals("")) //$NON-NLS-1$
			return null;
		synchronized (getGroupMembershipLock()) {
			return clients.remove(clientID);
		}
	}

	protected Object getClient(String clientID) {
		synchronized (getGroupMembershipLock()) {
			return clients.get(clientID);
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

	protected Object getClientForID(ID clientID) {
		synchronized (getGroupMembershipLock()) {
			return getClient(getIDMap(clientID));
		}
	}

	protected Serializable processSynch(SynchEvent e) throws IOException {
		Object req = e.getData();
		if (req instanceof ConnectRequestMessage) {
			return handleConnectRequest((ConnectRequestMessage) req,
					(AbstractJMSServerChannel) e.getConnection());
		} else if (req instanceof DisconnectRequestMessage) {
			// disconnect them
			DisconnectRequestMessage drm = (DisconnectRequestMessage) req;
			handleLeave(drm.getSenderID(), null);
		}
		return null;
	}

	protected void traceAndLogExceptionCatch(int code, String method,
			Throwable e) {
		Trace
				.catching(Activator.PLUGIN_ID,
						JmsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
						method, e);
		Activator.getDefault()
				.log(
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, code,
								method, e));
	}

	protected void handleConnectException(ContainerMessage mess,
			AbstractJMSServerChannel serverChannel, Exception e) {
	}

	protected Object checkJoin(SocketAddress socketAddress, ID fromID,
			String targetPath, Serializable data) throws Exception {
		if (joinPolicy != null)
			return joinPolicy.checkConnect(socketAddress, fromID, getID(),
					targetPath, data);
		else
			return null;
	}

	protected void addToIDMap(ID remoteID, String jmsClientID) throws ContainerConnectException {
		if (remoteID != null && jmsClientID != null) {
			addIDMap(remoteID, jmsClientID);
			if (getClientForID(remoteID) == null) {
				removeIDMap(remoteID);
				throw new ContainerConnectException(
						Messages.AbstractJMSServer_CONNECT_EXCEPTION_CONTAINER_CLIENT_NOT_FOUND);
			}
		}
	}
	
	protected Serializable handleConnectRequest(ConnectRequestMessage request,
			AbstractJMSServerChannel channel) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "handleConnectRequest", new Object[] { //$NON-NLS-1$
				request, channel });
		try {
			ContainerMessage containerMessage = (ContainerMessage) request
					.getData();
			if (containerMessage == null)
				throw new InvalidObjectException(
						Messages.AbstractJMSServer_CONNECT_EXCEPTION_CONTAINER_MESSAGE_NOT_NULL);
			ID remoteID = containerMessage.getFromContainerID();
			if (remoteID == null)
				throw new InvalidObjectException(
						Messages.AbstractJMSServer_CONNECT_EXCEPTION_REMOTEID_NOT_NULL);
			ContainerMessage.JoinGroupMessage jgm = (ContainerMessage.JoinGroupMessage) containerMessage
					.getData();
			if (jgm == null)
				throw new InvalidObjectException(
						Messages.AbstractJMSServer_CONNECT_EXCEPTION_JOINGROUPMESSAGE_NOT_NULL);
			ID memberIDs[] = null;
			Serializable[] messages = new Serializable[2];
			synchronized (getGroupMembershipLock()) {
				if (isClosing)
					throw new ContainerConnectException(
							Messages.AbstractJMSServer_CONNECT_EXCEPTION_CONTAINER_CLOSING);
				// Now check to see if this request is going to be allowed
				checkJoin(channel, remoteID, request.getTargetID().getTopic(),
						jgm.getData());

				// add to id map
				addToIDMap(remoteID, request.getSenderJMSID());
				
				if (addNewRemoteMember(remoteID, null)) {
					// Get current membership
					memberIDs = getGroupMemberIDs();
					// Notify existing remotes about new member
					messages[1] = serialize(ContainerMessage
							.createViewChangeMessage(getID(), null,
									getNextSequenceNumber(),
									new ID[] { remoteID }, true, null));
				} else {
					removeIDMap(remoteID);
					ConnectException e = new ConnectException(
							Messages.AbstractJMSServer_CONNECT_EXCEPTION_REFUSED);
					throw e;
				}
			}
			// notify listeners
			fireContainerEvent(new ContainerConnectedEvent(this.getID(),
					remoteID));

			messages[0] = serialize(ContainerMessage.createViewChangeMessage(
					getID(), remoteID, getNextSequenceNumber(), memberIDs,
					true, null));

			return messages;

		} catch (Exception e) {
			traceAndLogExceptionCatch(HANDLE_CONNECT_REQUEST_EXCEPTION,
					"handleConnectRequest", e); //$NON-NLS-1$
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
		serverChannel.sendAsynch(mess.toContainerID, serialize(mess));
	}

	protected void clientRemoved(String clientID) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "clientRemoved", new Object[] { clientID }); //$NON-NLS-1$
		// OK, get ID for client...
		ID remoteID = getIDForClientID(clientID);
		if (remoteID != null) {
			IConnection conn = getConnectionForID(remoteID);
			handleLeave(remoteID, conn);
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "clientRemoved"); //$NON-NLS-1$
	}

	protected void handleLeave(ID target, IConnection conn) {
		if (target == null)
			return;
		if (removeRemoteMember(target)) {
			try {
				queueContainerMessage(ContainerMessage.createViewChangeMessage(
						getID(), null, getNextSequenceNumber(),
						new ID[] { target }, false, null));
			} catch (IOException e) {
				traceAndLogExceptionCatch(MEMBER_LEAVE_ERROR_CODE,
						"memberLeave", e); //$NON-NLS-1$
			}
		}
		if (conn != null)
			disconnect(conn);
	}

}
