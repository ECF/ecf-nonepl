/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.jms.weblogic.container;

import java.io.IOException;
import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSServerChannel;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class WeblogicJMSChannel extends AbstractJMSServerChannel {

	private static final long serialVersionUID = 3688761380066499761L;

	public WeblogicJMSChannel(ISynchAsynchEventHandler handler, int keepAlive)
			throws ECFException {
		super(handler, keepAlive);
	}

	private String getProviderURLFromID(JMSID targetID) {
		// XXX replace with proper value from ID
		return WeblogicJMSServerContainer.DEFAULT_PROVIDER_URL;
	}

	protected ConnectionFactory createJMSConnectionFactory(JMSID targetID)
			throws IOException {
		try {
			InitialContext context = getInitialContext(getProviderURLFromID(targetID));
			return (ConnectionFactory) context
					.lookup(WeblogicJMSServerContainer.JMS_CONNECTION_FACTORY);
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	private InitialContext getInitialContext(String jmsProviderURL)
			throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				WeblogicJMSServerContainer.JNDI_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, jmsProviderURL);
		return new InitialContext(env);
	}
}
