/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.connection;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jgroups.Activator;
import org.eclipse.ecf.internal.provider.jgroups.JGroupsDebugOptions;
import org.eclipse.ecf.internal.provider.jgroups.Messages;
import org.eclipse.ecf.provider.comm.DisconnectEvent;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;

/**
 *
 */
public class JGroupsManagerConnection extends AbstractJGroupsConnection {

	private static final long DEFAULT_DISCONNECT_TIMEOUT = 3000;

	private final Map addressToIDMap = Collections.synchronizedMap(new HashMap());

	protected Object addToMap(Address address, ID id) {
		return addressToIDMap.put(address, id);
	}

	protected Object removeFromMap(Address address) {
		return addressToIDMap.remove(address);
	}

	protected ID getIDFromMap(Address address) {
		return (ID) addressToIDMap.get(address);
	}

	protected Address getAddressFromMap(ID id) {
		if (id == null)
			return null;
		synchronized (addressToIDMap) {
			for (final Iterator i = addressToIDMap.keySet().iterator(); i.hasNext();) {
				final Address address = (Address) i.next();
				final ID id1 = (ID) addressToIDMap.get(address);
				if (id1 != null && id1.equals(id))
					return address;
			}
		}
		return null;
	}

	/**
	 * @param eventHandler
	 * @param channelProperties
	 */
	public JGroupsManagerConnection(ISynchAsynchEventHandler eventHandler) throws ECFException {
		super(eventHandler);
		setupJGroups((JGroupsID) getLocalID());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection#connect(org.eclipse.ecf.core.identity.ID, java.lang.Object, int)
	 */
	public Object connect(ID targetID, Object data, int timeout) throws ECFException {
		throw new ECFException(Messages.JGroupsServerChannel_CONNECT_EXCEPTION_CONTAINER_SERVER_CANNOT_CONNECT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection#internalHandleSynch(org.jgroups.Message)
	 */
	protected Object internalHandleSynch(Message message) {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "internalHandleSynch", new Object[] {message}); //$NON-NLS-1$
		final Object o = message.getObject();
		if (o == null) {
			logMessageError("object in message is null", message);
			return null;
		}
		try {
			final Serializable[] resp = (Serializable[]) eventHandler.handleSynchEvent(new SynchEvent(this, o));
			// this resp is an Serializable[] with two messages, one for the
			// connect response and the other for everyone else
			if (o instanceof ConnectRequestMessage) {
				final ConnectResponseMessage crm = new ConnectResponseMessage(getLocalID(), ((ConnectRequestMessage) o).getSenderID(), resp[0]);
				sendAsynch(null, (byte[]) ((resp == null) ? null : resp[1]));
				return crm;
			} else if (o instanceof DisconnectRequestMessage) {
				return new DisconnectResponseMessage(getLocalID(), ((DisconnectRequestMessage) o).getSenderID(), null);
			}
		} catch (final Exception e) {
			Trace.catching(Activator.PLUGIN_ID, JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "internalHandleSynch", e);
		}
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "respondToRequest");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection#sendSynch(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public Object sendSynch(ID receiver, byte[] data) throws IOException {
		final MessageDispatcher messageDispatcher = getMessageDispatcher();
		final Message msg = new Message(null, null, new DisconnectRequestMessage(getLocalID(), receiver, data));
		Object response = null;
		try {
			response = messageDispatcher.sendMessage(msg, GroupRequest.GET_FIRST, DEFAULT_DISCONNECT_TIMEOUT);
		} catch (final Exception e) {
			Trace.catching(Activator.PLUGIN_ID, JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "sendSynch", e);
			throw new IOException("disconnect timeout");
		}
		return response;
	}

	public class Client implements ISynchAsynchConnection {

		private final ID clientID;
		private boolean isConnected = true;
		private boolean isStarted = false;
		private final Object disconnectLock = new Object();
		private boolean disconnectHandled = false;

		public Client(Address address, ID clientID) {
			this.clientID = clientID;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IAsynchConnection#sendAsynch(org.eclipse.ecf.core.identity.ID, byte[])
		 */
		public void sendAsynch(ID receiver, byte[] data) throws IOException {
			JGroupsManagerConnection.this.sendAsynch(receiver, data);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#addListener(org.eclipse.ecf.provider.comm.IConnectionListener)
		 */
		public void addListener(IConnectionListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID, java.lang.Object, int)
		 */
		public Object connect(ID targetID, Object data, int timeout) throws ECFException {
			throw new ECFException(Messages.JGroupsServerChannel_CONNECT_EXCEPTION_CONTAINER_SERVER_CANNOT_CONNECT);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#disconnect()
		 */
		public void disconnect() {
			isConnected = false;
			stop();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#getLocalID()
		 */
		public ID getLocalID() {
			return clientID;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#getProperties()
		 */
		public Map getProperties() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#isConnected()
		 */
		public boolean isConnected() {
			return isConnected;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#isStarted()
		 */
		public boolean isStarted() {
			return isStarted;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#removeListener(org.eclipse.ecf.provider.comm.IConnectionListener)
		 */
		public void removeListener(IConnectionListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#start()
		 */
		public void start() {
			isStarted = true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.IConnection#stop()
		 */
		public void stop() {
			isStarted = false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.comm.ISynchConnection#sendSynch(org.eclipse.ecf.core.identity.ID, byte[])
		 */
		public Object sendSynch(ID receiver, byte[] data) throws IOException {
			return JGroupsManagerConnection.this.sendSynch(receiver, data);
		}

		public void handleDisconnect() {
			synchronized (disconnectLock) {
				if (!disconnectHandled) {
					disconnectHandled = true;
					eventHandler.handleDisconnectEvent(new DisconnectEvent(Client.this, null, null));
				}
			}
			synchronized (Client.this) {
				Client.this.notifyAll();
			}
		}
	}
}