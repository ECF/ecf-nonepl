/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Eike Stepper - Net4j integration
 *****************************************************************************/
package org.eclipse.ecf.provider.net4j;

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

/**
 * @author Eike Stepper
 */
public class Net4jContainerID extends StringID
{
  private static final long serialVersionUID = 1L;

  public Net4jContainerID(Namespace namespace, String name)
  {
    super(namespace, name);
  }
}
