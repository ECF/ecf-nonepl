/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.icqlib.listener;

import org.eclipse.ecf.internal.provider.oscar.icqlib.OSCARConnection;

import java.io.IOException;
import org.eclipse.ecf.internal.provider.oscar.Messages;
import org.eclipse.ecf.internal.provider.oscar.OSCARPlugin;
import org.eclipse.ecf.internal.provider.oscar.icqlib.event.OSCARIncomingMessageEvent;
import org.eclipse.ecf.internal.provider.oscar.icqlib.event.OSCARIncomingObjectEvent;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesDeserializer;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesDeserializer.Message;
import org.eclipse.ecf.provider.comm.IAsynchEventHandler;
import ru.caffeineim.protocols.icq.integration.events.*;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;

public class OSCARSOMessagingListener implements MessagingListener {

	private IAsynchEventHandler handler = null;

	private OSCARConnection connection = null;

	public OSCARSOMessagingListener(IAsynchEventHandler handler, OSCARConnection connection) {
		this.handler = handler;
		this.connection = connection;
	}

	public void onIncomingMessage(IncomingMessageEvent e) {
		try {
			final Message message = MessagePropertiesDeserializer.deserialize(e.getMessage());
			final Object val = message.getProperties().get(OSCARConnection.OBJECT_PROPERTY_NAME);

			if (val != null)
				handler.handleAsynchEvent(new OSCARIncomingObjectEvent(connection, val));
			else
				handler.handleAsynchEvent(new OSCARIncomingMessageEvent(connection, message.getMessage()));

		} catch (IOException ex) {
			OSCARPlugin.log(Messages.OSCAR_NAMESPACE_EXCEPTION_ID_CREATE, ex);
		}
	}

	public void onIncomingUrl(IncomingUrlEvent e) {
		// XXX
	}

	public void onMessageAck(MessageAckEvent e) {
		// XXX
	}

	public void onMessageError(MessageErrorEvent e) {
		OSCARPlugin.log(Messages.OSCAR_SO_MESSAGE_ERROR);
	}

	public void onMessageMissed(MessageMissedEvent e) {
		// XXX
	}

	public void onOfflineMessage(OfflineMessageEvent e) {
		// XXX
	}
}
