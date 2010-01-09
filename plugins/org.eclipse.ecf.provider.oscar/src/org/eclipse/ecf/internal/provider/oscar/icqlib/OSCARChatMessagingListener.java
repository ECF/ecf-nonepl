/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.icqlib;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.oscar.*;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesDeserializer;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesDeserializer.Message;
import org.eclipse.ecf.provider.oscar.identity.OSCARID;
import ru.caffeineim.protocols.icq.integration.events.*;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;

public class OSCARChatMessagingListener implements MessagingListener {

	public static final String URL = "URL"; //$NON-NLS-1$

	private OSCARChatManager manager;

	private Namespace namespace;

	public OSCARChatMessagingListener(OSCARChatManager manager, Namespace namespace) {
		this.manager = manager;
		this.namespace = namespace;
	}

	public void onIncomingMessage(IncomingMessageEvent e) {
		try {
			Message message = MessagePropertiesDeserializer.deserialize(e.getMessage());
			manager.fireChatMessage(new OSCARID(namespace, e.getSenderID()), null, message.getMessage(), message
				.getProperties());
		} catch (URISyntaxException ex) {
			OSCARPlugin.log(Messages.OSCAR_NAMESPACE_EXCEPTION_ID_CREATE, ex);
		}
	}

	public void onIncomingUrl(IncomingUrlEvent e) {
		Map map = new HashMap();
		map.put(URL, e.getUrl());

		try {
			manager.fireChatMessage(new OSCARID(namespace, e.getSenderID()), null, e.getMessage(), map);
		} catch (URISyntaxException ex) {
			OSCARPlugin.log(Messages.OSCAR_NAMESPACE_EXCEPTION_ID_CREATE, ex);
		}
	}

	public void onMessageAck(MessageAckEvent e) {
		// XXX
	}

	public void onMessageError(MessageErrorEvent e) {
		// TODO add reaction on error message
	}

	public void onMessageMissed(MessageMissedEvent e) {
		// XXX
	}

	public void onOfflineMessage(OfflineMessageEvent e) {
		try {
			Message message = MessagePropertiesDeserializer.deserialize(e.getMessage());
			manager.fireChatMessage(new OSCARID(namespace, e.getSenderUin()), null, message.getMessage(), message
				.getProperties());
		} catch (URISyntaxException ex) {
			OSCARPlugin.log(Messages.OSCAR_NAMESPACE_EXCEPTION_ID_CREATE, ex);
		}
	}
}
