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

import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageSender;

/**
 *
 */
public class SkypeChatManager implements IChatManager {

	SkypeContainer container;
	
	/**
	 * @param skypeContainer
	 */
	public SkypeChatManager(SkypeContainer skypeContainer) {
		this.container = skypeContainer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#addMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public void addMessageListener(IIMMessageListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getChatMessageSender()
	 */
	public IChatMessageSender getChatMessageSender() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getHistoryManager()
	 */
	public IHistoryManager getHistoryManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getTypingMessageSender()
	 */
	public ITypingMessageSender getTypingMessageSender() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#removeMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
	public void removeMessageListener(IIMMessageListener listener) {
		// TODO Auto-generated method stub

	}

}
