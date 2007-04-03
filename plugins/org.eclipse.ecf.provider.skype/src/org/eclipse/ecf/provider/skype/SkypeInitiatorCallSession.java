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
