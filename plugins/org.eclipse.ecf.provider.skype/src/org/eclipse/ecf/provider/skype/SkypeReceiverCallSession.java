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
import org.eclipse.ecf.call.IReceiverCallSession;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Call;
import com.skype.SkypeException;
import com.skype.Call.Status;

/**
 * 
 */
public class SkypeReceiverCallSession extends AbstractSkypeCallSession
		implements IReceiverCallSession {

	/**
	 * @param receiverID
	 * @param receivedCall
	 */
	public SkypeReceiverCallSession(SkypeUserID receiverID, Call receivedCall,
			ICallSessionListener listener) throws SkypeException {
		super(new SkypeUserID(receivedCall.getPartner()), receiverID,
				receivedCall, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.skype.AbstractSkypeCallSession#handleStatusChanged(com.skype.Call.Status)
	 */
	protected void handleStatusChanged(Status status) {
		// TODO Auto-generated method stub
		this.callState = getCallState(status);
	}

}
