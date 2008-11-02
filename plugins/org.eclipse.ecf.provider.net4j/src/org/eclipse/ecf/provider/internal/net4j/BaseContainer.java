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
package org.eclipse.ecf.provider.internal.net4j;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class BaseContainer extends AbstractContainer
{
  private ID containerID;

  private ID targetID;

  private Map<String, Object> adapters = new HashMap<String, Object>();

  private IChannelContainerAdapter channelContainerAdapter;

  private Object channelContainerAdapterLock = new Object();

  public BaseContainer() throws IDCreateException
  {
    this(IDFactory.getDefault().createGUID());
  }

  public BaseContainer(ID id)
  {
    Assert.isNotNull(id);
    containerID = id;
  }

  public ID getConnectedID()
  {
    return targetID;
  }

  public ID getID()
  {
    return containerID;
  }

  public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException
  {
    String connectName = getConnectNamespace().getName();
    if (!targetID.getNamespace().getName().equals(connectName))
    {
      throw new ContainerConnectException("targetID not of appropriate Namespace");
    }

    fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));
    doConnect(targetID, connectContext);

    this.targetID = targetID;
    fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));
  }

  public void disconnect()
  {
    fireContainerEvent(new ContainerDisconnectingEvent(getID(), targetID));
    doDisconnect();

    final ID oldID = targetID;
    targetID = null;
    fireContainerEvent(new ContainerDisconnectedEvent(getID(), oldID));
  }

  public IChannelContainerAdapter getChannelContainerAdapter()
  {
    synchronized (channelContainerAdapterLock)
    {
      if (channelContainerAdapter == null)
      {
        channelContainerAdapter = (IChannelContainerAdapter)getAdapter(IChannelContainerAdapter.class);
      }

      return channelContainerAdapter;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Object getAdapter(Class serviceType)
  {
    if (serviceType == null)
    {
      return null;
    }

    if (serviceType.isAssignableFrom(getClass()))
    {
      return this;
    }

    String name = serviceType.getName();
    Object adapter = null;
    synchronized (adapters)
    {
      adapter = adapters.get(name);
      if (adapter == null)
      {
        adapter = createAdapter(serviceType);
        if (adapter != null)
        {
          adapters.put(name, adapter);
        }
      }
    }

    return adapter;
  }

  protected Object createAdapter(Class<?> serviceType)
  {
    IAdapterManager adapterManager = OM.getAdapterManager();
    if (adapterManager == null)
    {
      return null;
    }

    return adapterManager.loadAdapter(this, serviceType.getName());
  }

  protected abstract void doConnect(ID targetID, IConnectContext connectContext) throws ContainerConnectException;

  protected abstract void doDisconnect();
}
