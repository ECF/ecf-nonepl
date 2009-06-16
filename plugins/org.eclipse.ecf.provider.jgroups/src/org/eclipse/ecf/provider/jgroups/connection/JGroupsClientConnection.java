/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jgroups.Activator;
import org.eclipse.ecf.internal.provider.jgroups.JGroupsDebugOptions;
import org.eclipse.ecf.internal.provider.jgroups.Messages;
import org.eclipse.ecf.provider.comm.ConnectionEvent;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.jgroups.container.SOJGContainerConfig;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.eclipse.osgi.util.NLS;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.SuspectedException;
import org.jgroups.TimeoutException;
import org.jgroups.View;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;

/**
 *
 */
public class JGroupsClientConnection extends AbstractJGroupsConnection {

	private final IChannelConfigurator channelConfigurator;

	/**
	 * @param eventHandler
	 * @param chConf 
	 */
	public JGroupsClientConnection(ISynchAsynchEventHandler eventHandler, IChannelConfigurator chConf) {
		super(eventHandler);
		this.channelConfigurator=chConf;
		try {
			this.channel= channelConfigurator.createMChannel( "225.222.222.222", ((JGroupsID) getLocalID()).getName()  );
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jgroups.AbstractJGroupsConnection#connect(org.eclipse.ecf.core.identity.ID, java.lang.Object, int)
	 */
	public synchronized Object connect(ID targetID, Object data, int timeout) throws ECFException {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "connect", new Object[] {targetID, data, //$NON-NLS-1$
				new Integer(timeout)});
		if (isConnected())
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_ALREADY_CONNECTED);
		if (targetID == null)
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_TARGET_NOT_NULL);
		if (!(targetID instanceof JGroupsID))
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_TARGET_NOT_JMSID);
		if (!(data instanceof Serializable)) {
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_CONNECT_ERROR, new NotSerializableException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_NOT_SERIALIZABLE));
		}
		Object result = null;
		try {
			final JGroupsID jgroupsID = (JGroupsID) targetID;
			Trace.trace(Activator.PLUGIN_ID, "connecting to " + targetID + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ data + "," + timeout + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			result = getConnectResult(jgroupsID, (Serializable) data, timeout);
		} catch (final ECFException e) {
			final ECFException except = e;
			throw new ContainerConnectException(except.getStatus());
		} catch (final Exception e) {
			throw new ContainerConnectException(NLS.bind(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_CONNECT_FAILED, targetID.getName()), e);
		}
		if (result == null)
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_TARGET_REFUSED_CONNECTION);
		if (!(result instanceof ConnectResponseMessage))
			throw new ContainerConnectException(Messages.JGroupsClientChannel_CONNECT_EXCEPTION_INVALID_RESPONSE);
		Object connectResponseResult = null;
		try {
//			connectResponseResult = SOContainer.deserializeContainerMessage((byte[]) ((ConnectResponseMessage) result).getData());
			connectResponseResult = ContainerMessage.createViewChangeMessage(getLocalID(), targetID, 0, new ID[]{ getLocalID() }, true, null);
		} catch (final Exception e) {
			throw new ContainerConnectException(e);
		}
		if (connectResponseResult == null || !(connectResponseResult instanceof ContainerMessage))
			throw new ContainerConnectException("Server response not of type ContainerMessage");
		fireListenersConnect(new ConnectionEvent(this, connectResponseResult));
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING, this.getClass(), "connect", connectResponseResult); //$NON-NLS-1$
		return connectResponseResult;
	}


	/**
	 * @param jgroupsID
	 * @param data
	 * @param timeout
	 * @return
	 */
	private Object getConnectResult(JGroupsID jgroupsID, Serializable data, int timeout) throws ECFException {
		final Message msg = new Message( jgroupsID.getAddress(), null, new ConnectRequestMessage((JGroupsID) getLocalID(), jgroupsID, data));
		channel.send(msg);
		return new ConnectResponseMessage( jgroupsID, (JGroupsID) getLocalID(), data );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jgroups.AbstractJGroupsConnection#sendSynch(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public synchronized Object sendSynch(ID receiver, byte[] data) throws IOException {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "sendSynch", new Object[] {receiver, data}); //$NON-NLS-1$
		Object result = null;
		if (receiver == null || !(receiver instanceof JGroupsID))
			throw new IOException("invalid receiver id");
		if (isActive())
			result = new DisconnectRequestMessage((JGroupsID) getLocalID(), (JGroupsID) receiver, data);
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING, this.getClass(), "sendSynch", result);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection#internalHandleSynch(org.jgroups.Message)
	 */
	protected Object internalHandleSynch(Message message) {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "internalHandleSynch", new Object[] {message}); //$NON-NLS-1$
		final boolean active = isActive();
		final Object o = message.getObject();
		try {
			if (o instanceof DisconnectRequestMessage) {
				final DisconnectRequestMessage dcm = (DisconnectRequestMessage) o;
				if (active)
					eventHandler.handleSynchEvent(new SynchEvent(this, dcm.getData()));
				Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING, this.getClass(), "internalHandleSynch");
				return new DisconnectResponseMessage(dcm.getTargetID(), dcm.getSenderID(), null);
			}
		} catch (final Exception e) {
			Trace.catching(Activator.PLUGIN_ID, JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "internalHandleSynch", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jgroups.connection.AbstractJGroupsConnection#handleViewAccepted(org.jgroups.View)
	 */
	protected void handleViewAccepted(View view) {
		Trace.trace(Activator.PLUGIN_ID, "viewAccepted(" + view + ")");
	}
}
