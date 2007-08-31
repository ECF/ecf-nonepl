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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jgroups.Activator;
import org.eclipse.ecf.internal.provider.jgroups.JGroupsDebugOptions;
import org.eclipse.ecf.provider.comm.AsynchEvent;
import org.eclipse.ecf.provider.comm.ConnectionEvent;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.eclipse.osgi.util.NLS;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelListener;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.JChannelFactory;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

public abstract class AbstractJGroupsConnection implements ISynchAsynchConnection {

	private Channel channel;
	protected boolean started = false;
	protected final ISynchAsynchEventHandler eventHandler;

	protected List connectionListeners = new ArrayList();
	protected MessageDispatcher messageDispatcher;

	private final RequestHandler messageDispatcherHandler = new RequestHandler() {
		public Object handle(Message msg) {
			return handleSynch(msg);
		}
	};

	private final Receiver receiver = new ReceiverAdapter() {
		public byte[] getState() {
			return null;
		}

		public void receive(Message arg0) {
			handleAsynch(arg0);
		}

		public void setState(byte[] arg0) {
		}

		public void block() {
			System.out.println("block()");
		}

		public void suspect(Address arg0) {
			System.out.println("suspect(" + arg0 + ")");
		}

		public void viewAccepted(View arg0) {
			System.out.println("viewAccepted(" + arg0 + ")");
		}
	};

	private final ChannelListener channelListener = new ChannelListener() {

		public void channelClosed(Channel arg0) {
			System.out.println("channelClosed(" + arg0 + ")");
		}

		public void channelConnected(Channel arg0) {
			System.out.println("channelConnected(" + arg0 + ")");
		}

		public void channelDisconnected(Channel arg0) {
			System.out.println("channelConnected(" + arg0 + ")");
		}

		public void channelReconnected(Address arg0) {
			System.out.println("channelReconnected(" + arg0 + ")");
		}

		public void channelShunned() {
			System.out.println("channelShunned()");
		}

	};

	protected void fireListenersConnect(ConnectionEvent event) {
		List toNotify = null;
		synchronized (connectionListeners) {
			toNotify = new ArrayList(connectionListeners);
		}
		for (final Iterator i = toNotify.iterator(); i.hasNext();) {
			final IConnectionListener l = (IConnectionListener) i.next();
			l.handleConnectEvent(event);
		}
	}

	protected void fireListenersDisconnect(ConnectionEvent event) {
		List toNotify = null;
		synchronized (connectionListeners) {
			toNotify = new ArrayList(connectionListeners);
		}
		for (final Iterator i = toNotify.iterator(); i.hasNext();) {
			final IConnectionListener l = (IConnectionListener) i.next();
			l.handleConnectEvent(event);
		}
	}

	public AbstractJGroupsConnection(ISynchAsynchEventHandler eventHandler) {
		Assert.isNotNull(eventHandler);
		this.eventHandler = eventHandler;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IAsynchConnection#sendAsynch(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public synchronized void sendAsynch(ID targetID, byte[] data) throws IOException {
		if (!isConnected())
			throw new IOException("channel not connected");
		try {
			channel.send(null, null, new JGroupsMessage(getLocalID(), targetID, data));
		} catch (final ChannelNotConnectedException e) {
			throw new IOException(e.getLocalizedMessage());
		} catch (final ChannelClosedException e) {
			throw new IOException(e.getLocalizedMessage());
		}
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

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID, java.lang.Object, int)
	 */
	public abstract Object connect(ID targetID, Object data, int timeout) throws ECFException;

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.ISynchConnection#sendSynch(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public abstract Object sendSynch(ID receiver, byte[] data) throws IOException;

	protected void logMessageError(String errorString, Message message) {
		final String messageError = NLS.bind("jgroups message receive error.  error=%1 message=%2", errorString, message);
		Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, messageError, null));
		// XXX remove
		System.err.println(messageError);
	}

	private void handleAsynch(Message message) {
		Trace.entering(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_ENTERING, this.getClass(), "handleAsynch", new Object[] {message}); //$NON-NLS-1$
		if (message == null) {
			logMessageError("handleAsynch:message is null", message);
			return;
		}
		final Address src = message.getSrc();
		if (src == null) {
			logMessageError("handleAsynch:src address is null", message);
			return;
		}
		final Object o = message.getObject();
		if (o == null) {
			logMessageError("object in message is null", message);
			return;
		}
		if (o instanceof JGroupsMessage && started) {
			final JGroupsMessage msg = (JGroupsMessage) o;
			try {
				boolean fromUs = src.equals(getLocalAddress());
				final ID receiverID = msg.getTargetID();
				final boolean toUs = receiverID == null || getLocalID().equals(receiverID);
				if (!fromUs && toUs) {
					Trace.trace(Activator.PLUGIN_ID, "calling handleAsynchEvent");
					eventHandler.handleAsynchEvent(new AsynchEvent(this, msg.getData()));
				}
			} catch (final IOException e) {
				Trace.catching(Activator.PLUGIN_ID, JGroupsDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "handleAsynch", e); //$NON-NLS-1$
				Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "Exception on handleAsynch", e)); //$NON-NLS-1$
			}
		}
		Trace.exiting(Activator.PLUGIN_ID, JGroupsDebugOptions.METHODS_EXITING, this.getClass(), "handleAsynch"); //$NON-NLS-1$
	}

	private Object handleSynch(Message message) {
		if (message == null) {
			logMessageError("Message is null", message);
			return null;
		}
		final Address src = message.getSrc();
		if (src == null) {
			logMessageError("Src address is null", message);
			return null;
		}
		boolean fromUs = src.equals(getLocalAddress());
		if (isActive() && !fromUs)
			return internalHandleSynch(message);
		else
			return null;
	}

	protected abstract Object internalHandleSynch(Message message);

	protected Address getLocalAddress() {
		return channel.getLocalAddress();
	}

	protected Channel getChannel() {
		return channel;
	}

	protected MessageDispatcher getMessageDispatcher() {
		return messageDispatcher;
	}

	protected void setupJGroups(JGroupsID targetID) throws ECFException {
		try {
			final JChannelFactory factory = new JChannelFactory();
			factory.setMultiplexerConfig("stacks.xml");
			channel = factory.createMultiplexerChannel("udp", "ch1");
			channel.addChannelListener(channelListener);
			channel.setReceiver(receiver);
			messageDispatcher = new MessageDispatcher(factory.createMultiplexerChannel("udp", "ch2"), null, null, messageDispatcherHandler);
			channel.connect(targetID.getChannelName());
		} catch (final Exception e) {
			throw new ECFException("channel exception", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#disconnect()
	 */
	public synchronized void disconnect() {
		stop();
		if (channel != null) {
			channel.disconnect();
			channel.close();
			channel = null;
			if (messageDispatcher != null) {
				messageDispatcher = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#getLocalID()
	 */
	public ID getLocalID() {
		return eventHandler.getEventHandlerID();
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
	public synchronized boolean isConnected() {
		return (channel != null && channel.isConnected());
	}

	protected synchronized boolean isActive() {
		return isConnected() && isStarted();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#isStarted()
	 */
	public boolean isStarted() {
		return started;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#start()
	 */
	public void start() {
		started = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.comm.IConnection#stop()
	 */
	public void stop() {
		started = false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}