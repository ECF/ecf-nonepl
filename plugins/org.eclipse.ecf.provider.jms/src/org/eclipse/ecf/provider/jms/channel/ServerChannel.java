/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
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
import org.eclipse.ecf.internal.provider.jms.JmsPlugin;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;

/**
 * 
 */
public class ServerChannel extends Channel implements ISynchAsynchConnection {
	private static final long serialVersionUID = -4762123821387039176L;

	private static final int RESPOND_TO_REQUEST_ERROR_CODE = 33001;

	public ServerChannel(ISynchAsynchEventHandler handler, int keepAlive)
			throws IOException, URISyntaxException {
		super(handler, keepAlive);
		url = containerID.getName();
		topicName = removeLeadingSlashes(url);
		setup();
	}

	public synchronized Object connect(ID remote, Object data, int timeout)
			throws ECFException {
		throw new ECFException("server cannot connect");
	}

	protected void respondToRequest(ObjectMessage omsg, ECFMessage o) {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"respondToRequest", new Object[] { omsg, o });
		try {
			Serializable[] resp = (Serializable[]) handler
					.handleSynchEvent(new SynchEvent(this, o));
			// this resp is an Serializable[] with two messages, one for the
			// connect response and the other for everyone else
			if (o instanceof ConnectRequest) {
				ObjectMessage first = session
						.createObjectMessage(new ConnectResponse(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), resp[0]));
				first.setJMSCorrelationID(omsg.getJMSCorrelationID());
				Trace.trace(JmsPlugin.PLUGIN_ID,
						"respondToConnectRequest.sending=" + first);
				topicProducer.send(first);
				ObjectMessage second = session
						.createObjectMessage(new JMSMessage(getConnectionID(),
								getLocalID(), null, resp[1]));
				Trace.trace(JmsPlugin.PLUGIN_ID,
						"respondToConnectRequest.sending=" + second);
				topicProducer.send(second);
			} else if (o instanceof DisconnectRequest) {
				ObjectMessage msg = session
						.createObjectMessage(new DisconnectResponse(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), null));
				msg.setJMSCorrelationID(omsg.getJMSCorrelationID());
				Trace.trace(JmsPlugin.PLUGIN_ID,
						"SERVER.respondToDisconnectRequest:sending:" + msg);
				topicProducer.send(msg);
			}
		} catch (Exception e) {
			traceAndLogExceptionCatch(RESPOND_TO_REQUEST_ERROR_CODE,
					"respondToRequest", e);
			hardDisconnect();
		}
	}

	public Object sendSynch(ID target, byte[] data) throws IOException {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(), "sendSynch",
				new Object[] { target, data });
		Object result = null;
		if (isConnected() && isStarted()) {
			result = sendAndWait(new DisconnectRequest(getConnectionID(),
					getLocalID(), target, data), keepAlive);
		}
		Trace.exiting(JmsPlugin.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "sendSynch", result);
		return result;
	}
}
