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

import java.util.Map;

import org.eclipse.ecf.call.CallException;
import org.eclipse.ecf.call.CallState;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.IReceiverCallSession;
import org.eclipse.ecf.core.identity.ID;

import com.skype.Call;
import com.skype.CallStatusChangedListener;
import com.skype.SkypeException;
import com.skype.Call.Status;

/**
 *
 */
public class SkypeReceiverCallSession implements IReceiverCallSession {

	ID receiverID;
	Call call;
	ICallSessionListener listener;
	Map properties;
	ID fromID;
	ID sessionID;
	
	CallStatusChangedListener statusChangedListener = new CallStatusChangedListener() {
		public void statusChanged(Status status) throws SkypeException {
			System.out.println("statusChanged("+status+")");
		}
	};
	
	/**
	 * @param receiverID
	 * @param receivedCall
	 * @param properties
	 * @param fromID
	 * @param sessionID
	 */
	public SkypeReceiverCallSession(ID receiverID, Call receivedCall, ICallSessionListener listener, Map properties,
			ID fromID, ID sessionID) {
		this.receiverID = receiverID;
		this.call = receivedCall;
		this.listener = listener;
		this.properties = properties;
		this.fromID = fromID;
		this.sessionID = sessionID;
		call.addCallStatusChangedListener(statusChangedListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#getInitiator()
	 */
	public ID getInitiator() {
		return fromID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#getListener()
	 */
	public ICallSessionListener getListener() {
		return listener;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#getReceiver()
	 */
	public ID getReceiver() {
		return receiverID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#getState()
	 */
	public CallState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSession#sendTerminate()
	 */
	public void sendTerminate() throws CallException {
		try {
			call.finish();
		} catch (SkypeException e) {
			throw new CallException("call finish exception",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return sessionID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
