/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.call.sip;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.call.sip.identity.SipUriID;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionErrorDetails;
import org.eclipse.ecf.telephony.call.CallSessionFailureReason;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;


public class SipCallSession implements ICallSession {
	private SipUriID initiatorId;
	private SipUriID receiverId;
//	private String initiatorPassword;
	private SipCall call;
	private ICallSessionListener listener;
	private CallSessionState callState;
	private CallSessionFailureReason failureReason;
	private SipCallSessionContainerAdapter callAdapter;
	private CallSessionErrorDetails callError;
	
	
	public SipCallSession(SipUriID initiatorId, SipUriID receiverId, ICallSessionListener listener, SipCallSessionContainerAdapter callAdapter){
		this.initiatorId=initiatorId;
		this.receiverId=receiverId;
		this.listener=listener;
		this.callAdapter=callAdapter;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getErrorDetails()
	 */
	@Override
	public CallSessionErrorDetails getErrorDetails() {
		return callError;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getFailureReason()
	 */
	@Override
	public CallSessionFailureReason getFailureReason() {
		return failureReason;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getInitiator()
	 */
	@Override
	public ID getInitiator() {
		return IDFactory.getDefault().createStringID(initiatorId.getSIPUrl());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getListener()
	 */
	@Override
	public ICallSessionListener getListener() {
		return listener;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getReceiver()
	 */
	@Override
	public ID getReceiver() {
		return IDFactory.getDefault().createStringID(receiverId.getSIPUrl());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getState()
	 */
	@Override
	public CallSessionState getState() {
		return callState;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#sendTerminate()
	 */
	@Override
	public void sendTerminate() throws CallException {
		callAdapter.hangupActiveCall();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	@Override
	public ID getID() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}


	public void setCallState(CallSessionState callState) {
		this.callState = callState;
	}


	public void setFailureReason(CallSessionFailureReason failureReason) {
		this.failureReason = failureReason;
	}


	public void setCallError(CallSessionErrorDetails callError) {
		this.callError = callError;
	}
	
	
	

}
