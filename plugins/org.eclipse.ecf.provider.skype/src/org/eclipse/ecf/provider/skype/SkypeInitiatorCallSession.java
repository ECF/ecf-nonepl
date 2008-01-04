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

import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;

import com.skype.Call;
import com.skype.SkypeException;

public class SkypeInitiatorCallSession extends SkypeCallSession {

	/**
	 * @param adapter 
	 * @param initiatorID 
	 * @param receiverID 
	 * @param call 
	 * @param listener 
	 * @throws SkypeException 
	 */
	public SkypeInitiatorCallSession(SkypeCallContainerAdapter adapter, SkypeUserID initiatorID, SkypeUserID receiverID, Call call, ICallSessionListener listener) throws SkypeException {
		super(adapter, initiatorID, receiverID, call, listener);
		final ICallSessionListener l = getListener();
		if (l != null)
			l.handleCallSessionEvent(new ICallSessionEvent() {

				public ICallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent("ICallSessionEvent").toString(); //$NON-NLS-1$
				}

			});
	}

}
