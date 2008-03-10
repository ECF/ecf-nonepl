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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectTransactionConfig;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.provider.datashare.BaseChannel;

/**
 *
 */
public class SkypeA2AChannel extends BaseChannel {

	/**
	 * 
	 */
	private static final String SEND_CHANNEL_MESSAGE = "sendChannelMessage";

	/**
	 * @param config
	 * @param listener
	 */
	public SkypeA2AChannel(ISharedObjectTransactionConfig config, IChannelListener listener) {
		super(config, listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.datashare.BaseChannel#sendMessage(org.eclipse.ecf.core.identity.ID, byte[])
	 */
	public void sendMessage(ID receiver, byte[] message) throws ECFException {
		sendSharedObjectMsgToSelf(SharedObjectMsg.createMsg(SEND_CHANNEL_MESSAGE, new Object[] {receiver, message}));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#handleSharedObjectMsg(org.eclipse.ecf.core.sharedobject.SharedObjectMsg)
	 */
	protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
		if (msg.getMethod().equals(SEND_CHANNEL_MESSAGE)) {
			final Object[] args = msg.getParameters();
			try {
				super.sendMessage((ID) args[0], (byte[]) args[1]);
				return true;
			} catch (final Exception e) {
				Activator.log(SEND_CHANNEL_MESSAGE, e);
			}
		}
		return false;
	}
}
