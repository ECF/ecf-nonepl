/** 
 * 
 * Copyright 2004 Protique Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 **/
package org.eclipse.ecf.provider.jms.app;

import java.net.URI;
import javax.jms.JMSException;
import org.activemq.ActiveMQConnection;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jms.container.JMSServerSOContainer;
import org.eclipse.ecf.provider.jms.identity.JMSNamespace;

/**
 * A simple command line tool which runs a JMS Message Broker on the command
 * line
 * 
 * @version $Revision$
 */
public class ServerApplication {
	public static final int DEFAULT_KEEPALIVE = 30000;
	static JMSServerSOContainer groups[] = null;

	protected static void initializeNamespace() {
		IDFactory.getDefault().addNamespace(new JMSNamespace());
	}

	protected static String removeLeadingSlashes(URI aURI) {
		String name = aURI.getPath();
		while (name.indexOf('/') != -1) {
			name = name.substring(1);
		}
		return name;
	}

	protected static void setupSharedObjectContainers(
			ECFBrokerContainerImpl broker, String[] urls) throws Exception {
		if (urls == null || urls.length == 0)
			throw new NullPointerException("must have at least one group url");
		initializeNamespace();
		groups = new JMSServerSOContainer[urls.length];
		for (int i = 0; i < urls.length; i++) {
			String serverid = urls[i];
			ID groupID = IDFactory.getDefault().createID(JMSNamespace.JMS_NAMESPACE_NAME,
					serverid);
			URI aURI = new URI(groupID.getName());
			String groupName = removeLeadingSlashes(new URI(aURI
					.getSchemeSpecificPart()));
			SOContainerConfig newConfig = new SOContainerConfig(groupID);
			JMSServerSOContainer newCont = new JMSServerSOContainer(newConfig,
					DEFAULT_KEEPALIVE);
			broker.addSOContainer(groupName, newCont);
			newCont.initialize();
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
			System.out.println("Creating container for " + url);
			setupSharedObjectContainers((ECFBrokerContainerImpl) container,
					new String[] { url });
			container.setOnTheAir(true);
			System.out.println("Server started");
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