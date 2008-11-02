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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class BaseContainerAdapter implements IAdaptable
{
  private BaseContainer container;

  private Map<String, Object> adapters = new HashMap<String, Object>();

  public BaseContainerAdapter(BaseContainer container)
  {
    Assert.isNotNull(container);
    this.container = container;
  }

  public BaseContainer getContainer()
  {
    return container;
  }

  @SuppressWarnings("unchecked")
  public final Object getAdapter(Class serviceType)
  {
    if (serviceType == null)
    {
      return null;
    }

    if (serviceType == IContainer.class)
    {
      return getContainer();
    }

    if (serviceType.isAssignableFrom(getClass()))
    {
      return this;
    }

    String name = serviceType.getName();
    synchronized (adapters)
    {
      Object adapter = adapters.get(name);
      if (adapter == null)
      {
        adapter = createAdapter(serviceType);
        if (adapter != null)
        {
          adapters.put(name, adapter);
          return adapter;
        }
      }
    }

    return null;
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

  public void dispose()
  {
  }
}
