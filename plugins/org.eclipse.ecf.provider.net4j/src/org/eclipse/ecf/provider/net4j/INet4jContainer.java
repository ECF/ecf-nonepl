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

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;

/**
 * @author Eike Stepper
 */
public interface INet4jContainer extends IContainer
{
  public IChannelContainerAdapter getChannelContainerAdapter();

  public INet4jConnectorProvider getConnectorProvider();

  public void setConnectorProvider(INet4jConnectorProvider connectorProvider);
}
