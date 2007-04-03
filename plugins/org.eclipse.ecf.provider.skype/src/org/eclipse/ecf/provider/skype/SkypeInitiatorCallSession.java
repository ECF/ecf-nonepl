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

import org.eclipse.ecf.call.CallState;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.IInitiatorCallSession;
import org.eclipse.ecf.call.events.ICallSessionAcceptedEvent;
import org.eclipse.ecf.call.events.ICallSessionFailedEvent;
import org.eclipse.ecf.call.events.ICallSessionInitiatedEvent;
import org.eclipse.ecf.call.events.ICallSessionPendingEvent;
import org.eclipse.ecf.call.events.ICallSessionTerminatedEvent;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Call;
import com.skype.SkypeException;
import com.skype.Call.Status;

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
		if (l != null)
			l.handleCallSessionEvent(new ICallSessionInitiatedEvent() {

				public IInitiatorCallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent("ICallSessionInitiatedEvent")
							.toString();
				}

			});
	}

	protected void handleStatusChanged(Status status) {
		super.handleStatusChanged(status);
		CallState callState = getState();
		if (callState.equals(CallState.PENDING))
			fireCallSessionEvent(new ICallSessionPendingEvent() {
				public ICallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent("ICallSessionPendingEvent")
							.toString();
				}

			});
		else if (callState.equals(CallState.ACTIVE))
			fireCallSessionEvent(new ICallSessionAcceptedEvent() {

				public ICallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent("ICallSessionAcceptedEvent")
							.toString();
				}

			});
		else if (callState.equals(CallState.FINISHED))
			fireCallSessionEvent(new ICallSessionTerminatedEvent() {

				public ICallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent(
							"ICallSessionTerminatedEvent").toString();
				}

			});
		else if (callState.equals(CallState.FAILED))
			fireCallSessionEvent(new ICallSessionFailedEvent() {

				public ICallSession getCallSession() {
					return SkypeInitiatorCallSession.this;
				}

				public String toString() {
					return getStringBufferForEvent("ICallSessionFailedEvent")
							.toString();
				}

			});
	}
}
