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
import java.io.Serializable;
import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.jms.channel.AbstractJMSClientChannel;
import org.eclipse.ecf.provider.jms.channel.JmsTopic;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class WeblogicJMSChannel extends AbstractJMSClientChannel {

	private static final long serialVersionUID = 3688761380066499761L;

	public WeblogicJMSChannel(ISynchAsynchEventHandler handler, int keepAlive)
			throws ECFException {
		super(handler, keepAlive);
	}

	private String getServerFromID(JMSID targetID) {
		return targetID.getServer();
	}

	private InitialContext getInitialContext(String jmsProviderURL)
			throws NamingException {
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				WeblogicJMSServerContainer.JNDI_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, jmsProviderURL);
		return new InitialContext(env);
	}
	
	@Override
	protected Serializable setupJMS(JMSID targetID, Object data)
			throws ECFException {
		try {
			InitialContext ctx = getInitialContext(getServerFromID(targetID));
			Destination topicDestination = (Destination) ctx.lookup(targetID.getTopic());
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup(WeblogicJMSServerContainer.JMS_CONNECTION_FACTORY);
			
			connection = factory.createConnection();
			connection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					onJMSException(arg0);
				}
			});
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			jmsTopic = new JmsTopic(session,topicDestination);
			jmsTopic.getConsumer().setMessageListener(new TopicReceiver());
			connected = true;
			connection.start();
			Serializable connectData = createConnectRequestData(data);
			return connectData;
		} catch (Exception e) {
			disconnect();
			throw new ECFException("WeblogicJMSChannel.setupJMS",e); //$NON-NLS-1$
		}
	}

	@Override
	protected ConnectionFactory createJMSConnectionFactory(JMSID targetID)
			throws IOException {
		// XXX not used due to override of setupJMS above
		return null;
	}
}
