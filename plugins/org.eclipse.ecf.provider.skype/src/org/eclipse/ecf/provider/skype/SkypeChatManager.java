/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.history.IHistory;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.ChatMessageEvent;
import org.eclipse.ecf.presence.im.IChat;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.im.IChatMessage.Type;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 *
 */
public class SkypeChatManager implements IChatManager {

	private final Vector chatListeners = new Vector();

	private final Map chats = new HashMap();

	private Chat sentChat = null;

	private ChatMessageListener chatMessageListener = new ChatMessageListener() {

		public void chatMessageReceived(ChatMessage chatMessageReceived) throws SkypeException {
			Trace.trace(Activator.PLUGIN_ID, "chatMessageReceived(id=" //$NON-NLS-1$
					+ chatMessageReceived.getId() + ";content=" + chatMessageReceived.getContent() + ";senderid=" + chatMessageReceived.getSenderId() + ";sendername=" + chatMessageReceived.getSenderDisplayName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			fireChatMessageReceived(chatMessageReceived);
		}

		private void fireChatMessageReceived(ChatMessage chatMessageReceived) {
			for (Iterator i = chatListeners.iterator(); i.hasNext();) {
				IIMMessageListener l = (IIMMessageListener) i.next();
				try {
					@SuppressWarnings("unused") //$NON-NLS-1$
					Chat chat = (Chat) chats.get(chatMessageReceived.getChat().getId());
					// XXX eventually we should only show messages from
					// e.g. if (chat != null) { ...
					// chats that we 'know' about.  For now, we'll show all messages
					// to all clients (including the skype normal client)
					ID senderID = new SkypeUserID(chatMessageReceived.getSenderId());
					final IChatMessage chatMessage = new org.eclipse.ecf.presence.im.ChatMessage(senderID, IDFactory.getDefault().createStringID(chatMessageReceived.getId()), Type.CHAT, null, chatMessageReceived.getContent(), createPropertiesForChatMessage(chatMessageReceived));
					l.handleMessageEvent(new ChatMessageEvent(senderID, chatMessage));
				} catch (Exception e) {
					Activator.log("fireChatMessageReceived", e); //$NON-NLS-1$
				}

			}
		}

		private Map createPropertiesForChatMessage(ChatMessage chatMessageReceived) {
			// TODO Auto-generated method stub
			return null;
		}

		public void chatMessageSent(ChatMessage sentChatMessage) throws SkypeException {
			try {
				if (sentChat == null) {
					String chatId = sentChatMessage.getChat().getId();
					Chat chat = (Chat) chats.get(chatId);
					if (chat == null)
						chats.put(chatId, sentChatMessage.getChat());
					fireChatMessageSent(sentChatMessage);
				} else {
					sentChat = null;
				}
			} catch (SkypeException e) {
				Activator.log("chatMessageSent", e); //$NON-NLS-1$
			}
		}

		private void fireChatMessageSent(ChatMessage chatMessageSent) {
			for (Iterator i = chatListeners.iterator(); i.hasNext();) {
				IIMMessageListener l = (IIMMessageListener) i.next();
				try {
					Chat chat = (Chat) chats.get(chatMessageSent.getChat().getId());
					if (chat != null) {
						ID senderID = new SkypeUserID(chatMessageSent.getSenderId());
						final IChatMessage chatMessage = new org.eclipse.ecf.presence.im.ChatMessage(senderID, IDFactory.getDefault().createStringID(chatMessageSent.getId()), Type.CHAT, null, chatMessageSent.getContent(), createPropertiesForChatMessage(chatMessageSent));
						l.handleMessageEvent(new ChatMessageEvent(senderID, chatMessage));
					}
				} catch (Exception e) {
					Activator.log("fireChatMessageSent", e); //$NON-NLS-1$
				}

			}
		}

	};

	IHistoryManager historyManager = new IHistoryManager() {
		public IHistory getHistory(ID targetID, Map options) {
			return null;
		}

		public boolean isActive() {
			return false;
		}

		public void setActive(boolean active) {
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

	};

	IChatMessageSender chatMessageSender = new IChatMessageSender() {

		public void sendChatMessage(ID toID, ID threadID, Type type, String subject, String body, Map properties) throws ECFException {
			if (toID == null || !(toID instanceof SkypeUserID))
				throw new ECFException(Messages.SkypeChatManager_EXCEPTION_INVALID_SKYPE_ID);
			SkypeUserID skypeId = (SkypeUserID) toID;
			try {
				Chat chat = Skype.chat(skypeId.getName());
				chat.send(body);
				sentChat = chat;
			} catch (SkypeException e) {
				throw new ECFException(Messages.SkypeChatManager_EXCEPTION_SKYPE_EXCEPTION, e);
			}
		}

		public void sendChatMessage(ID toID, String body) throws ECFException {
			sendChatMessage(toID, null, Type.CHAT, null, body, null);
		}

	};

	/**
	 * @throws SkypeException 
	 */
	public SkypeChatManager() throws SkypeException {
		Skype.addChatMessageListener(chatMessageListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#addMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public void addMessageListener(IIMMessageListener listener) {
		chatListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getChatMessageSender()
	 */
	public IChatMessageSender getChatMessageSender() {
		return chatMessageSender;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getHistoryManager()
	 */
	public IHistoryManager getHistoryManager() {
		return historyManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getTypingMessageSender()
	 */
	public ITypingMessageSender getTypingMessageSender() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#removeMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public void removeMessageListener(IIMMessageListener listener) {
		chatListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#createChat(org.eclipse.ecf.core.identity.ID)
	 */
	public IChat createChat(ID targetUser, IIMMessageListener messageListener) throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void dispose() {

	}

	/**
	 * 
	 */
	public void disconnect() {
		if (chatMessageListener != null) {
			Skype.removeChatMessageListener(chatMessageListener);
			chatMessageListener = null;
		}
		chatListeners.clear();
		chats.clear();
	}

}
