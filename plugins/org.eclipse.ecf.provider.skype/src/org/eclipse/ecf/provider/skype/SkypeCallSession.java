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
import org.eclipse.ecf.call.ICallDescription;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallTransportCandidate;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;

/**
 * 
 */
public class SkypeCallSession implements ICallSession {

	SharedObjectCallContainerAdapter adapter;
	ID callSessionID;

	/**
	 * @param sharedObjectCallContainerAdapter
	 */
	public SkypeCallSession(
			SharedObjectCallContainerAdapter sharedObjectCallContainerAdapter,
			ID callSessionID) throws IDCreateException {
		this.adapter = sharedObjectCallContainerAdapter;
		this.callSessionID = (callSessionID == null) ? IDFactory.getDefault()
				.createGUID() : callSessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getCallSessionState()
	 */
	public State getCallSessionState() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getInitiator()
	 */
	public ID getInitiator() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getReceiver()
	 */
	public ID getReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#sendInitiate(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.call.ICallDescription[],
	 *      org.eclipse.ecf.call.ICallTransportCandidate[])
	 */
	public void sendInitiate(ID initiator, ID receiver,
			ICallDescription[] descriptions,
			ICallTransportCandidate[] transports) throws CallException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#sendTerminate()
	 */
	public void sendTerminate() throws CallException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
