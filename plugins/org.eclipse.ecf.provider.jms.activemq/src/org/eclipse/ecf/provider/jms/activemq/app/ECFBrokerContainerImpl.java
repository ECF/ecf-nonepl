/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.activemq.app;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import org.activemq.broker.Broker;
import org.activemq.broker.BrokerClient;
import org.activemq.broker.BrokerContext;
import org.activemq.broker.impl.BrokerContainerImpl;
import org.activemq.io.util.MemoryBoundedObjectManager;
import org.activemq.message.ConnectionInfo;
import org.activemq.message.ConsumerInfo;
import org.activemq.message.ProducerInfo;
import org.activemq.message.SessionInfo;
import org.activemq.store.PersistenceAdapter;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.jms.activemq.Activator;
import org.eclipse.ecf.internal.provider.jms.activemq.ActiveMQDebugOptions;
import org.eclipse.ecf.provider.jms.activemq.container.ActiveMQJMSServerContainer;

public class ECFBrokerContainerImpl extends BrokerContainerImpl {

	Map clients = Collections.synchronizedMap(new HashMap());

	Map containerMap = Collections.synchronizedMap(new HashMap());

	protected boolean onTheAir = false;

	public void trace(String msg) {
		Trace.trace(Activator.PLUGIN_ID, msg);
	}

	public void setOnTheAir(boolean val) {
		onTheAir = val;
	}

	public void addSOContainer(String name,
			ActiveMQJMSServerContainer socontainer) {
		containerMap.put(name, socontainer);
	}

	public ActiveMQJMSServerContainer getSOContainer(String name) {
		return (ActiveMQJMSServerContainer) containerMap.get(name);
	}

	public ActiveMQJMSServerContainer removeSOContainer(String name) {
		return (ActiveMQJMSServerContainer) containerMap.remove(name);
	}

	public void addClient(BrokerClient client) {
		clients.put(client.getClientID(), client);
	}

	public void removeClient(BrokerClient client) {
		clients.remove(client.getClientID());
	}

	public boolean clientsEmpty() {
		return clients.isEmpty();
	}

	public BrokerClient getClient(BrokerClient client) {
		return (BrokerClient) clients.get(client.getClientID());
	}

	public ECFBrokerContainerImpl() {
		super();
	}

	public ECFBrokerContainerImpl(String brokerName) {
		super(brokerName);
	}

	public ECFBrokerContainerImpl(String brokerName,
			MemoryBoundedObjectManager memoryManager) {
		super(brokerName, memoryManager);
	}

	public ECFBrokerContainerImpl(String brokerName, String clusterName) {
		super(brokerName, clusterName);
	}

	public ECFBrokerContainerImpl(String brokerName,
			PersistenceAdapter persistenceAdapter) {
		super(brokerName, persistenceAdapter);
	}

	public ECFBrokerContainerImpl(String brokerName, BrokerContext context) {
		super(brokerName, context);
	}

	public ECFBrokerContainerImpl(String brokerName, BrokerContext context,
			MemoryBoundedObjectManager memoryManager) {
		super(brokerName, context, memoryManager);
	}

	public ECFBrokerContainerImpl(String brokerName, String clusterName,
			BrokerContext context) {
		super(brokerName, clusterName, context);
	}

	public ECFBrokerContainerImpl(String brokerName,
			PersistenceAdapter persistenceAdapter, BrokerContext context) {
		super(brokerName, persistenceAdapter, context);
	}

	public ECFBrokerContainerImpl(String brokerName, String clusterName,
			PersistenceAdapter persistenceAdapter, BrokerContext context) {
		super(brokerName, clusterName, persistenceAdapter, context);
	}

	public ECFBrokerContainerImpl(Broker broker, BrokerContext context) {
		super(broker, context);
	}

	public void registerSession(BrokerClient client, SessionInfo info)
			throws JMSException {
		trace("registerSession(" + client + "," + info + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void deregisterSession(BrokerClient client, SessionInfo info)
			throws JMSException {
		trace("deregisterSession(" + client + "," + info + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void registerRemoteClientID(String remoteClientID) {
		trace("registerRemoteClientID(" + remoteClientID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		super.registerRemoteClientID(remoteClientID);
	}

	protected void disconnectBrokerClient(BrokerClient client) {
		client.getChannel().forceDisconnect();
	}

	protected ActiveMQJMSServerContainer verifyOrDisconnect(String dest,
			BrokerClient client) {
		if (dest == null) {
			disconnectBrokerClient(client);
			return null;
		}
		ActiveMQJMSServerContainer container = getSOContainer(dest);
		if (container == null) {
			disconnectBrokerClient(client);
			return null;
		}
		return container;
	}

	public void registerMessageConsumer(BrokerClient client, ConsumerInfo info)
			throws JMSException {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerMessageConsumer", new Object[] { client, info }); //$NON-NLS-1$
		trace("registerMessageConsumer(" + client + "," + info + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String dest = info.getDestination().toString();
		ActiveMQJMSServerContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		} else {
			container.addBrokerClient(client);
		}
		super.registerMessageConsumer(client, info);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"registerMessageConsumer"); //$NON-NLS-1$
	}

	public void deregisterMessageConsumer(BrokerClient client, ConsumerInfo info)
			throws JMSException {
		trace("deregisterMessageConsumer(" + client + "," + info + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String dest = info.getDestination().toString();
		ActiveMQJMSServerContainer container = verifyOrDisconnect(dest, client);
		if (container != null) {
			container.removeBrokerClient(client);
		}
		super.deregisterMessageConsumer(client, info);
	}

	public void registerMessageProducer(BrokerClient client, ProducerInfo info)
			throws JMSException {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerMessageProducer", new Object[] { client, info }); //$NON-NLS-1$
		String dest = info.getDestination().toString();
		ActiveMQJMSServerContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		}
		super.registerMessageProducer(client, info);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"registerMessageProducer"); //$NON-NLS-1$
	}

	public void deregisterMessageProducer(BrokerClient client, ProducerInfo info)
			throws JMSException {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterMessageProducer", new Object[] { client, info }); //$NON-NLS-1$
		String dest = info.getDestination().toString();
		ActiveMQJMSServerContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		} else {
			container.brokerClientRemoved(client);
		}
		super.deregisterMessageProducer(client, info);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"deregisterMessageProducer"); //$NON-NLS-1$
	}

	public void deregisterRemoteClientID(String remoteClientID) {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterRemoteClientID", new Object[] { remoteClientID }); //$NON-NLS-1$
		super.deregisterRemoteClientID(remoteClientID);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"deregisterRemoteClientID"); //$NON-NLS-1$
	}

	public void registerConnection(BrokerClient client, ConnectionInfo info)
			throws JMSException {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerConnection", new Object[] { client, info }); //$NON-NLS-1$
		if (!clientsEmpty()) {
			addClient(client);
		}
		super.registerConnection(client, info);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"registerConnection"); //$NON-NLS-1$
	}

	public void deregisterConnection(BrokerClient client, ConnectionInfo info)
			throws JMSException {
		Trace.entering(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterConnection", new Object[] { client, info }); //$NON-NLS-1$
		removeClient(client);
		super.deregisterConnection(client, info);
		Trace.exiting(Activator.PLUGIN_ID,
				ActiveMQDebugOptions.METHODS_EXITING, this.getClass(),
				"deregisterConnection"); //$NON-NLS-1$
	}

}
