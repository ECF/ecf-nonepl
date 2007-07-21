/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.weblogic.container;

import java.io.IOException;
import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSClientChannel;
import org.eclipse.ecf.provider.jms.container.AbstractJMSClient;
import org.eclipse.ecf.provider.jms.identity.JMSID;
import org.eclipse.osgi.util.NLS;

public class WLSJMSClientContainer extends AbstractJMSClient {

	public static final int DEFAULT_KEEPALIVE = 30000;
	public static final String DEFAULT_PASSWORD = "weblogic";
	public static final String DEFAULT_USERNAME = "weblogic";

	class WeblogicClientChannel extends AbstractJMSClientChannel {

		private static final long serialVersionUID = -5581778054975360068L;
		private static final String JNDI_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
		private final static String JMS_CONNECTION_FACTORY = "weblogic.jms.ConnectionFactory";

		public WeblogicClientChannel() {
			super(getReceiver(), getKeepAlive());
		}

		private String getProviderURLFromID(JMSID targetID) {
			// XXX replace with proper value from ID
			return "t3://localhost:7001";
		}

		protected ConnectionFactory createJMSConnectionFactory(JMSID targetID)
				throws IOException {
			try {
				InitialContext context = getInitialContext(getProviderURLFromID(targetID));
				return (ConnectionFactory) context
						.lookup(JMS_CONNECTION_FACTORY);
			} catch (Exception e) {
				throw new IOException(NLS.bind(
						"Could not connect to %1. Exception: %2", new Object[] {
								targetID.getName(), e.getLocalizedMessage() }));
			}
		}

		private InitialContext getInitialContext(String jmsProviderURL)
				throws NamingException {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, jmsProviderURL);
			return new InitialContext(env);
		}
	}

	/**
	 * @param keepAlive
	 * @throws Exception
	 */
	public WLSJMSClientContainer(int keepAlive) throws Exception {
		super(keepAlive);
	}

	public WLSJMSClientContainer(String name, int keepAlive) throws Exception {
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
		return new WeblogicClientChannel();
	}
}