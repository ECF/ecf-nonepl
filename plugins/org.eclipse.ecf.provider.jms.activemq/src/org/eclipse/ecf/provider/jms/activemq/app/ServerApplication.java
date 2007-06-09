/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.activemq.app;

import javax.jms.JMSException;

import org.activemq.ActiveMQConnection;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.jms.activemq.container.ActiveMQJMSServerContainer;
import org.eclipse.ecf.provider.jms.container.JMSContainerConfig;
import org.eclipse.ecf.provider.jms.identity.JMSID;
import org.eclipse.ecf.provider.jms.identity.JMSNamespace;

/**
 * A simple command line tool which runs a ActiveMQ JMS server on the command
 * line
 * 
 * Usage: org.eclipse.ecf.provider.jms.activemq.app.ServerApplication <server
 * URL> e.g. org.eclipse.ecf.provider.jms.activemq.app.ServerApplication
 * tcp://localhost:3240/server
 * 
 * @version $Revision: 1.2 $
 */
public class ServerApplication {
	public static final int DEFAULT_KEEPALIVE = 30000;

	static ActiveMQJMSServerContainer groups[] = null;

	protected static void initializeNamespace() {
		IDFactory.getDefault().addNamespace(new JMSNamespace());
	}

	protected static void setupSharedObjectContainers(
			ECFBrokerContainerImpl broker, String[] urls) throws Exception {
		if (urls == null || urls.length == 0)
			throw new NullPointerException("must have at least one group url");
		initializeNamespace();
		groups = new ActiveMQJMSServerContainer[urls.length];
		for (int i = 0; i < urls.length; i++) {
			String serverid = urls[i];
			JMSID groupID = (JMSID) IDFactory.getDefault().createID(
					JMSNamespace.NAME, serverid);
			ActiveMQJMSServerContainer newCont = new ActiveMQJMSServerContainer(
					new JMSContainerConfig(groupID));
			broker.addSOContainer(groupID.getTopic(), newCont);
			newCont.start();
			groups[i] = newCont;
		}
	}

	/**
	 * run the Message Broker as a standalone application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			String url = ActiveMQConnection.DEFAULT_BROKER_URL;
			if (args.length > 0) {
				url = args[0];
			}
			ECFBrokerContainerImpl container = new ECFBrokerContainerImpl();
			container.addConnector(url);
			if (args.length > 1) {
				container.addNetworkConnector(args[1]);
			}
			container.start();
			// XXX generalize to allow multiple groups/urls
			System.out.println("Creating JMS container at " + url);
			setupSharedObjectContainers((ECFBrokerContainerImpl) container,
					new String[] { url });
			container.setOnTheAir(true);
			System.out.println("Started");
			// lets wait until we're killed.
			Object lock = new Object();
			synchronized (lock) {
				lock.wait();
			}
		} catch (JMSException e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
			Exception le = e.getLinkedException();
			System.out.println("Reason: " + le);
			if (le != null) {
				le.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}
}