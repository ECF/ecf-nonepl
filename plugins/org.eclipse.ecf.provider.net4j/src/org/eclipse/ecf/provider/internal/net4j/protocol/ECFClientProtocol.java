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

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.internal.net4j.Net4jContainer;
import org.eclipse.ecf.provider.internal.net4j.bundle.OM;
import org.eclipse.ecf.provider.internal.net4j.datashare.Net4jChannelContainerAdapter;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ECFClientProtocol extends SignalProtocol<Net4jContainer> implements IECFProtocol
{
  public ECFClientProtocol(Net4jContainer container)
  {
    setInfraStructure(container);
  }

  public String getType()
  {
    return TYPE;
  }

  public boolean openSession() throws ContainerConnectException
  {
    try
    {
      return new RequestWithConfirmation<Boolean>(this, SIGNAL_OPEN_SESSION)
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws IOException
        {
          out.writeObject(getInfraStructure().getID());
        }

        @Override
        protected Boolean confirming(ExtendedDataInputStream in) throws IOException
        {
          return in.readBoolean();
        }
      }.send();
    }
    catch (Exception ex)
    {
      throw new ContainerConnectException(ex);
    }
  }

  public int connectChannel(final ID channelID) throws ECFException
  {
    try
    {
      return new RequestWithConfirmation<Integer>(this, SIGNAL_CONNECT_CHANNEL)
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws IOException
        {
          out.writeObject(channelID);
        }

        @Override
        protected Integer confirming(ExtendedDataInputStream in) throws IOException
        {
          return in.readInt();
        }
      }.send();
    }
    catch (Exception ex)
    {
      throw new ECFException(ex);
    }
  }

  public void disconnectChannel(final int channelIndex) throws ECFException
  {
    try
    {
      new Request(this, SIGNAL_DISCONNECT_CHANNEL)
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws IOException
        {
          out.writeInt(channelIndex);
        }
      }.send();
    }
    catch (Exception ex)
    {
      throw new ECFException(ex);
    }
  }

  public void sendChannelMessage(final int channelIndex, final byte[] message) throws ECFException
  {
    try
    {
      new Request(this, SIGNAL_SEND_MESSAGE)
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws IOException
        {
          out.writeInt(channelIndex);
          out.writeByteArray(message);
        }
      }.send();
    }
    catch (Exception ex)
    {
      throw new ECFException(ex);
    }
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL_NOTIFY_MESSAGE:
      return new Indication(this, SIGNAL_NOTIFY_MESSAGE)
      {
        @Override
        protected void indicating(ExtendedDataInputStream in) throws IOException
        {
          int channelIndex = in.readInt();
          ID sender = (ID)in.readObject(OM.class.getClassLoader());
          byte[] message = in.readByteArray();

          Net4jChannelContainerAdapter adapter = getInfraStructure().getChannelContainerAdapter();
          adapter.handleMessage(channelIndex, sender, message);
        }
      };

    default:
      return null;
    }
  }
}
