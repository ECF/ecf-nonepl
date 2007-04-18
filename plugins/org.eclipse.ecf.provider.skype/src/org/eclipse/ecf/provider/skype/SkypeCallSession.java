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
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.provider.skype.identity.SkypeCallSessionID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionErrorDetails;
import org.eclipse.ecf.telephony.call.CallSessionFailureReason;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;

import com.skype.Call;
import com.skype.CallStatusChangedListener;
import com.skype.SkypeException;
import com.skype.Call.Status;

/**
 * 
 */
public class SkypeCallSession  implements ICallSession {

	protected SkypeUserID initiatorID = null;
	protected SkypeUserID receiverID = null;
	protected Call call = null;
	protected SkypeCallSessionID sessionID = null;
	protected ICallSessionListener listener = null;
	protected CallSessionState callState = null;
	protected CallSessionFailureReason failureReason = null;
	protected SharedObjectCallContainerAdapter adapter = null;
	protected CallSessionErrorDetails callError = null;
	
	protected CallStatusChangedListener callStatusChangedListener = new CallStatusChangedListener() {
		public void statusChanged(Status status) throws SkypeException {
			handleStatusChanged(status);
		}
	};

	protected void fireCallSessionEvent(ICallSessionEvent event) {
		ICallSessionListener listener = getListener();
		if (listener != null) {
			listener.handleCallSessionEvent(event);
		}
	}

	protected SharedObjectCallContainerAdapter getAdapter() {
		return adapter;
	}
	
	protected SkypeCallSession(SharedObjectCallContainerAdapter adapter, SkypeUserID initiatorID,
			SkypeUserID receiverID, Call call, ICallSessionListener listener)
			throws SkypeException {
		this.adapter = null;
		this.initiatorID = initiatorID;
		this.call = call;
		this.receiverID = receiverID;
		this.sessionID = new SkypeCallSessionID(call.getId());
		this.listener = listener;
		this.callState = CallSessionState.PENDING;
		this.call.addCallStatusChangedListener(callStatusChangedListener);
	}

	protected synchronized void setFailureReason(CallSessionFailureReason reason) {
		this.failureReason = reason;
	}

	protected synchronized void setCallState(CallSessionState callState) {
		if (callState != null) {
			this.callState = callState;
			if (this.callState.equals(CallSessionState.FAILED)) {
				try {
					setFailureReason(lookupFailureReason(call.getErrorCode()));
				} catch (SkypeException e) {
				}
			}
		}
	}

	protected CallSessionFailureReason lookupFailureReason(int errorCode) {
		return new SkypeFailureReason(errorCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.skype.SkypeCallSession#handleStatusChanged(com.skype.Call.Status)
	 */
	protected void handleStatusChanged(Status status) {
		setCallState(createCallState(status));
		fireCallSessionEvent(new ICallSessionEvent() {
			public ICallSession getCallSession() {
				return SkypeCallSession.this;
			}

			public String toString() {
				return getStringBufferForEvent("ICallSessionEvent")
						.toString();
			}

		});
	}

	protected StringBuffer getStringBufferForEvent(String eventType) {
		StringBuffer buffer = new StringBuffer(eventType);
		buffer.append("[");
		buffer.append("id=").append( //$NON-NLS-1$
				getID());
		buffer.append(";init=").append(getInitiator());
		buffer.append(";rcvr=").append(getReceiver());
		buffer.append(";state=").append(getState());
		buffer.append(";reason=").append(getFailureReason());
		buffer.append("]"); //$NON-NLS-1$		
		return buffer;
	}

	/**
	 * @param status
	 * @return
	 */
	protected CallSessionState createCallState(Status status) {
		if (status.equals(Status.BUSY))
			return CallSessionState.BUSY;
		else if (status.equals(Status.CANCELLED))
			return CallSessionState.CANCELLED;
		else if (status.equals(Status.EARLYMEDIA))
			return CallSessionState.PREPENDING;
		else if (status.equals(Status.FAILED))
			return CallSessionState.FAILED;
		else if (status.equals(Status.FINISHED))
			return CallSessionState.FINISHED;
		else if (status.equals(Status.INPROGRESS))
			return CallSessionState.ACTIVE;
		else if (status.equals(Status.MISSED))
			return CallSessionState.MISSED;
		else if (status.equals(Status.ONHOLD))
			return CallSessionState.ONHOLD;
		else if (status.equals(Status.REFUSED))
			return CallSessionState.REFUSED;
		else if (status.equals(Status.RINGING))
			return CallSessionState.PENDING;
		else if (status.equals(Status.ROUTING))
			return CallSessionState.ROUTING;
		else if (status.equals(Status.UNPLACED))
			return CallSessionState.UNPLACED;
		else
			return CallSessionState.UNKNOWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getInitiator()
	 */
	public ID getInitiator() {
		return initiatorID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getReceiver()
	 */
	public ID getReceiver() {
		return receiverID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#sendTerminate()
	 */
	public void sendTerminate() throws CallException {
		try {
			call.finish();
		} catch (SkypeException e) {
			throw new CallException(
					Messages.SharedObjectCallContainerAdapter_Exception_Skype,
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public synchronized ID getID() {
		return sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getListener()
	 */
	public ICallSessionListener getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getState()
	 */
	public CallSessionState getState() {
		return callState;
	}

	public CallSessionFailureReason getFailureReason() {
		return failureReason;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#getError()
	 */
	public CallSessionErrorDetails getErrorDetails() {
		return callError;
	}
}
