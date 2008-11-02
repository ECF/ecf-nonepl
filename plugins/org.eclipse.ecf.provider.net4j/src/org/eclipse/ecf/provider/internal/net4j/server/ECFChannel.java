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
package org.eclipse.ecf.provider.internal.net4j.server;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class ECFChannel
{
  private ID id;

  private int index;

  private ConcurrentMap<ECFSession, ECFSession> sessions = new ConcurrentHashMap<ECFSession, ECFSession>();

  public ECFChannel(ID id)
  {
    this.id = id;
  }

  public ID getID()
  {
    return id;
  }

  public int getIndex()
  {
    return index;
  }

  public Set<ECFSession> getSessions()
  {
    return sessions.keySet();
  }

  public void addSession(ECFSession session)
  {
    sessions.put(session, session);
  }

  public void removeSession(ECFSession session)
  {
    sessions.remove(session);
  }

  public void sendMessage(ECFSession sender, byte[] message)
  {
    for (ECFSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.notifyMessage(index, sender.getContainerID(), message);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  void setIndex(int index)
  {
    this.index = index;
  }
}
