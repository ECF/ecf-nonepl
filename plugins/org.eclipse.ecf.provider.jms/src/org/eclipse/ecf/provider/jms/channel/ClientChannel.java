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
import java.io.NotSerializableException;
import java.io.Serializable;
import java.net.ConnectException;

import javax.jms.ObjectMessage;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.JmsPlugin;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class ClientChannel extends Channel implements ISynchAsynchConnection {
	private static final long serialVersionUID = -1381571376210849678L;

	private static final int RESPOND_TO_REQUEST_ERROR_CODE = 32001;

	public ClientChannel(ISynchAsynchEventHandler handler, int keepAlive) {
		super(handler, keepAlive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object, int)
	 */
	public synchronized Object connect(ID remote, Object data, int timeout)
			throws ECFException {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(), "connect",
				new Object[] { remote, data, new Integer(timeout) });
		if (connected)
			throw new ECFException("already connected");
		if (remote == null)
			throw new ECFException("remote target cannot be null");
		if (!(remote instanceof JMSID))
			throw new ECFException("remote " + remote.getName()
					+ " not JMS ID");
		managerID = (JMSID) remote;
		url = managerID.getName();
		topicName = removeLeadingSlashes(url);

		if (!(data instanceof Serializable)) {
			throw new ECFException(new NotSerializableException("data are not serializable"));
		}
		Serializable connectData = (Serializable) data;
		Serializable res;
		try {
			setup();
			Trace.trace(JmsPlugin.PLUGIN_ID, "connecting to " + remote + ","
					+ data + "," + timeout + ")");
			res = getConnectResult(managerID, connectData);
			if (res != null && (!(res instanceof ConnectResponse))) {
				throw new ConnectException("Invalid response");
			}
			if (res == null)
				throw new ConnectException("server refused connection");
		} catch (Exception e) {
			throw new ECFException("connect failed to "+remote.getName(),e);
		}
		ConnectResponse cr = (ConnectResponse) res;
		Object result = cr.getData();
		Trace.exiting(JmsPlugin.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "connect", result);
		return result;
	}

	protected Serializable getConnectResult(ID managerID, Serializable data)
			throws IOException {
		return sendAndWait(new ConnectRequest(getConnectionID(), getLocalID(),
				managerID, data));
	}

	protected void respondToRequest(ObjectMessage omsg, ECFMessage o) {
		Trace.entering(JmsPlugin.PLUGIN_ID,
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"respondToRequest", new Object[] { omsg, o });
		try {
			ObjectMessage first = session
					.createObjectMessage(new DisconnectResponse(
							getConnectionID(), o.getTargetID(),
							o.getSenderID(), null));
			first.setJMSCorrelationID(omsg.getJMSCorrelationID());
			Trace.trace(JmsPlugin.PLUGIN_ID, "respondToRequest.sending="
					+ first);
			topicProducer.send(first);
			handler.handleSynchEvent(new SynchEvent(this, o.getData()));
		} catch (Exception e) {
			traceAndLogExceptionCatch(RESPOND_TO_REQUEST_ERROR_CODE,
					"respondToRequest", e);
		}
		Trace.exiting(JmsPlugin.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "respondToRequest");
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
