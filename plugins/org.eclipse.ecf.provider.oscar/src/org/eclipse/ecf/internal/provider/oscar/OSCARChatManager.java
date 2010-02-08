/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar;

import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.oscar.icqlib.IOSCARConnectable;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesSerializer;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.history.IHistory;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.*;
import org.eclipse.ecf.presence.search.message.IMessageSearchManager;
import org.eclipse.ecf.provider.oscar.identity.OSCARID;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.OscarInterface;

public class OSCARChatManager implements IChatManager, IOSCARConnectable {

	OscarConnection connection;

	private final Vector messageListeners = new Vector();

	private final IChatMessageSender chatMessageSender = new IChatMessageSender() {

		private String getUIN(ID id) {
			return ((OSCARID) id).getUin();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.im.IChatMessageSender#sendChatMessage(org.eclipse.ecf.core.identity.ID,
		 * 		org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.presence.im.IChatMessage.Type,
		 * 		java.lang.String, java.lang.String)
		 */
		public void sendChatMessage(ID toID, ID threadID, IChatMessage.Type type, String subject, String body,
				Map properties) throws ECFException {
			if (toID == null)
				throw new ECFException(Messages.OSCAR_CHAT_EXCEPTION_RECEIVER_NULL);

			if (!(toID instanceof OSCARID))
				throw new ECFException(Messages.OSCAR_CHAT_EXCEPTION_ID_IS_NOT_OSCARID);

			try {
				OscarInterface.sendBasicMessage(connection, getUIN(toID), MessagePropertiesSerializer.serialize(body,
					properties));
			} catch (final Exception e) {
				throw new ECFException(Messages.OSCAR_CHAT_EXCEPTION_SEND_FAILED, e);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.im.IChatMessageSender#sendChatMessage(org.eclipse.ecf.core.identity.ID,
		 * 			java.lang.String)
		 */
		public void sendChatMessage(ID toID, String body) throws ECFException {
			sendChatMessage(toID, null, IChatMessage.Type.CHAT, null, body, null);
		}

	};

	protected IHistoryManager historyManager = new IHistoryManager() {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ecf.presence.im.IChatHistoryManager#getHistory(org.eclipse.ecf.core.identity.ID,
		 * 			java.util.Map)
		 */
		public IHistory getHistory(ID partnerID, Map options) {
			// TODO develop customizable local storage
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			if (adapter == null)
				return null;

			if (adapter.isInstance(this))
				return this;

			final IAdapterManager adapterManager = OSCARPlugin.getDefault().getAdapterManager();
			return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
		}

		public boolean isActive() {
			return false;
		}

		public void setActive(boolean active) {
			// TODO Auto-generated method stub
		}
	};

	protected ITypingMessageSender typingMessageSender = new ITypingMessageSender() {

		public void sendTypingMessage(ID toID, boolean isTyping, String body) throws ECFException {
			if (toID == null)
				throw new ECFException(Messages.OSCAR_CHAT_EXCEPTION_RECEIVER_NULL);

			try {
				// TODO send notification about typing (icqlib do not support this function now)
			} catch (final Exception e) {
				throw new ECFException(Messages.OSCAR_CHAT_EXCEPTION_SEND_FAILED, e);
			}
		}
	};

	public void addMessageListener(IIMMessageListener listener) {
		messageListeners.add(listener);
	}

	public void removeMessageListener(IIMMessageListener listener) {
		messageListeners.remove(listener);
	}

	public IChat createChat(ID targetUser, IIMMessageListener messageListener) {
		// TODO Auto-generated method stub
		return null;
	}

	public IChatMessageSender getChatMessageSender() {
		return chatMessageSender;
	}

	public IHistoryManager getHistoryManager() {
		return historyManager;
	}

	public IMessageSearchManager getMessageSearchManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypingMessageSender getTypingMessageSender() {
		return typingMessageSender;
	}

	public void setConnection(OscarConnection connection) {
		this.connection = connection;
	}

	private void fireMessageEvent(IIMMessageEvent event) {
		for (final Iterator i = messageListeners.iterator(); i.hasNext();) {
			final IIMMessageListener l = (IIMMessageListener) i.next();
			l.handleMessageEvent(event);
		}
	}

	public void fireChatMessage(ID fromID, ID threadID, String body, Map properties) {
		fireMessageEvent(new ChatMessageEvent(fromID, new ChatMessage(fromID, threadID, IChatMessage.Type.CHAT, null,
				body, properties)));
	}

	public void fireTypingMessage(ID fromID, ITypingMessage typingMessage) {
		fireMessageEvent(new TypingMessageEvent(fromID, typingMessage));
	}
}
