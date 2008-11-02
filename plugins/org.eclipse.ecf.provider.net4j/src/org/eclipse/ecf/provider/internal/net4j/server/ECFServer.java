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

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.collection.DynamicArray;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;
import org.eclipse.ecf.provider.internal.net4j.protocol.ECFServerProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class ECFServer
{
  public static final ECFServer INSTANCE = new ECFServer();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ECFServer.class);

  private ConcurrentMap<ID, ECFSession> sessions = new ConcurrentHashMap<ID, ECFSession>();

  private Map<ID, ECFChannel> channels = new HashMap<ID, ECFChannel>();

  private DynamicArray<ECFChannel> channelArray = new DynamicArray<ECFChannel>();

  public ECFServer()
  {
  }

  public ECFSession openSession(ID containerID, ECFServerProtocol protocol)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Opening session for container {0}", containerID);
    }

    ECFSession session = new ECFSession(containerID, protocol);
    if (sessions.putIfAbsent(containerID, session) == null)
    {
      return session;
    }

    return null;
  }

  public void closeSession(ECFSession session)
  {
    sessions.remove(session.getContainerID());
    session.close();
  }

  public ECFChannel getChannel(int channelIndex)
  {
    synchronized (channels)
    {
      return channelArray.get(channelIndex);
    }
  }

  public ECFChannel getChannel(ID channelID)
  {
    synchronized (channels)
    {
      ECFChannel channel = channels.get(channelID);
      if (channel == null)
      {
        channel = new ECFChannel(channelID);
        int channelIndex = channelArray.add(channel);
        channel.setIndex(channelIndex);
        channels.put(channelID, channel);
      }

      return channel;
    }
  }

  public static void main(String[] args) throws Exception
  {
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);

    ManagedContainer container = new ManagedContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    container.registerFactory(new ECFServerProtocol.Factory());

    TCPUtil.getAcceptor(container, "0.0.0.0");
    while (System.in.available() == 0)
    {
      Thread.sleep(100);
    }

    container.deactivate();
  }
}
