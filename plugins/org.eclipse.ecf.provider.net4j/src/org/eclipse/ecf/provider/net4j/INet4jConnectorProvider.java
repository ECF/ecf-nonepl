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
package org.eclipse.ecf.provider.net4j;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.internal.net4j.Net4jConnectorProvider;

/**
 * @author Eike Stepper
 */
public interface INet4jConnectorProvider
{
  public static final INet4jConnectorProvider DEFAULT = new Net4jConnectorProvider(IPluginContainer.INSTANCE);

  public IConnector getConnector(ID targetID, IConnectContext connectContext);
}
