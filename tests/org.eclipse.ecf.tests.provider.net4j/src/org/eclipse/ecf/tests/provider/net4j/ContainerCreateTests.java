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

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ManagedContainer;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.provider.internal.net4j.Net4jContainer;
import org.eclipse.ecf.provider.internal.net4j.datashare.Net4jChannelContainerAdapter;
import org.eclipse.ecf.provider.net4j.INet4jConnectorProvider;
import org.eclipse.ecf.provider.net4j.Net4jContainerNamespace;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public class ContainerCreateTests extends TestCase
{
  public void testCreateContainer() throws Exception
  {
    IContainer container1 = createContainer();
    IChannel channel = createChannel(container1, 1);

    IContainer container2 = createContainer();
    createChannel(container2, 1);

    channel.sendMessage("Eike Stepper".getBytes());
    Thread.sleep(200);
  }

  private IContainer createContainer() throws Exception
  {
    Net4jContainer container = new Net4jContainer();
    container.setConnectorProvider(new INet4jConnectorProvider()
    {
      public IConnector getConnector(ID targetID, IConnectContext connectContext)
      {
        ManagedContainer container = new ManagedContainer();
        Net4jUtil.prepareContainer(container);
        TCPUtil.prepareContainer(container);
        return TCPUtil.getConnector(container, "localhost");
      }
    });

    ID targetID = new Net4jContainerNamespace().createInstance(new String[] { "tcp:localhost" });
    container.connect(targetID, null);
    return container;
  }

  private IChannel createChannel(IContainer container, int i) throws Exception
  {
    IChannelContainerAdapter adapter = (IChannelContainerAdapter)container.getAdapter(IChannelContainerAdapter.class);
    assertTrue(adapter instanceof Net4jChannelContainerAdapter);

    ID channelID = new StringID.StringIDNamespace().createInstance(new String[] { "CHANNEL-" + i });
    return adapter.createChannel(channelID, new IChannelListener()
    {
      public void handleChannelEvent(IChannelEvent event)
      {
        if (event instanceof IChannelMessageEvent)
        {
          IChannelMessageEvent e = (IChannelMessageEvent)event;
          System.out.println("Sender:  " + e.getFromContainerID());
          System.out.println("Channel: " + e.getChannelID());
          System.out.println("Message: " + new String(e.getData()));
        }
      }
    }, null);
  }
}
