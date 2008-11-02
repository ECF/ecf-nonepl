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

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.net4j.INet4jConnectorProvider;
import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class Net4jConnectorProvider implements INet4jConnectorProvider
{
  public static final char SEPARATOR_CHAR = ':';

  private IManagedContainer managedContainer;

  public Net4jConnectorProvider(IManagedContainer managedContainer)
  {
    this.managedContainer = managedContainer;
  }

  public final IManagedContainer getManagedContainer()
  {
    return managedContainer;
  }

  public final IConnector getConnector(ID targetID, IConnectContext connectContext)
  {
    String factoryType = getFactoryType(targetID, connectContext);
    String description = getDescription(targetID, connectContext);
    return (IConnector)managedContainer.getElement(ConnectorFactory.PRODUCT_GROUP, factoryType, description);
  }

  protected String getFactoryType(ID targetID, IConnectContext connectContext)
  {
    String name = targetID.getName();
    int sep = name.indexOf(SEPARATOR_CHAR);
    if (sep == -1)
    {
      return name;
    }

    return name.substring(0, sep);
  }

  protected String getDescription(ID targetID, IConnectContext connectContext)
  {
    String name = targetID.getName();
    int sep = name.indexOf(SEPARATOR_CHAR);
    if (sep == -1)
    {
      return null;
    }

    return name.substring(sep + 1);
  }
}
