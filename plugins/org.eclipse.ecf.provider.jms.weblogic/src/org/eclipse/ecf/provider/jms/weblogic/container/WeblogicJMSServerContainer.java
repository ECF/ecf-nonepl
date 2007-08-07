/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.weblogic.container;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.jms.container.AbstractJMSServer;
import org.eclipse.ecf.provider.jms.container.JMSContainerConfig;

public class WeblogicJMSServerContainer extends AbstractJMSServer {

	public static final int DEFAULT_KEEPALIVE = 30000;
	public static final String DEFAULT_PASSWORD = "weblogic";
	public static final String DEFAULT_USERNAME = "weblogic";
	public static final String JNDI_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
	public final static String JMS_CONNECTION_FACTORY = "weblogic.jms.ConnectionFactory";
	public final static String DEFAULT_PROVIDER_URL = "t3://localhost:7001";

	public WeblogicJMSServerContainer(JMSContainerConfig config) {
		super(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.jms.container.AbstractJMSServer#start()
	 */
	public void start() throws ECFException {
		ISynchAsynchConnection connection = new WeblogicJMSChannel(
				getReceiver(), ((JMSContainerConfig) getConfig())
						.getKeepAlive());
		setConnection(connection);
		connection.start();
	}

	public void dispose() {
		getConnection().disconnect();
		setConnection(null);
		super.dispose();
	}

}