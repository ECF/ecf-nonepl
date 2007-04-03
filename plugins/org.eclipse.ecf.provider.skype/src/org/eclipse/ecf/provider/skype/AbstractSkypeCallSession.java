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

import org.eclipse.ecf.call.CallException;
import org.eclipse.ecf.call.CallState;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.provider.skype.identity.SkypeCallSessionID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Call;
import com.skype.CallStatusChangedListener;
import com.skype.SkypeException;
import com.skype.Call.Status;

/**
 * 
 */
public abstract class AbstractSkypeCallSession {

	protected SkypeUserID initiatorID = null;
	protected SkypeUserID receiverID = null;
	protected Call call = null;
	protected SkypeCallSessionID sessionID = null;
	protected ICallSessionListener listener = null;
	protected CallState callState = null;

	protected CallStatusChangedListener callStatusChangedListener = new CallStatusChangedListener() {
		public void statusChanged(Status status) throws SkypeException {
			System.out.println(getID() + ".statusChanged(" + status + ")");
			handleStatusChanged(status);
		}
	};

	protected AbstractSkypeCallSession(SkypeUserID initiatorID,
			SkypeUserID receiverID, Call call, ICallSessionListener listener)
			throws SkypeException {
		this.initiatorID = initiatorID;
		this.call = call;
		this.receiverID = receiverID;
		this.sessionID = new SkypeCallSessionID(call.getId());
		this.listener = listener;
		this.callState = getCallState(call.getStatus());
		this.call.addCallStatusChangedListener(callStatusChangedListener);
	}

	protected abstract void handleStatusChanged(Status status);

	/**
	 * @param status
	 * @return
	 */
	protected CallState getCallState(Status status) {
		if (status.equals(Status.BUSY))
			return CallState.BUSY;
		else if (status.equals(Status.CANCELLED))
			return CallState.CANCELLED;
		else if (status.equals(Status.EARLYMEDIA))
			return CallState.PREPENDING;
		else if (status.equals(Status.FAILED))
			return CallState.FAILED;
		else if (status.equals(Status.FINISHED))
			return CallState.FINISHED;
		else if (status.equals(Status.INPROGRESS))
			return CallState.ACTIVE;
		else if (status.equals(Status.MISSED))
			return CallState.MISSED;
		else if (status.equals(Status.ONHOLD))
			return CallState.ONHOLD;
		else if (status.equals(Status.REFUSED))
			return CallState.REFUSED;
		else if (status.equals(Status.RINGING))
			return CallState.PENDING;
		else if (status.equals(Status.ROUTING))
			return CallState.ROUTING;
		else if (status.equals(Status.UNPLACED))
			return CallState.UNPLACED;
		else
			return CallState.UNKNOWN;
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
	public CallState getState() {
		return callState;
	}

}
