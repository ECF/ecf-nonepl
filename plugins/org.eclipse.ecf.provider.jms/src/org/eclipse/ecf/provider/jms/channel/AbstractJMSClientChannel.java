/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.channel;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;

import javax.jms.ObjectMessage;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.Activator;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.Messages;
import org.eclipse.ecf.provider.comm.ConnectionEvent;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.jms.identity.JMSID;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractJMSClientChannel extends AbstractJMSChannel
		implements ISynchAsynchConnection {
	private static final long serialVersionUID = -1381571376210849678L;

	private static final int RESPOND_TO_REQUEST_ERROR_CODE = 32001;
	private static final int DEFAULT_DISCONNECT_WAIT_TIME = 3000;

	private int disconnectWaitTime = DEFAULT_DISCONNECT_WAIT_TIME;

	public AbstractJMSClientChannel(ISynchAsynchEventHandler handler,
			int keepAlive) {
		super(handler, keepAlive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object, int)
	 */
	public synchronized Object connect(ID target, Object data, int timeout)
			throws ECFException {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "connect", new Object[] { target, data, //$NON-NLS-1$
						new Integer(timeout) });
		if (isConnected())
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_ALREADY_CONNECTED);
		if (target == null)
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_TARGET_NOT_NULL);
		if (!(target instanceof JMSID))
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_TARGET_NOT_JMSID);
		if (!(data instanceof Serializable)) {
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_CONNECT_ERROR,
					new NotSerializableException(
							Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_NOT_SERIALIZABLE));
		}
		Serializable result = null;
		try {
			Serializable connectData = setupJMS((JMSID) target, data);
			Trace.trace(Activator.PLUGIN_ID, "connecting to " + target + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ data + "," + timeout + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			result = getConnectResult((JMSID) target, connectData);
		} catch (Exception e) {
			throw new ContainerConnectException(
					NLS
							.bind(
									Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_CONNECT_FAILED,
									target.getName()), e);
		}
		if (result == null)
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_TARGET_REFUSED_CONNECTION);
		if (!(result instanceof ConnectResponseMessage))
			throw new ContainerConnectException(
					Messages.AbstractJMSClientChannel_CONNECT_EXCEPTION_INVALID_RESPONSE);
		Object resultData = ((ConnectResponseMessage) result).getData();
		fireListenersConnect(new ConnectionEvent(this, resultData));
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "connect", resultData); //$NON-NLS-1$
		return resultData;
	}

	protected Serializable getConnectResult(JMSID managerID, Serializable data)
			throws IOException {
		return sendAndWait(new ConnectRequestMessage(getConnectionID(),
				getLocalID(), managerID, data));
	}

	protected void handleSynchRequest(ObjectMessage omsg, ECFMessage o) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "respondToRequest", new Object[] { omsg, o }); //$NON-NLS-1$
		try {
			if (o instanceof DisconnectRequestMessage) {
				ObjectMessage response = session
						.createObjectMessage(new DisconnectResponseMessage(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), null));
				response.setJMSCorrelationID(omsg.getJMSCorrelationID());
				jmsTopic.getProducer().send(response);
				handler.handleSynchEvent(new SynchEvent(this, o.getData()));
			} else if (o instanceof Ping) {
				ObjectMessage response = session
						.createObjectMessage(new PingResponse(o.getTargetID(),
								o.getSenderID()));
				response.setJMSCorrelationID(omsg.getJMSCorrelationID());
				jmsTopic.getProducer().send(response);
			}
		} catch (Exception e) {
			traceAndLogExceptionCatch(RESPOND_TO_REQUEST_ERROR_CODE,
					"respondToRequest", e); //$NON-NLS-1$			
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "respondToRequest"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.jms.channel.AbstractJMSChannel#sendSynch(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
	public Object sendSynch(ID target, byte[] data) throws IOException {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "sendSynch", new Object[] { target, data }); //$NON-NLS-1$
		Object result = null;
		if (isConnected() && isStarted() && !isStopping()) {
			isStopping = true;
			result = sendAndWait(new DisconnectRequestMessage(
					getConnectionID(), getLocalID(), target, data),
					disconnectWaitTime);
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "sendSynch", result); //$NON-NLS-1$
		return result;
	}
}
