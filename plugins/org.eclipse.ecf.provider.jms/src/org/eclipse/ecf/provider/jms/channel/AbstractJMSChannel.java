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
import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.Activator;
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.provider.comm.AsynchEvent;
import org.eclipse.ecf.provider.comm.ConnectionEvent;
import org.eclipse.ecf.provider.comm.DisconnectEvent;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.jms.identity.JMSID;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract JMSChannel implementation. This class is superclass to
 * AbstractJMSServerChannel and AbstractJMSClient channel.
 */
public abstract class AbstractJMSChannel extends SocketAddress implements
		ISynchAsynchConnection {

	private static final int CLOSE_ERROR_CODE = 31002;

	private static final int HANDLE_ASYNCH_ERROR_CODE = 31003;

	private static final int INTERRUPTED_ERROR_CODE = 31004;

	private static final int HANDLE_SYNCH_ERROR_CODE = 31005;

	private static final int GET_CONNECTIONID_ERROR_CODE = 31006;

	private static final int ONMESSAGE_ERROR_CODE = 0;

	protected static long correlationID = 0;

	protected Connection connection = null;

	protected Session session = null;
	
	protected JmsTopic jmsTopic = null;

	protected JMSID targetID = null;

	protected ID containerID;

	protected boolean connected = false;

	private boolean started = false;

	protected ISynchAsynchEventHandler handler;

	protected int keepAlive = -1;

	private Map properties = new HashMap();

	private List connectionListeners = new Vector();

	public AbstractJMSChannel(ISynchAsynchEventHandler hand, int keepAlive) {
		this.handler = hand;
		Assert.isNotNull(this.handler);
		this.containerID = hand.getEventHandlerID();
		Assert.isNotNull(containerID);
		this.keepAlive = keepAlive;
	}

	public Session getSession() {
		return session;
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object, int)
	 */
	public abstract Object connect(ID remote, Object data, int timeout)
			throws ECFException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.ISynchConnection#sendSynch(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
	public abstract Object sendSynch(ID target, byte[] data) throws IOException;

	/**
	 * Create a JMS ConnectionFactory instance for a given targetID with given
	 * data. Implementers of this method must return a non-<code>null</code>
	 * ConnectionFactory instance or throw an IOException. They cannot return
	 * <code>null</code>.
	 * 
	 * @param targetID
	 *            the JMSID for the target host.
	 * @return ConnectionFactory instance. Must not be <code>null</code>.
	 * @throws IOException
	 *             if the connection factory cannot be made for the given
	 *             target.
	 */
	protected abstract ConnectionFactory createJMSConnectionFactory(
			JMSID targetID) throws IOException;

	protected abstract void respondToRequest(ObjectMessage omsg, ECFMessage o);

	protected void fireListenersConnect(ConnectionEvent event) {
		synchronized (connectionListeners) {
			for (Iterator i = connectionListeners.iterator(); i.hasNext();) {
				IConnectionListener l = (IConnectionListener) i.next();
				l.handleConnectEvent(event);
			}
		}
	}

	protected void fireListenersDisconnect(ConnectionEvent event) {
		synchronized (connectionListeners) {
			for (Iterator i = connectionListeners.iterator(); i.hasNext();) {
				IConnectionListener l = (IConnectionListener) i.next();
				l.handleConnectEvent(event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#getLocalID()
	 */
	public ID getLocalID() {
		return containerID;
	}

	protected static long getNextCorrelationID() {
		return correlationID++;
	}

	protected void onJMSException(JMSException except) {
		if (isConnected() && isStarted())
			handler.handleDisconnectEvent(new DisconnectEvent(this, except,
					null));
	}

	protected Serializable createConnectRequestData(Object data) {
		if (data instanceof Serializable)
			return (Serializable) data;
		else
			return null;
	}

	protected Serializable setupJMS(JMSID targetID, Object data)
			throws ECFException {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "setupJMS"); //$NON-NLS-1$
		try {
			ConnectionFactory factory = createJMSConnectionFactory(targetID);
			connection = factory.createConnection();
			connection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					onJMSException(arg0);
				}
			});
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			jmsTopic = new JmsTopic(session,"wlevsDemo-jms/"+targetID.getTopic());
			jmsTopic.getConsumer().setMessageListener(new TopicReceiver());
			connected = true;
			connection.start();
			Serializable connectData = createConnectRequestData(data);
			Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
					this.getClass(), "setup", connectData); //$NON-NLS-1$
			return connectData;
		} catch (Exception e) {
			disconnect();
			throw new ECFException("JMS Setup Exception",e); //$NON-NLS-1$
		}
	}

	public void sendAsynch(ID recipient, Object obj) throws IOException {
		queueObject(recipient, (Serializable) obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IAsynchConnection#sendAsynch(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
	public void sendAsynch(ID recipient, byte[] obj) throws IOException {
		queueObject(recipient, obj);
	}

	private synchronized void queueObject(ID recipient, Serializable obj)
			throws IOException {
		if (!isConnected())
			throw new ConnectException("Not connected"); //$NON-NLS-1$
		ObjectMessage msg = null;
		try {
			msg = session.createObjectMessage(new JMSMessage(getConnectionID(),
					getLocalID(), recipient, obj));
			jmsTopic.getProducer().send(msg);
		} catch (JMSException e) {
			throwIOException("queueObject", "Exception in queueObject", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected void onTopicException(JMSException except) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "onTopicException", new Object[] { except }); //$NON-NLS-1$
		if (isConnected() && isStarted())
			handler.handleDisconnectEvent(new DisconnectEvent(this, except,
					null));
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "onTopicException"); //$NON-NLS-1$
	}

	protected void throwIOException(String method, String msg, Throwable t)
			throws IOException {
		Trace
				.throwing(Activator.PLUGIN_ID,
						JmsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
						method, t);
		throw new IOException(msg + ": " + t.getMessage()); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#isConnected()
	 */
	public synchronized boolean isConnected() {
		return connected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#isStarted()
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#getProperties()
	 */
	public Map getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#addCommEventListener(org.eclipse.ecf.core.comm.IConnectionListener)
	 */
	public void addListener(IConnectionListener listener) {
		connectionListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#removeCommEventListener(org.eclipse.ecf.core.comm.IConnectionListener)
	 */
	public void removeListener(IConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class clazz) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#disconnect()
	 */
	public synchronized void disconnect() {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "disconnect"); //$NON-NLS-1$
		stop();
		fireListenersDisconnect(new ConnectionEvent(this, null));
		connectionListeners.clear();
		notifyAll();
	}

	protected void close() {
		try {
			if (connection != null) {
				connection.close();
				connection.stop();
				connection = null;
				connected = false;
			}
		} catch (Exception e) {
			traceAndLogExceptionCatch(CLOSE_ERROR_CODE, "close", e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#stop()
	 */
	public synchronized void stop() {
		close();
		started = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.comm.IConnection#start()
	 */
	public synchronized void start() {
		started = true;
	}

	protected void handleTopicMessage(Message msg, JMSMessage jmsmsg) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "handleTopicMessage", new Object[] { msg, //$NON-NLS-1$
						jmsmsg });
		if (isConnected() && isStarted() && !waitDone) {
			try {
				Object o = jmsmsg.getData();
				handler.handleAsynchEvent(new AsynchEvent(this, o));
			} catch (IOException e) {
				Trace.catching(Activator.PLUGIN_ID,
						JmsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
						"handleTopicMessage", e); //$NON-NLS-1$
				Activator.getDefault().log(
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								HANDLE_ASYNCH_ERROR_CODE,
								"Exception on handleTopicMessage", e)); //$NON-NLS-1$
			}
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "handleTopicMessage"); //$NON-NLS-1$
	}

	private Object synch = new Object();

	private String correlation = null;

	private Serializable reply = null;

	protected Serializable getReply() {
		return reply;
	}

	protected Serializable sendAndWait(Serializable obj) throws IOException {
		return sendAndWait(obj, keepAlive);
	}

	protected boolean waitDone;
	
	protected Serializable sendAndWait(Serializable obj, int waitDuration)
			throws IOException {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "sendAndWait", new Object[] { obj, //$NON-NLS-1$
						new Integer(waitDuration) });
		synchronized (synch) {
			try {
				ObjectMessage msg = session.createObjectMessage(obj);
				correlation = String.valueOf(getNextCorrelationID());
				msg.setJMSCorrelationID(correlation);
				waitDone = false;
				long waittimeout = System.currentTimeMillis() + waitDuration;
				jmsTopic.getProducer().send(msg);
				while (!waitDone && (waittimeout - System.currentTimeMillis() > 0)) {
					synch.wait(waitDuration/10);
				}
				waitDone = true;
			} catch (JMSException e) {
				Trace.catching(Activator.PLUGIN_ID,
						JmsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(),
						"sendAndWait", e); //$NON-NLS-1$
				throwIOException("sendAndWait", "JMSException in sendAndWait", //$NON-NLS-1$ //$NON-NLS-2$
						e);
			} catch (InterruptedException e) {
				traceAndLogExceptionCatch(INTERRUPTED_ERROR_CODE,
						"handleTopicMessage", e); //$NON-NLS-1$
			}
			Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
					this.getClass(), "sendAndWait", reply); //$NON-NLS-1$
			return reply;
		}
	}

	protected void handleSynchMessage(ObjectMessage msg, ECFMessage ecfmsg) {
		Trace.entering(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_ENTERING,
				this.getClass(), "handleSynchMessage", new Object[] { msg, //$NON-NLS-1$
						ecfmsg });
		synchronized (synch) {
			if (correlation == null)
				return;
			try {
				if (correlation.equals(msg.getJMSCorrelationID())) {
					reply = msg.getObject();
					waitDone = true;
					synch.notify();
				}
			} catch (JMSException e) {
				traceAndLogExceptionCatch(HANDLE_SYNCH_ERROR_CODE,
						"handleSynchMessage", e); //$NON-NLS-1$
			}
		}
		Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "handleSynchMessage"); //$NON-NLS-1$
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

	protected String getConnectionID() {
		String res = null;
		try {
			res = connection.getClientID();
			if (res == null) res = getLocalID().getName();
			return res;
		} catch (Exception e) {
			traceAndLogExceptionCatch(GET_CONNECTIONID_ERROR_CODE,
					"getConnectionID", e); //$NON-NLS-1$
			return null;
		}
	}

	protected final class TopicReceiver implements MessageListener {

		public TopicReceiver() {
			super();
		}
		
		public void onMessage(Message msg) {
			Trace.entering(Activator.PLUGIN_ID,
					JmsDebugOptions.METHODS_ENTERING, this.getClass(),
					"handleSynchMessage", new Object[] { msg }); //$NON-NLS-1$
			try {
				if (msg instanceof ObjectMessage) {
					ObjectMessage omg = (ObjectMessage) msg;
					Object o = omg.getObject();
					if (o instanceof ECFMessage) {
						ECFMessage ecfmsg = (ECFMessage) o;
						ID fromID = ecfmsg.getSenderID();
						if (fromID == null) {
							Trace.exiting(Activator.PLUGIN_ID,
									JmsDebugOptions.METHODS_ENTERING, this
											.getClass(),
									"onMessage.fromID=null"); //$NON-NLS-1$
							return;
						}
						if (fromID.equals(getLocalID())) {
							Trace.exiting(Activator.PLUGIN_ID,
									JmsDebugOptions.METHODS_ENTERING, this
											.getClass(),
									"onMessage.fromID=getLocalID()"); //$NON-NLS-1$
							return;
						}

						ID targetID = ecfmsg.getTargetID();
						if (targetID == null) {
							if (ecfmsg instanceof JMSMessage)
								handleTopicMessage(msg, (JMSMessage) ecfmsg);
							else
								Trace
										.trace(Activator.PLUGIN_ID,
												"onMessage.received invalid message to group"); //$NON-NLS-1$
						} else {
							if (targetID.equals(getLocalID())) {
								if (ecfmsg instanceof JMSMessage)
									handleTopicMessage(msg, (JMSMessage) ecfmsg);
								else if (ecfmsg instanceof SynchRequestMessage)
									respondToRequest(omg, ecfmsg);
								else if (ecfmsg instanceof SynchResponseMessage)
									handleSynchMessage(omg, ecfmsg);
								else
									Trace.trace(Activator.PLUGIN_ID, NLS.bind(
											"onMessage.msg invalid message to {0}" //$NON-NLS-1$
											, targetID));
							}
						}
					} else
						// received bogus message...ignore
						Trace.trace(Activator.PLUGIN_ID, NLS.bind(
								"onMessage received non-ECFMessage...ignoring {0}" //$NON-NLS-1$
								, o));
				} else
					Trace.trace(Activator.PLUGIN_ID, NLS.bind(
							"onMessage.non object message received {0}", msg)); //$NON-NLS-1$
			} catch (Exception e) {
				traceAndLogExceptionCatch(ONMESSAGE_ERROR_CODE, "onMessage", e); //$NON-NLS-1$
			}
			Trace.exiting(Activator.PLUGIN_ID, JmsDebugOptions.METHODS_EXITING,
					this.getClass(), "onMessage"); //$NON-NLS-1$

		}
	}
}
