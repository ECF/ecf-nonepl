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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * @author Eike Stepper
 */
public class Net4jContainerNamespace extends Namespace
{
  private static final long serialVersionUID = 1L;

  public static final String SCHEME = "net4j";

  public static final String NAME = "ecf.namespace.net4j";

  public Net4jContainerNamespace()
  {
    super(Net4jContainerID.class.getName(), "Net4jContainerID Namespace");
  }

  public Net4jContainerNamespace(String name, String desc)
  {
    super(name, desc);
  }

  @Override
  public ID createInstance(Object[] parameters) throws IDCreateException
  {
    // XXX Note that this assumes that a unique string is provided for creating the ID
    // e.g. IDFactory.getDefault().createID("myid");
    if (parameters == null || parameters.length < 1)
    {
      throw new IDCreateException("parameters not of correct size");
    }

    if (!(parameters[0] instanceof String))
    {
      throw new IDCreateException("parameter not of String type");
    }

    return new Net4jContainerID(this, (String)parameters[0]);
  }

  @Override
  public String getScheme()
  {
    return SCHEME;
  }
}
