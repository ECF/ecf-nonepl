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

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.provider.internal.net4j.datashare.Net4jChannelContainerAdapter;
import org.eclipse.ecf.provider.internal.net4j.protocol.ECFClientProtocol;
import org.eclipse.ecf.provider.net4j.INet4jConnectorProvider;
import org.eclipse.ecf.provider.net4j.INet4jContainer;
import org.eclipse.ecf.provider.net4j.Net4jContainerNamespace;

/**
 * @author Eike Stepper
 */
public class Net4jContainer extends BaseContainer implements INet4jContainer
{
  private INet4jConnectorProvider connectorProvider;

  private Object connectorProviderLock = new Object();

  private IConnector connector;

  private ECFClientProtocol protocol = new ECFClientProtocol(this);

  public Net4jContainer() throws IDCreateException
  {
  }

  public Net4jContainer(ID id)
  {
    super(id);
  }

  public Namespace getConnectNamespace()
  {
    return new Net4jContainerNamespace();
  }

  public INet4jConnectorProvider getConnectorProvider()
  {
    synchronized (connectorProviderLock)
    {
      if (connectorProvider == null)
      {
        connectorProvider = INet4jConnectorProvider.DEFAULT;
      }

      return connectorProvider;
    }
  }

  public void setConnectorProvider(INet4jConnectorProvider connectorProvider)
  {
    synchronized (connectorProviderLock)
    {
      this.connectorProvider = connectorProvider;
    }
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public ECFClientProtocol getProtocol()
  {
    return protocol;
  }

  @Override
  public Net4jChannelContainerAdapter getChannelContainerAdapter()
  {
    return (Net4jChannelContainerAdapter)super.getChannelContainerAdapter();
  }

  @Override
  protected Object createAdapter(Class<?> serviceType)
  {
    if (serviceType == IChannelContainerAdapter.class)
    {
      return new Net4jChannelContainerAdapter(this);
    }

    return super.createAdapter(serviceType);
  }

  @Override
  protected void doConnect(ID targetID, IConnectContext connectContext) throws ContainerConnectException
  {
    connector = getConnectorProvider().getConnector(targetID, connectContext);
    protocol.open(connector);
    if (!protocol.openSession())
    {
      throw new ContainerConnectException("Server refused connection: " + targetID);
    }
  }

  @Override
  protected void doDisconnect()
  {
    protocol.close();
  }
}
