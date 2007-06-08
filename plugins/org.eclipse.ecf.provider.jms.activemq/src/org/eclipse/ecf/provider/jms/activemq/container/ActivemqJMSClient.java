/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.activemq.container;

import java.io.IOException;

import javax.jms.ConnectionFactory;

import org.activemq.ActiveMQConnectionFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSClientChannel;
import org.eclipse.ecf.provider.jms.container.AbstractJMSClient;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class ActivemqJMSClient extends AbstractJMSClient {

	public static final String PASSWORD_PROPERTY = "password";
	public static final String USERNAME_PROPERTY = "username";

	class ActivemqClientChannel extends AbstractJMSClientChannel {

		private static final long serialVersionUID = -5581778054975360068L;

		public ActivemqClientChannel() {
			super(getReceiver(), getKeepAlive());
		}

		protected ConnectionFactory createJMSConnectionFactory(JMSID targetID)
				throws IOException {
			return new ActiveMQConnectionFactory(getActivemqUsername(targetID),
					getActivemqPassword(targetID), targetID.getName());
		}

		private String getActivemqPassword(JMSID targetID) {
			String pw = (String) getConfig().getProperties().get(
					PASSWORD_PROPERTY);
			return (pw == null) ? "defaultPassword" : pw;
		}

		private String getActivemqUsername(JMSID targetID) {
			String username = (String) getConfig().getProperties().get(
					USERNAME_PROPERTY);
			return (username == null) ? "defaultUsername" : username;
		}

	}

	/**
	 * @param keepAlive
	 * @throws Exception
	 */
	public ActivemqJMSClient(int keepAlive) throws Exception {
		super(keepAlive);
	}

	public ActivemqJMSClient(String name, int keepAlive) throws Exception {
		super(
				new SOContainerConfig(IDFactory.getDefault().createStringID(
						name)), keepAlive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#createConnection(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object)
	 */
	protected ISynchAsynchConnection createConnection(ID remoteSpace,
			Object data) throws ConnectionCreateException {
		return new ActivemqClientChannel();
	}
}