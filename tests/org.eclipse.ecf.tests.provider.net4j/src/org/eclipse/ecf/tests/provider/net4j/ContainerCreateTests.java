/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper (Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.ecf.tests.provider.net4j;

import junit.framework.TestCase;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.provider.internal.net4j.datashare.Net4jChannelContainerAdapter;

/**
 * @author Eike Stepper
 */
public class ContainerCreateTests extends TestCase {

	public void testCreateContainer() throws Exception {
		IContainer container1 = createAndConnectContainer();
		IChannel channel = createChannel(container1, 1);

		IContainer container2 = createAndConnectContainer();
		createChannel(container2, 1);

		channel.sendMessage("Eike Stepper".getBytes());
		Thread.sleep(200);
	}

	private IContainer createAndConnectContainer() throws Exception {
		IContainer container = ContainerFactory.getDefault().createContainer(
				OM.CLIENT_CONTAINER_FACTORY);

		ID targetID = IDFactory.getDefault().createID(
				container.getConnectNamespace(), "tcp:localhost");
		container.connect(targetID, null);
		return container;
	}

	private IChannel createChannel(IContainer container, int i)
			throws Exception {
		IChannelContainerAdapter adapter = (IChannelContainerAdapter) container
				.getAdapter(IChannelContainerAdapter.class);
		assertTrue(adapter instanceof Net4jChannelContainerAdapter);

		ID channelID = new StringID.StringIDNamespace()
				.createInstance(new String[] { "CHANNEL-" + i });
		return adapter.createChannel(channelID, new IChannelListener() {
			public void handleChannelEvent(IChannelEvent event) {
				if (event instanceof IChannelMessageEvent) {
					IChannelMessageEvent e = (IChannelMessageEvent) event;
					System.out.println("Sender:  " + e.getFromContainerID());
					System.out.println("Channel: " + e.getChannelID());
					System.out.println("Message: " + new String(e.getData()));
				}
			}
		}, null);
	}
}
