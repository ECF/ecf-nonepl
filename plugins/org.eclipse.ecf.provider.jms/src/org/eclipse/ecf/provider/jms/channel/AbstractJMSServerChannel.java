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
import java.io.Serializable;
import java.net.URISyntaxException;

import javax.jms.ObjectMessage;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.Activator;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.jms.identity.JMSID;

/**
 * Abstract JMS server channel.
 */
public abstract class AbstractJMSServerChannel extends AbstractJMSChannel
		implements ISynchAsynchConnection {
	private static final long serialVersionUID = -4762123821387039176L;

	private static final int RESPOND_TO_REQUEST_ERROR_CODE = 33001;

	public AbstractJMSServerChannel(ISynchAsynchEventHandler handler,
			int keepAlive) throws IOException, URISyntaxException {
		super(handler, keepAlive);
		if (containerID instanceof JMSID) {
			setupJMS((JMSID) containerID, null);
		} else
			throw new IOException("Handler ID not JMSID");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jms.channel.AbstractJMSChannel#connect(org.eclipse.ecf.core.identity.ID, java.lang.Object, int)
	 */
	public synchronized Object connect(ID remote, Object data, int timeout)
			throws ECFException {
		throw new ECFException("server cannot connect");
	}

	protected void respondToRequest(ObjectMessage omsg, ECFMessage o) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "respondToRequest", new Object[] { omsg, o });
		try {
			Serializable[] resp = (Serializable[]) handler
					.handleSynchEvent(new SynchEvent(this, o));
			// this resp is an Serializable[] with two messages, one for the
			// connect response and the other for everyone else
			if (o instanceof ConnectRequestMessage) {
				ObjectMessage first = session
						.createObjectMessage(new ConnectResponseMessage(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), resp[0]));
				first.setJMSCorrelationID(omsg.getJMSCorrelationID());
				Trace.trace(Activator.PLUGIN_ID,
						"respondToConnectRequest.sending=" + first);
				topicProducer.send(first);
				ObjectMessage second = session
						.createObjectMessage(new JMSMessage(getConnectionID(),
								getLocalID(), null, resp[1]));
				Trace.trace(Activator.PLUGIN_ID,
						"respondToConnectRequest.sending=" + second);
				topicProducer.send(second);
			} else if (o instanceof DisconnectRequestMessage) {
				ObjectMessage msg = session
						.createObjectMessage(new DisconnectResponseMessage(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), null));
				msg.setJMSCorrelationID(omsg.getJMSCorrelationID());
				Trace.trace(Activator.PLUGIN_ID,
						"SERVER.respondToDisconnectRequest:sending:" + msg);
				topicProducer.send(msg);
			}
		} catch (Exception e) {
			traceAndLogExceptionCatch(RESPOND_TO_REQUEST_ERROR_CODE,
					"respondToRequest", e);
			hardDisconnect();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.jms.channel.AbstractJMSChannel#sendSynch(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public Object sendSynch(ID target, byte[] data) throws IOException {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "sendSynch", new Object[] { target, data });
		Object result = null;
		if (isConnected() && isStarted()) {
			result = sendAndWait(new DisconnectRequestMessage(
					getConnectionID(), getLocalID(), target, data), keepAlive);
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "sendSynch", result);
		return result;
	}
}
