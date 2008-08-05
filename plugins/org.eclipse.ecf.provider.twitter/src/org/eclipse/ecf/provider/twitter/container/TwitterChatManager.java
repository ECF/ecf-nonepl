/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.twitter.container;

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.IChat;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.im.IChatMessage.Type;

/**
 *
 */
public class TwitterChatManager implements IChatManager {

	private final TwitterContainer container;

	private final IChatMessageSender messageSender = new IChatMessageSender() {

		public void sendChatMessage(ID toID, ID threadID, Type type, String subject, String body, Map properties) throws ECFException {
			sendChatMessage(toID, body);
		}

		public void sendChatMessage(ID toID, String body) throws ECFException {
			if (toID == null || toID.equals(container.getTargetID())) {
				container.sendTwitterUpdate(body);
			} else {
				container.sendTwitterMessage(toID.getName(), body);
			}
		}
	};

	public TwitterChatManager(TwitterContainer twitterContainer) {
		this.container = twitterContainer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#addMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public void addMessageListener(IIMMessageListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#createChat(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public IChat createChat(ID targetUser, IIMMessageListener messageListener) throws ECFException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getChatMessageSender()
	 */
	public IChatMessageSender getChatMessageSender() {
		return messageSender;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getHistoryManager()
	 */
	public IHistoryManager getHistoryManager() {
		return null;
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
		// TODO Auto-generated method stub

	}

}