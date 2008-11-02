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
package org.eclipse.ecf.provider.internal.net4j.datashare;

import org.eclipse.net4j.util.collection.DynamicArray;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.provider.internal.net4j.Net4jContainer;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Net4jChannelContainerAdapter extends BaseChannelContainerAdapter
{
  private DynamicArray<Net4jChannel> channels = new DynamicArray<Net4jChannel>();

  public Net4jChannelContainerAdapter(Net4jContainer container)
  {
    super(container);
  }

  @Override
  public Net4jContainer getContainer()
  {
    return (Net4jContainer)super.getContainer();
  }

  public Namespace getChannelNamespace()
  {
    return IDFactory.getDefault().getNamespaceByName(StringID.class.getName());
  }

  public void handleMessage(int channelIndex, ID sender, byte[] message)
  {
    Net4jChannel channel = channels.get(channelIndex);
    if (channel != null)
    {
      channel.handleMessage(sender, message);
    }
    else
    {
      OM.LOG.warn("Message dropped for channel index " + channelIndex);
    }
  }

  @Override
  protected BaseChannel doCreateChannel(ID channelID, IChannelListener listener, Map<?, ?> properties)
      throws ECFException
  {
    return new Net4jChannel(getContainer().getProtocol(), channelID, listener, properties);
  }

  @Override
  protected void channelConnected(BaseChannel channel)
  {
    super.channelConnected(channel);
    addChannel((Net4jChannel)channel);
  }

  @Override
  protected void channelClosed(BaseChannel channel)
  {
    removeChannel((Net4jChannel)channel);
    super.channelClosed(channel);
  }

  private void addChannel(Net4jChannel channel)
  {
    int channelIndex = channel.getChannelIndex();
    synchronized (channels)
    {
      channels.add(channelIndex, channel);
    }
  }

  private void removeChannel(Net4jChannel channel)
  {
    int channelIndex = channel.getChannelIndex();
    synchronized (channels)
    {
      channels.remove(channelIndex);
    }
  }
}
