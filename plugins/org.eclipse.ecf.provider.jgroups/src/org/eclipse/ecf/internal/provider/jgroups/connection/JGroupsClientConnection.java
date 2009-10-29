/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jgroups.connection;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Properties;

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
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.eclipse.ecf.remoteservice.eventadmin.DistributedEventAdmin;
import org.eclipse.osgi.util.NLS;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.SuspectedException;
import org.jgroups.TimeoutException;
import org.jgroups.View;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 *
 */
public class JGroupsClientConnection extends AbstractJGroupsConnection
		implements EventHandler {

	private final ServiceRegistration eventHandlerRegistration;
	private final DistributedEventAdmin eventAdminImpl;
	private final ServiceRegistration eventAdminRegistration;

	/**
	 * @param eventHandler
	 * @throws ECFException
	 */
	public JGroupsClientConnection(ISynchAsynchEventHandler eventHandler)
			throws ECFException {
		super(eventHandler);
		final BundleContext context = Activator.getDefault().getContext();
		eventAdminImpl = new DistributedEventAdmin(context);
		eventAdminImpl.start();

		// register as EventAdmin service instance
		Properties props0 = new Properties();
		props0.put(EventConstants.EVENT_TOPIC, "*");
		eventAdminRegistration = context.registerService(
				"org.osgi.service.event.EventAdmin", eventAdminImpl, props0);

		Properties props1 = new Properties();
		props1.put(EventConstants.EVENT_TOPIC, "*");
		eventHandlerRegistration = context.registerService(
				EventHandler.class.getName(), this, props1);
	}

	@Override
	public synchronized Object connect(ID targetID, Object data, int timeout)
			throws ECFException {
		Trace.entering(Activator.PLUGIN_ID,
				JGroupsDebugOptions.METHODS_ENTERING, this.getClass(),
				"connect", new Object[] { targetID, data, //$NON-NLS-1$
						new Integer(timeout) });
		if (isConnected())
			throw new ContainerConnectException("Already connected");//$NON-NLS-1$
		if (targetID == null)
			throw new ContainerConnectException("TargetID must not be null");//$NON-NLS-1$
		if (!(targetID instanceof JGroupsID))
			throw new ContainerConnectException(
					Messages.JGroupsClientChannel_CONNECT_EXCEPTION_TARGET_NOT_JMSID);
		if (!(data instanceof Serializable)) {
			throw new ContainerConnectException(
					Messages.JGroupsClientChannel_CONNECT_EXCEPTION_CONNECT_ERROR,
					new NotSerializableException(
							Messages.JGroupsClientChannel_CONNECT_EXCEPTION_NOT_SERIALIZABLE));
		}
		Object result = null;
		try {
			final JGroupsID jgroupsID = (JGroupsID) targetID;
			setupJGroups(jgroupsID);
			Trace.trace(Activator.PLUGIN_ID, "connecting to " + targetID + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ data + "," + timeout + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			result = getConnectResult(jgroupsID, (Serializable) data, timeout);
		} catch (final ECFException e) {
			final ECFException except = e;
			throw new ContainerConnectException(except.getStatus());
		} catch (final Exception e) {
			throw new ContainerConnectException(
					NLS
							.bind(
									Messages.JGroupsClientChannel_CONNECT_EXCEPTION_CONNECT_FAILED,
									targetID.getName()), e);
		}
		if (result == null)
			throw new ContainerConnectException(
					Messages.JGroupsClientChannel_CONNECT_EXCEPTION_TARGET_REFUSED_CONNECTION);
		if (!(result instanceof ConnectResponseMessage))
			throw new ContainerConnectException(
					Messages.JGroupsClientChannel_CONNECT_EXCEPTION_INVALID_RESPONSE);
		Object connectResponseResult = null;
		try {
			connectResponseResult = SOContainer
					.deserializeContainerMessage((byte[]) ((ConnectResponseMessage) result)
							.getData());
		} catch (final Exception e) {
			throw new ContainerConnectException(e);
		}
		if (connectResponseResult == null
				|| !(connectResponseResult instanceof ContainerMessage))
			throw new ContainerConnectException(
					"Server response not of type ContainerMessage");
		fireListenersConnect(new ConnectionEvent(this, connectResponseResult));
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING,
				this.getClass(), "connect", connectResponseResult); //$NON-NLS-1$
		return connectResponseResult;
	}

	/**
	 * @param jgroupsID
	 * @param data
	 * @param timeout
	 * @return
	 */
	private Object getConnectResult(JGroupsID jgroupsID, Serializable data,
			int timeout) throws ECFException {
		final MessageDispatcher messageDispatcher = getMessageDispatcher();
		final Message msg = new Message(getConnectDestination(), null,
				new ConnectRequestMessage((JGroupsID) getLocalID(), jgroupsID,
						data));
		Object response = null;
		try {
			response = messageDispatcher.sendMessage(msg,
					GroupRequest.GET_FIRST, timeout);
		} catch (final TimeoutException e) {
			throw new ECFException("connect timeout", e);
		} catch (final SuspectedException e) {
			throw new ECFException("connect failure", e);
		}
		return response;
	}

	private Address getConnectDestination() {
		return getChannel().getView().getCreator();
	}

	@Override
	public synchronized Object sendSynch(ID receiver, byte[] data)
			throws IOException {
		Trace.entering(Activator.PLUGIN_ID,
				JGroupsDebugOptions.METHODS_ENTERING, this.getClass(),
				"sendSynch", new Object[] { receiver, data }); //$NON-NLS-1$
		Object result = null;
		if (receiver == null || !(receiver instanceof JGroupsID))
			throw new IOException("invalid receiver id");
		if (isActive())
			result = new DisconnectRequestMessage((JGroupsID) getLocalID(),
					(JGroupsID) receiver, data);
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING,
				this.getClass(), "sendSynch", result);
		return result;
	}

	@Override
	protected Object internalHandleSynch(Message message) {
		Trace.entering(Activator.PLUGIN_ID,
				JGroupsDebugOptions.METHODS_ENTERING, this.getClass(),
				"internalHandleSynch", new Object[] { message }); //$NON-NLS-1$
		final boolean active = isActive();
		final Object o = message.getObject();
		try {
			if (o instanceof DisconnectRequestMessage) {
				final DisconnectRequestMessage dcm = (DisconnectRequestMessage) o;
				if (active)
					eventHandler.handleSynchEvent(new SynchEvent(this, dcm
							.getData()));
				Trace.exiting(Activator.PLUGIN_ID,
						JGroupsDebugOptions.METHODS_EXITING, this.getClass(),
						"internalHandleSynch");
				return new DisconnectResponseMessage(dcm.getTargetID(), dcm
						.getSenderID(), null);
			}
		} catch (final Exception e) {
			Trace.catching(Activator.PLUGIN_ID,
					JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
					"internalHandleSynch", e);
		}
		return null;
	}

	@Override
	protected void handleViewAccepted(View view) {
		Trace.trace(Activator.PLUGIN_ID, "viewAccepted(" + view + ")");
		// TODO [pierre] handle that
	}

	public void handleEvent(Event event) {
		System.out.println("event received by client: " + event.toString());
		if (event.getProperty("command").toString().equalsIgnoreCase("evict")) {
			final DisconnectRequestMessage message = new DisconnectRequestMessage(
					(JGroupsID) this.getLocalID(), (JGroupsID) event
							.getProperty("ID"), null);
			try {
				sendSynch((ID) event.getProperty("ID"), (byte[]) message
						.getData());
			} catch (IOException e) {
				// AFAIRE [pierre] handle exception
				e.printStackTrace();
			}
		}
	}
}
