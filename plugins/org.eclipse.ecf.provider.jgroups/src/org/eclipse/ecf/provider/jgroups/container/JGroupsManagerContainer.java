/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.container;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jgroups.Activator;
import org.eclipse.ecf.internal.provider.jgroups.JGroupsDebugOptions;
import org.eclipse.ecf.internal.provider.jgroups.Messages;
import org.eclipse.ecf.provider.comm.IAsynchConnection;
import org.eclipse.ecf.provider.comm.IConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.ServerSOContainer;
import org.eclipse.ecf.provider.jgroups.connection.ConnectRequestMessage;
import org.eclipse.ecf.provider.jgroups.connection.DisconnectRequestMessage;
import org.eclipse.ecf.provider.jgroups.connection.JGroupsManagerConnection;
import org.jgroups.Address;
import org.jgroups.stack.IpAddress;

/**
 *
 */
public class JGroupsManagerContainer extends ServerSOContainer {

	private IConnectHandlerPolicy joinPolicy = null;

	private ISynchAsynchConnection serverConnection;

	/**
	 * @param config
	 */
	public JGroupsManagerContainer(ISharedObjectContainerConfig config) {
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
	public void start() throws ECFException {
		serverConnection = new JGroupsManagerConnection(getReceiver());
		serverConnection.start();
	}

	protected void setConnection(ISynchAsynchConnection channel) {
		this.serverConnection = channel;
	}

	protected ISynchAsynchConnection getConnection() {
		return serverConnection;
	}

	protected IConnectHandlerPolicy getConnectHandlerPolicy() {
		return joinPolicy;
	}

	protected void setConnectHandlerPolicy(IConnectHandlerPolicy policy) {
		this.joinPolicy = policy;
	}

	protected Serializable processSynch(SynchEvent e) throws IOException {
		final Object req = e.getData();
		if (req instanceof ConnectRequestMessage) {
			return handleConnectRequest((ConnectRequestMessage) req, (JGroupsManagerConnection) e.getConnection());
		} else if (req instanceof DisconnectRequestMessage) {
			// disconnect them
			final DisconnectRequestMessage dcm = (DisconnectRequestMessage) req;
			final IAsynchConnection conn = getConnectionForID(dcm.getSenderID());
			if (conn != null && conn instanceof JGroupsManagerConnection.Client) {
				final JGroupsManagerConnection.Client client = (JGroupsManagerConnection.Client) conn;
				client.handleDisconnect();
			}
		}
		return null;
	}

	protected void traceAndLogExceptionCatch(int code, String method, Throwable e) {
		Trace.catching(Activator.PLUGIN_ID, JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), method, e);
		Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, code, method, e));
	}

	protected void handleConnectException(ContainerMessage mess, JGroupsManagerConnection serverChannel, Exception e) {
	}

	protected Object checkJoin(SocketAddress socketAddress, ID fromID, String targetPath, Serializable data) throws Exception {
		if (joinPolicy != null)
			return joinPolicy.checkConnect(socketAddress, fromID, getID(), targetPath, data);
		else
			return null;
	}

	protected Serializable handleConnectRequest(ConnectRequestMessage request, JGroupsManagerConnection connection) {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "handleConnectRequest", new Object[] { //$NON-NLS-1$
				request, connection});
		try {
			final ContainerMessage containerMessage = (ContainerMessage) request.getData();
			if (containerMessage == null)
				throw new InvalidObjectException(Messages.JGroupsServer_CONNECT_EXCEPTION_CONTAINER_MESSAGE_NOT_NULL);
			final ID remoteID = containerMessage.getFromContainerID();
			if (remoteID == null)
				throw new InvalidObjectException(Messages.JGroupsServer_CONNECT_EXCEPTION_REMOTEID_NOT_NULL);
			final ContainerMessage.JoinGroupMessage jgm = (ContainerMessage.JoinGroupMessage) containerMessage.getData();
			if (jgm == null)
				throw new InvalidObjectException(Messages.JGroupsServer_CONNECT_EXCEPTION_JOINGROUPMESSAGE_NOT_NULL);
			ID memberIDs[] = null;
			final Serializable[] messages = new Serializable[2];
			JGroupsManagerConnection.Client newclient = null;
			synchronized (getGroupMembershipLock()) {
				if (isClosing)
					throw new ContainerConnectException(Messages.JGroupsServer_CONNECT_EXCEPTION_CONTAINER_CLOSING);
				final Address address = request.getClientAddress();
				int port = -1;
				InetAddress host = null;
				if (address instanceof IpAddress) {
					port = ((IpAddress) address).getPort();
					host = ((IpAddress) address).getIpAddress();
				}
				// Now check to see if this request is going to be allowed
				checkJoin(new InetSocketAddress(host, port), remoteID, request.getTargetID().getChannelName(), jgm.getData());

				newclient = connection.new Client(address, remoteID);

				if (addNewRemoteMember(remoteID, newclient)) {
					// Get current membership
					memberIDs = getGroupMemberIDs();
					// Notify existing remotes about new member
					messages[1] = serialize(ContainerMessage.createViewChangeMessage(getID(), null, getNextSequenceNumber(), new ID[] {remoteID}, true, null));
				} else {
					final ConnectException e = new ConnectException(Messages.JGroupsServer_CONNECT_EXCEPTION_REFUSED);
					throw e;
				}
			}
			// notify listeners
			fireContainerEvent(new ContainerConnectedEvent(this.getID(), remoteID));

			messages[0] = serialize(ContainerMessage.createViewChangeMessage(getID(), remoteID, getNextSequenceNumber(), memberIDs, true, null));

			newclient.start();

			return messages;

		} catch (final Exception e) {
			traceAndLogExceptionCatch(IStatus.ERROR, "handleConnectRequest", e);
			return null;
		}
	}

	protected void forwardExcluding(ID from, ID excluding, ContainerMessage data) throws IOException {
		// no forwarding necessary
	}

	protected void forwardToRemote(ID from, ID to, ContainerMessage data) throws IOException {
		// no forwarding necessary
	}

	protected void queueContainerMessage(ContainerMessage mess) throws IOException {
		serverConnection.sendAsynch(mess.toContainerID, serialize(mess));
	}

	protected void handleLeave(ID target, IConnection conn) {
		if (target == null)
			return;
		if (removeRemoteMember(target)) {
			try {
				queueContainerMessage(ContainerMessage.createViewChangeMessage(getID(), null, getNextSequenceNumber(), new ID[] {target}, false, null));
			} catch (final IOException e) {
				traceAndLogExceptionCatch(IStatus.ERROR, "memberLeave", e); //$NON-NLS-1$
			}
		}
		if (conn != null)
			disconnect(conn);
	}

}
