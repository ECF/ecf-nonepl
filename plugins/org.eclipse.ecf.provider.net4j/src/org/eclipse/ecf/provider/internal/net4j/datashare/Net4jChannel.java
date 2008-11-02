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

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.provider.internal.net4j.protocol.ECFClientProtocol;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Net4jChannel extends BaseChannel
{
  private ECFClientProtocol protocol;

  private int channelIndex;

  public Net4jChannel(ECFClientProtocol protocol, ID id, IChannelListener listener, Map<?, ?> properties)
  {
    super(id, listener, properties);
    this.protocol = protocol;
  }

  public int getChannelIndex()
  {
    return channelIndex;
  }

  public void sendMessage(ID receiver, byte[] message) throws ECFException
  {
    Assert.isTrue(receiver == null);
    protocol.sendChannelMessage(channelIndex, message);
  }

  public void handleMessage(final ID sender, final byte[] message)
  {
    fireChannelEvent(new IChannelMessageEvent()
    {
      public ID getFromContainerID()
      {
        return sender;
      }

      public ID getChannelID()
      {
        return getID();
      }

      public byte[] getData()
      {
        return message;
      }
    });
  }

  @Override
  protected void doConnect() throws ECFException
  {
    channelIndex = protocol.connectChannel(getID());
  }

  @Override
  protected void doDisconnect() throws ECFException
  {
    protocol.disconnectChannel(channelIndex);
  }
}
