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

import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.IInitiatorCallSession;
import org.eclipse.ecf.call.events.ICallSessionInitiatedEvent;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Call;
import com.skype.SkypeException;

public class SkypeInitiatorCallSession extends AbstractSkypeCallSession
		implements IInitiatorCallSession {

	/**
	 * @param sharedObjectCallContainerAdapter
	 */
	public SkypeInitiatorCallSession(SkypeUserID initiatorID,
			SkypeUserID receiverID, Call call, ICallSessionListener listener)
			throws SkypeException {
		super(initiatorID, receiverID, call, listener);
		ICallSessionListener l = getListener();
		if (l != null) l.handleCallSessionEvent(new ICallSessionInitiatedEvent() {

			public IInitiatorCallSession getCallSession() {
				return SkypeInitiatorCallSession.this;
			}

			public String toString() {
				StringBuffer buffer = new StringBuffer(
						"ICallSessionInitiatedEvent["); //$NON-NLS-1$
				buffer.append("id=").append( //$NON-NLS-1$
						getID());
				buffer.append(";initiator=").append(getInitiator());
				buffer.append(";receiver=").append(getReceiver());
				buffer.append(";callstatus=").append(getState());
				buffer.append("]"); //$NON-NLS-1$
				return buffer.toString();
			}

		});
	}

}
