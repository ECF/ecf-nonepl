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
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.internal.provider.skype.SkypeProviderDebugOptions;
import org.eclipse.ecf.provider.skype.identity.SkypeCallSessionID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserNamespace;

import com.skype.Call;
import com.skype.CallStatusChangedListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Call.Status;

public class SkypeCallSession implements ICallSession {

	SharedObjectCallContainerAdapter adapter;
	SkypeUserID initiator = null;
	SkypeUserID receiver = null;
	SkypeCallSessionID callSession = null;
	
	Call skypeCall = null;

	CallStatusChangedListener callStatusChangeListener = new CallStatusChangedListener() {
		public void statusChanged(Status status) throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), getID().getName()+ ".statusChanged("+status+")");
		}
	};
	
	/**
	 * @param sharedObjectCallContainerAdapter
	 */
	public SkypeCallSession(
			SharedObjectCallContainerAdapter sharedObjectCallContainerAdapter)
			throws IDCreateException {
		this.adapter = sharedObjectCallContainerAdapter;
		this.initiator = this.adapter.getUserID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getCallSessionState()
	 */
	public synchronized State getCallSessionState() {
		return (skypeCall == null) ? ICallSession.State.PREPENDING
				: createCallState();
	}

	/**
	 * @return
	 */
	private State createCallState() {
		Status s = null;
		try {
			s = skypeCall.getStatus();
		} catch (SkypeException e) {
			return ICallSession.State.ENDED;
		}
		// XXX TODO
		return ICallSession.State.PENDING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getInitiator()
	 */
	public ID getInitiator() {
		return initiator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getReceiver()
	 */
	public ID getReceiver() {
		return receiver;
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
		if (receiver instanceof SkypeUserID) {
			this.receiver = (SkypeUserID) receiver;
			synchronized (this) {
				try {
					this.skypeCall = Skype.call(this.receiver.getUser());
					this.skypeCall.addCallStatusChangedListener(callStatusChangeListener);
				} catch (SkypeException e) {
					Trace.catching(Activator.getDefault(),
							SkypeProviderDebugOptions.EXCEPTIONS_CATCHING, this
									.getClass(), "sendInitiateCall", e); //$NON-NLS-1$
					Trace.throwing(Activator.getDefault(),
							SkypeProviderDebugOptions.EXCEPTIONS_THROWING, this
									.getClass(), "sendInitiateCall", e); //$NON-NLS-1$
					throw new CallException(
							Messages.SharedObjectCallContainerAdapter_Exception_Skype);
				}
				adapter.addCallSession(getID(),this);
			}
		} else
			throw new CallException(
					Messages.SkypeCallSession_Exception_Invalid_Receiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#sendTerminate()
	 */
	public synchronized void sendTerminate() throws CallException {
		if (skypeCall == null)
			throw new CallException(
					Messages.SkypeCallSession_Exception_Call_Wrong_State);
		else {
			try {
				skypeCall.finish();
			} catch (SkypeException e) {
				throw new CallException(
						Messages.SharedObjectCallContainerAdapter_Exception_Skype,
						e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public synchronized ID getID() {
		return callSession;
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
	 * @see org.eclipse.ecf.call.ICallSession#getCallPartyNamespace()
	 */
	public Namespace getCallPartyNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				SkypeUserNamespace.NAMESPACE_NAME);
	}

}
