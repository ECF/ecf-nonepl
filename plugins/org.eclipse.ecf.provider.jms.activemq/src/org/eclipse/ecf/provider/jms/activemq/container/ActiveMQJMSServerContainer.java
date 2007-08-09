/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.activemq.container;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.activemq.ActiveMQConnectionFactory;
import org.activemq.broker.BrokerClient;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.IConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSServerChannel;
import org.eclipse.ecf.provider.jms.container.AbstractJMSServer;
import org.eclipse.ecf.provider.jms.container.JMSContainerConfig;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class ActiveMQJMSServerContainer extends AbstractJMSServer {

	public static final String PASSWORD_PROPERTY = "password";
	public static final String USERNAME_PROPERTY = "username";
	public static final String DEFAULT_PASSWORD = "defaultPassword";
	public static final String DEFAULT_USERNAME = "defaultUsername";

	// JMS client ID -> BrokerClient
	private Map<String, Object> clients = new HashMap<String, Object>();

	// ECF ID -> JMS client ID
	private Map<ID, String> idMap = new HashMap<ID, String>();

	public ActiveMQJMSServerContainer(JMSContainerConfig config) {
		super(config);
	}

	protected Object addClient(String clientID, Object client) {
		if (clientID == null || clientID.equals("")) //$NON-NLS-1$
			return null;
		synchronized (getGroupMembershipLock()) {
			return clients.put(clientID, client);
		}
	}

	protected Object removeClient(String clientID) {
		if (clientID == null || clientID.equals("")) //$NON-NLS-1$
			return null;
		synchronized (getGroupMembershipLock()) {
			return clients.remove(clientID);
		}
	}

	protected Object getClient(String clientID) {
		synchronized (getGroupMembershipLock()) {
			return clients.get(clientID);
		}
	}

	protected void addIDMap(ID ecfID, String clientID) {
		synchronized (getGroupMembershipLock()) {
			idMap.put(ecfID, clientID);
		}
	}

	protected void removeIDMap(ID ecfID) {
		synchronized (getGroupMembershipLock()) {
			idMap.remove(ecfID);
		}
	}

	protected String getIDMap(ID ecfID) {
		synchronized (getGroupMembershipLock()) {
			return (String) idMap.get(ecfID);
		}
	}

	protected ID getIDForClientID(String clientID) {
		if (clientID == null)
			return null;
		synchronized (getGroupMembershipLock()) {
			for (Iterator<ID> i = idMap.keySet().iterator(); i.hasNext();) {
				ID key = (ID) i.next();
				String value = (String) idMap.get(key);
				if (clientID.equals(value)) {
					return key;
				}
			}
		}
		return null;
	}

	protected Object getClientForID(ID clientID) {
		synchronized (getGroupMembershipLock()) {
			return getClient(getIDMap(clientID));
		}
	}

	class ActiveMQServerChannel extends AbstractJMSServerChannel {

		private static final long serialVersionUID = -2348383004973299553L;

		/**
		 * @param handler
		 * @param keepAlive
		 * @throws IOException
		 * @throws URISyntaxException
		 */
		public ActiveMQServerChannel() throws ECFException {
			super(getReceiver(), getJMSContainerConfig().getKeepAlive());
		}

		protected ConnectionFactory createJMSConnectionFactory(JMSID targetID)
				throws IOException {
			return new ActiveMQConnectionFactory(getActiveMQUsername(targetID),
					getActiveMQPassword(targetID), targetID.getName());
		}

		private String getActiveMQPassword(JMSID targetID) {
			String pw = (String) getJMSContainerConfig().getProperties().get(
					PASSWORD_PROPERTY);
			return (pw == null) ? "defaultPassword" : pw;
		}

		private String getActiveMQUsername(JMSID targetID) {
			String username = (String) getJMSContainerConfig().getProperties()
					.get(USERNAME_PROPERTY);
			return (username == null) ? "defaultUsername" : username;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.jms.container.AbstractJMSServer#start()
	 */
	public void start() throws ECFException {
		try {
			ISynchAsynchConnection connection = new ActiveMQServerChannel();
			setConnection(connection);
			connection.start();
		} catch (Exception e) {
			throw new ECFException("exception starting server", e);
		}
	}

	public void dispose() {
		super.dispose();
		getConnection().disconnect();
		setConnection(null);
	}

	public BrokerClient addBrokerClient(BrokerClient client) {
		return (BrokerClient) addClient(client.getClientID(), client);
	}

	public BrokerClient removeBrokerClient(BrokerClient client) {
		return (BrokerClient) removeClient(client.getClientID());
	}

	protected void clientRemoved(String clientID) {
		// OK, get ID for client...
		ID remoteID = getIDForClientID(clientID);
		if (remoteID != null) {
			IConnection conn = getConnectionForID(remoteID);
			handleLeave(remoteID, conn);
		}
	}

	public void brokerClientRemoved(BrokerClient client) {
		clientRemoved(client.getClientID());
	}

}