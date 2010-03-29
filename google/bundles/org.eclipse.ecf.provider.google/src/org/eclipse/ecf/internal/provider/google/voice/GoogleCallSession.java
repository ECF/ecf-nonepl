/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google.voice;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.telephony.call.*;

public class GoogleCallSession implements ICallSession {

	protected String initaterID;
	protected String recvID;
	protected ICallSessionListener listener;
	protected CallSessionState state;
	protected GoogleCallSessionContainerAdapter callAdapter;

	public GoogleCallSession(String initiaterID, String recvID,
			ICallSessionListener listener,
			GoogleCallSessionContainerAdapter callAdapter) {
		this.initaterID = initiaterID;
		this.recvID = recvID;
		this.listener = listener;
		this.callAdapter = callAdapter;

	}

	public CallSessionErrorDetails getErrorDetails() {
		return null;
	}

	public CallSessionFailureReason getFailureReason() {
		return null;
	}

	public ID getInitiator() {
		return IDFactory.getDefault().createStringID(initaterID);
	}

	public ICallSessionListener getListener() {
		return listener;
	}

	public ID getReceiver() {
		return IDFactory.getDefault().createStringID(recvID);
	}

	public void setState(CallSessionState state) {
		this.state = state;
	}

	public CallSessionState getState() {
		return state;
	}

	public void sendTerminate() throws CallException {
		callAdapter.hangupActiveCall();
	}

	public ID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
