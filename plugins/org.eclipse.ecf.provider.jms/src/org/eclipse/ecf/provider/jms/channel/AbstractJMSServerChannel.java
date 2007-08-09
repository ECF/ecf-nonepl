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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.jms.ObjectMessage;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.Activator;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.Messages;
import org.eclipse.ecf.provider.comm.DisconnectEvent;
import org.eclipse.ecf.provider.comm.IConnectionListener;
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
			int keepAlive) throws ECFException {
		super(handler, keepAlive);
		if (localContainerID instanceof JMSID) {
			setupJMS((JMSID) localContainerID, null);
		} else
			throw new ECFException(
					Messages.AbstractJMSServerChannel_CONNECT_EXCEPTION_CONTAINER_NOT_JMSID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.jms.channel.AbstractJMSChannel#connect(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object, int)
	 */
	public synchronized Object connect(ID remote, Object data, int timeout)
			throws ECFException {
		throw new ECFException(
				Messages.AbstractJMSServerChannel_CONNECT_EXCEPTION_CONTAINER_SERVER_CANNOT_CONNECT);
	}

	public class Client implements ISynchAsynchConnection {

		public static final int DEFAULT_PING_WAITTIME = 3000;

		private Map properties;
		private ID clientID;
		private boolean isStarted = false;
		private Object disconnectLock = new Object();
		private boolean disconnectHandled = false;

		private Thread pingThread = null;
		private int pingWaitTime = DEFAULT_PING_WAITTIME;

		public Client(ID clientID) {
			this.clientID = clientID;
			this.properties = new HashMap();
		}

		public void sendAsynch(ID receiver, byte[] data) throws IOException {
			AbstractJMSServerChannel.this.sendAsynch(receiver, data);
		}

		public void addListener(IConnectionListener listener) {
		}

		public Object connect(ID remote, Object data, int timeout)
				throws ECFException {
			throw new ECFException(
					Messages.AbstractJMSServerChannel_CONNECT_EXCEPTION_CONTAINER_SERVER_CANNOT_CONNECT);
		}

		public synchronized void disconnect() {
			stop();
		}

		public ID getLocalID() {
			return clientID;
		}

		public Map getProperties() {
			return properties;
		}

		public boolean isConnected() {
			return true;
		}

		public boolean isStarted() {
			return isStarted;
		}

		public void removeListener(IConnectionListener listener) {
			// TODO Auto-generated method stub
		}

		public void start() {
			if (!isStarted) {
				isStarted = true;
				pingThread = setupPing();
				pingThread.setDaemon(true);
				pingThread.start();
			}
		}

		public void stop() {
			if (isStarted) {
				isStarted = false;
				if (pingThread != null) {
					pingThread.interrupt();
					pingThread = null;
				}
			}
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public Object sendSynch(ID receiver, byte[] data) throws IOException {
			return AbstractJMSServerChannel.this.sendSynch(receiver, data);
		}

		private Thread setupPing() {
			final int pingStartWait = (new Random()).nextInt(keepAlive / 2);
			return new Thread(new Runnable() {
				public void run() {
					Thread me = Thread.currentThread();
					// Sleep a random interval to start
					try {
						Thread.sleep(pingStartWait);
					} catch (InterruptedException e) {
						return;
					}
					// Setup ping frequency as keepAlive /2
					int frequency = keepAlive / 2;
					while (isStarted) {
						try {
							// We give up if thread interrupted or disconnect
							// has
							// occurred
							if (me.isInterrupted() || disconnectHandled)
								break;
							// Sleep for timeout interval divided by two
							Thread.sleep(frequency);
							// We give up if thread interrupted or disconnect
							// has
							// occurred
							if (me.isInterrupted() || disconnectHandled)
								break;
							sendAndWait(new Ping(AbstractJMSServerChannel.this
									.getLocalID(), Client.this.getLocalID()),
									pingWaitTime);
						} catch (Exception e) {
							handleException(e);
							break;
						}
					}
					handleException(null);
				}
			}, getLocalID()
					+ ":ping:" + AbstractJMSServerChannel.this.getLocalID()); //$NON-NLS-1$
		}

		public void handleDisconnect() {
			synchronized (disconnectLock) {
				if (!disconnectHandled) {
					disconnectHandled = true;
					handler.handleDisconnectEvent(new DisconnectEvent(
							Client.this, null, null));
				}
			}
			synchronized (Client.this) {
				Client.this.notifyAll();
			}
		}

		private void handleException(Throwable e) {
			synchronized (disconnectLock) {
				if (!disconnectHandled) {
					disconnectHandled = true;
					if (e != null)
						handler.handleDisconnectEvent(new DisconnectEvent(
								Client.this, e, null));
				}
			}
			synchronized (Client.this) {
				Client.this.notifyAll();
			}
		}
	}

	protected void handleSynchRequest(ObjectMessage omsg, ECFMessage o) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "respondToRequest", new Object[] { omsg, o }); //$NON-NLS-1$
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
				jmsTopic.getProducer().send(first);
				ObjectMessage second = session
						.createObjectMessage(new JMSMessage(getConnectionID(),
								getLocalID(), null, resp[1]));
				jmsTopic.getProducer().send(second);
			} else if (o instanceof DisconnectRequestMessage) {
				ObjectMessage msg = session
						.createObjectMessage(new DisconnectResponseMessage(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), null));
				msg.setJMSCorrelationID(omsg.getJMSCorrelationID());
				jmsTopic.getProducer().send(msg);
			}
		} catch (Exception e) {
			traceAndLogExceptionCatch(RESPOND_TO_REQUEST_ERROR_CODE,
					"respondToRequest", e); //$NON-NLS-1$
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
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
		if (isConnected() && isStarted()) {
			result = sendAndWait(new DisconnectRequestMessage(
					getConnectionID(), getLocalID(), target, data), keepAlive);
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "sendSynch", result); //$NON-NLS-1$
		return result;
	}
}
