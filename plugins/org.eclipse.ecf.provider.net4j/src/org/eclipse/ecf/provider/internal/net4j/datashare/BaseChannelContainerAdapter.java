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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelContainerListener;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelActivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelDeactivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerEvent;
import org.eclipse.ecf.provider.internal.net4j.BaseContainer;
import org.eclipse.ecf.provider.internal.net4j.BaseContainerAdapter;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class BaseChannelContainerAdapter extends BaseContainerAdapter implements IChannelContainerAdapter
{
  private final List<IChannelContainerListener> listeners = new ArrayList<IChannelContainerListener>(5);

  private final Map<ID, BaseChannel> channels = new HashMap<ID, BaseChannel>();

  public BaseChannelContainerAdapter(BaseContainer container)
  {
    super(container);
  }

  public void addListener(IChannelContainerListener listener)
  {
    synchronized (listeners)
    {
      if (!listeners.contains(listener))
      {
        listeners.add(listener);
      }
    }
  }

  public void removeListener(IChannelContainerListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }

  public IChannelContainerListener[] getListeners()
  {
    synchronized (listeners)
    {
      return listeners.toArray(new IChannelContainerListener[listeners.size()]);
    }
  }

  protected void fireChannelContainerEvent(IChannelContainerEvent event)
  {
    for (IChannelContainerListener listener : getListeners())
    {
      try
      {
        listener.handleChannelContainerEvent(event);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public IChannel createChannel(final ID channelID, IChannelListener listener, Map properties) throws ECFException
  {
    synchronized (channels)
    {
      // Only a local check. Server-based validation occurs later...
      if (channels.containsKey(channelID))
      {
        throw new ECFException("Channel ID already exists: " + channelID);
      }
    }

    BaseChannel channel = doCreateChannel(channelID, listener, properties);
    channel.connect();
    channelConnected(channel);
    synchronized (channels)
    {
      channels.put(channelID, channel);
    }

    fireChannelContainerEvent(new IChannelContainerChannelActivatedEvent()
    {
      public ID getChannelContainerID()
      {
        return getContainer().getID();
      }

      public ID getChannelID()
      {
        return channelID;
      }
    });

    return channel;
  }

  public IChannel createChannel(IChannelConfig config) throws ECFException
  {
    return createChannel(config.getID(), config.getListener(), config.getProperties());
  }

  public boolean removeChannel(final ID channelID)
  {
    BaseChannel channel;
    synchronized (channels)
    {
      channel = channels.remove(channelID);
    }

    if (channel != null)
    {
      channel.dispose();
      channelClosed(channel);
      fireChannelContainerEvent(new IChannelContainerChannelDeactivatedEvent()
      {
        public ID getChannelContainerID()
        {
          return getContainer().getID();
        }

        public ID getChannelID()
        {
          return channelID;
        }
      });

      return true;
    }

    return false;
  }

  public IChannel getChannel(ID channelID)
  {
    synchronized (channels)
    {
      return channels.get(channelID);
    }
  }

  @Override
  public void dispose()
  {
    synchronized (listeners)
    {
      listeners.clear();
    }
  }

  /**
   * Must not return <code>null</code>.
   */
  protected abstract BaseChannel doCreateChannel(ID channelID, IChannelListener listener, Map<?, ?> properties)
      throws ECFException;

  protected void channelConnected(BaseChannel channel)
  {
  }

  protected void channelClosed(BaseChannel channel)
  {
  }
}
