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
package org.eclipse.ecf.provider.internal.net4j.server;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.internal.net4j.protocol.ECFServerProtocol;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class ECFSession
{
  private ID containerID;

  private ECFServerProtocol protocol;

  private ConcurrentMap<ECFChannel, ECFChannel> channels = new ConcurrentHashMap<ECFChannel, ECFChannel>();

  public ECFSession(ID containerID, ECFServerProtocol protocol)
  {
    this.containerID = containerID;
    this.protocol = protocol;
  }

  public ID getContainerID()
  {
    return containerID;
  }

  public ECFServerProtocol getProtocol()
  {
    return protocol;
  }

  public Set<ECFChannel> getChannels()
  {
    return channels.keySet();
  }

  public void addChannel(ECFChannel channel)
  {
    channels.put(channel, channel);
  }

  public void removeChannel(ECFChannel channel)
  {
    channels.remove(channel);
  }

  public void notifyMessage(int channelIndex, ID sender, byte[] message) throws Exception
  {
    protocol.notifyMessage(channelIndex, sender, message);
  }

  public int handleConnectChannel(ID channelID)
  {
    ECFChannel channel = ECFServer.INSTANCE.getChannel(channelID);
    channel.addSession(this);
    channels.put(channel, channel);
    return channel.getIndex();
  }

  public void handleDisconnectChannel(int channelIndex)
  {
    ECFChannel channel = ECFServer.INSTANCE.getChannel(channelIndex);
    channel.removeSession(this);
    channels.remove(channel);
  }

  public void handleChannelMessage(int channelIndex, byte[] message)
  {
    ECFChannel channel = ECFServer.INSTANCE.getChannel(channelIndex);
    channel.sendMessage(this, message);
  }

  public void close()
  {
    for (ECFChannel channel : channels.keySet())
    {
      channel.removeSession(this);
    }

    channels.clear();
  }
}
