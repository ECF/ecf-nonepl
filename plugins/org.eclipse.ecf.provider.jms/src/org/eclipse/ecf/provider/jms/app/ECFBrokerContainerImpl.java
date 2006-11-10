/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.app;

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
import org.eclipse.ecf.internal.provider.jms.JmsDebugOptions;
import org.eclipse.ecf.internal.provider.jms.JmsPlugin;
import org.eclipse.ecf.provider.jms.container.JMSServerSOContainer;

public class ECFBrokerContainerImpl extends BrokerContainerImpl {

	Map clients = Collections.synchronizedMap(new HashMap());

	Map containerMap = Collections.synchronizedMap(new HashMap());

	protected boolean onTheAir = false;

	public void trace(String msg) {
		Trace.trace(JmsPlugin.getDefault(), msg);
	}

	public void setOnTheAir(boolean val) {
		onTheAir = val;
	}

	public void addSOContainer(String name, JMSServerSOContainer socontainer) {
		containerMap.put(name, socontainer);
	}

	public JMSServerSOContainer getSOContainer(String name) {
		return (JMSServerSOContainer) containerMap.get(name);
	}

	public JMSServerSOContainer removeSOContainer(String name) {
		return (JMSServerSOContainer) containerMap.remove(name);
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
		trace("registerSession(" + client + "," + info + ")");
	}

	public void deregisterSession(BrokerClient client, SessionInfo info)
			throws JMSException {
		trace("deregisterSession(" + client + "," + info + ")");
	}

	public void registerRemoteClientID(String remoteClientID) {
		trace("registerRemoteClientID(" + remoteClientID + ")");
		super.registerRemoteClientID(remoteClientID);
	}

	protected void disconnectBrokerClient(BrokerClient client) {
		client.getChannel().forceDisconnect();
	}

	protected JMSServerSOContainer verifyOrDisconnect(String dest,
			BrokerClient client) {
		if (dest == null) {
			disconnectBrokerClient(client);
			return null;
		}
		JMSServerSOContainer container = getSOContainer(dest);
		if (container == null) {
			disconnectBrokerClient(client);
			return null;
		}
		return container;
	}

	public void registerMessageConsumer(BrokerClient client, ConsumerInfo info)
			throws JMSException {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerMessageConsumer", new Object[] { client, info });
		trace("registerMessageConsumer(" + client + "," + info + ")");
		String dest = info.getDestination().toString();
		JMSServerSOContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		} else {
			container.addClient(client);
		}
		super.registerMessageConsumer(client, info);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "registerMessageConsumer");
	}

	public void deregisterMessageConsumer(BrokerClient client, ConsumerInfo info)
			throws JMSException {
		trace("deregisterMessageConsumer(" + client + "," + info + ")");
		String dest = info.getDestination().toString();
		JMSServerSOContainer container = verifyOrDisconnect(dest, client);
		if (container != null) {
			container.removeClient(client);
		}
		super.deregisterMessageConsumer(client, info);
	}

	public void registerMessageProducer(BrokerClient client, ProducerInfo info)
			throws JMSException {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerMessageProducer", new Object[] { client, info });
		String dest = info.getDestination().toString();
		JMSServerSOContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		}
		super.registerMessageProducer(client, info);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "registerMessageProducer");
	}

	public void deregisterMessageProducer(BrokerClient client, ProducerInfo info)
			throws JMSException {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterMessageProducer", new Object[] { client, info });
		String dest = info.getDestination().toString();
		JMSServerSOContainer container = verifyOrDisconnect(dest, client);
		if (container == null) {
			return;
		} else {
			container.clientRemoved(client);
		}
		super.deregisterMessageProducer(client, info);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "deregisterMessageProducer");
	}

	public void deregisterRemoteClientID(String remoteClientID) {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterRemoteClientID", new Object[] { remoteClientID });
		super.deregisterRemoteClientID(remoteClientID);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "deregisterRemoteClientID");
	}

	public void registerConnection(BrokerClient client, ConnectionInfo info)
			throws JMSException {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"registerConnection", new Object[] { client, info });
		if (!clientsEmpty()) {
			addClient(client);
		}
		super.registerConnection(client, info);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "registerConnection");
	}

	public void deregisterConnection(BrokerClient client, ConnectionInfo info)
			throws JMSException {
		Trace.entering(JmsPlugin.getDefault(),
				JmsDebugOptions.METHODS_ENTERING, this.getClass(),
				"deregisterConnection", new Object[] { client, info });
		removeClient(client);
		super.deregisterConnection(client, info);
		Trace.exiting(JmsPlugin.getDefault(), JmsDebugOptions.METHODS_EXITING,
				this.getClass(), "deregisterConnection");
	}

}
