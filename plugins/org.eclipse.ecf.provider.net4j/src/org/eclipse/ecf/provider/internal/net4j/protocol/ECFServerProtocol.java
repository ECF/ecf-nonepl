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

import org.eclipse.net4j.protocol.ServerProtocolFactory;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;
import org.eclipse.ecf.provider.internal.net4j.server.ECFServer;
import org.eclipse.ecf.provider.internal.net4j.server.ECFSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ECFServerProtocol extends SignalProtocol<ECFSession> implements IECFProtocol
{
  public ECFServerProtocol()
  {
  }

  public String getType()
  {
    return TYPE;
  }

  public void notifyMessage(final int channelIndex, final ID sender, final byte[] message) throws Exception
  {
    new Request(this, SIGNAL_NOTIFY_MESSAGE)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out) throws IOException
      {
        out.writeInt(channelIndex);
        out.writeObject(sender);
        out.writeByteArray(message);
      }
    }.send();
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL_OPEN_SESSION:
      return new IndicationWithResponse(this, SIGNAL_OPEN_SESSION)
      {
        @Override
        protected void indicating(ExtendedDataInputStream in) throws IOException
        {
          ID containerID = (ID)in.readObject(OM.class.getClassLoader());
          ECFSession session = ECFServer.INSTANCE.openSession(containerID, ECFServerProtocol.this);
          setInfraStructure(session);
        }

        @Override
        protected void responding(ExtendedDataOutputStream out) throws IOException
        {
          out.writeBoolean(getInfraStructure() != null);
        }
      };

    case SIGNAL_CONNECT_CHANNEL:
      return new IndicationWithResponse(this, SIGNAL_CONNECT_CHANNEL)
      {
        int channelIndex;

        @Override
        protected void indicating(ExtendedDataInputStream in) throws IOException
        {
          ID channelID = (ID)in.readObject(OM.class.getClassLoader());
          channelIndex = getInfraStructure().handleConnectChannel(channelID);
        }

        @Override
        protected void responding(ExtendedDataOutputStream out) throws IOException
        {
          out.writeInt(channelIndex);
        }
      };

    case SIGNAL_DISCONNECT_CHANNEL:
      return new Indication(this, SIGNAL_DISCONNECT_CHANNEL)
      {
        @Override
        protected void indicating(ExtendedDataInputStream in) throws IOException
        {
          int channelIndex = in.readInt();
          getInfraStructure().handleDisconnectChannel(channelIndex);
        }
      };

    case SIGNAL_SEND_MESSAGE:
      return new Indication(this, SIGNAL_SEND_MESSAGE)
      {
        @Override
        protected void indicating(ExtendedDataInputStream in) throws IOException
        {
          int channelIndex = in.readInt();
          byte[] message = in.readByteArray();
          getInfraStructure().handleChannelMessage(channelIndex, message);
        }
      };

    default:
      return null;
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ECFServer.INSTANCE.closeSession(getInfraStructure());
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ServerProtocolFactory
  {
    public Factory()
    {
      super(TYPE);
    }

    public Object create(String description) throws ProductCreationException
    {
      return new ECFServerProtocol();
    }
  }
}
