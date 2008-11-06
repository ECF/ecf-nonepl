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

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;

/**
 * @author Eike Stepper
 */
public class Net4jContainerInstantiator extends BaseContainerInstantiator
{
  @Override
  public IContainer createInstance(ContainerTypeDescription description, Object[] parameters)
      throws ContainerCreateException
  {
    try
    {
      if (parameters != null && parameters.length > 0)
      {
        if (parameters[0] instanceof ID)
        {
          return new Net4jContainer((ID)parameters[0]);
        }

        if (parameters[0] instanceof String)
        {
          return new Net4jContainer(IDFactory.getDefault().createStringID((String)parameters[0]));
        }
      }

      return new Net4jContainer();
    }
    catch (IDCreateException ex)
    {
      throw new ContainerCreateException("Exception creating ID for net4j container", ex);
    }
  }

  @Override
  public String[] getSupportedAdapterTypes(ContainerTypeDescription description)
  {
    // TODO Return String [] with adapter types supported for the given description
    return super.getSupportedAdapterTypes(description);
  }
}