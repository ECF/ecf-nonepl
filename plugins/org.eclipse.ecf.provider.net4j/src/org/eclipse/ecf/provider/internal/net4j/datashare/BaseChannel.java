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

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelConnectEvent;
import org.eclipse.ecf.datashare.events.IChannelDisconnectEvent;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class BaseChannel implements IChannel
{
  private ID channelID;

  private Map<?, ?> properties;

  private IChannelListener listener;

  private Object listenerLock = new Object();

  private Object connectLock = new Object();

  public BaseChannel(ID id, IChannelListener listener, Map<?, ?> properties)
  {
    Assert.isNotNull(id);
    channelID = id;
    this.listener = listener;
    this.properties = properties;
  }

  public ID getID()
  {
    return channelID;
  }

  public Map<?, ?> getProperties()
  {
    return properties;
  }

  public IChannelListener getListener()
  {
    synchronized (listenerLock)
    {
      return listener;
    }
  }

  public IChannelListener setListener(IChannelListener listener)
  {
    synchronized (listenerLock)
    {
      IChannelListener old = this.listener;
      this.listener = listener;
      return old;
    }
  }

  public void connect() throws ECFException
  {
    synchronized (connectLock)
    {
      doConnect();
    }

    fireChannelEvent(new IChannelConnectEvent()
    {
      public ID getChannelID()
      {
        return channelID;
      }

      public ID getTargetID()
      {
        // TODO Scott: What is a channel target ID? What is a "target group"?
        throw new UnsupportedOperationException();
      }
    });
  }

  public void sendMessage(byte[] message) throws ECFException
  {
    // TODO Scott: What is the rationale behind the receiverID?
    sendMessage(null, message);
  }

  public void dispose()
  {
    try
    {
      synchronized (connectLock)
      {
        doDisconnect();
      }
    }
    catch (ECFException ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      fireChannelEvent(new IChannelDisconnectEvent()
      {
        public ID getChannelID()
        {
          return channelID;
        }

        public ID getTargetID()
        {
          // TODO Scott: What is a channel target ID? What is a "target group"?
          throw new UnsupportedOperationException();
        }
      });
    }
  }

  @SuppressWarnings("unchecked")
  public Object getAdapter(Class serviceType)
  {
    if (serviceType == null)
    {
      return null;
    }

    if (serviceType.isAssignableFrom(getClass()))
    {
      return this;
    }

    IAdapterManager adapterManager = OM.getAdapterManager();
    if (adapterManager == null)
    {
      return null;
    }

    return adapterManager.getAdapter(this, serviceType);
  }

  /**
   * Fires a channel event
   * 
   * @param event
   */
  protected void fireChannelEvent(IChannelEvent event)
  {
    IChannelListener listener = getListener();
    if (listener != null)
    {
      listener.handleChannelEvent(event);
    }
  }

  protected abstract void doConnect() throws ECFException;

  protected abstract void doDisconnect() throws ECFException;
}
