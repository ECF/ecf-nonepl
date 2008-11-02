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
package org.eclipse.ecf.provider.internal.net4j.protocol;

/**
 * @author Eike Stepper
 */
public interface IECFProtocol
{
  public static final String TYPE = "ecf";

  public static final short SIGNAL_OPEN_SESSION = 0;

  public static final short SIGNAL_CONNECT_CHANNEL = 1;

  public static final short SIGNAL_DISCONNECT_CHANNEL = 2;

  public static final short SIGNAL_SEND_MESSAGE = 3;

  public static final short SIGNAL_NOTIFY_MESSAGE = 4;
}
