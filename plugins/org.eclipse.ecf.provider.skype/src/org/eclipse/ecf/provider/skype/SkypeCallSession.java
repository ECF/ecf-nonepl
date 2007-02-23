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
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.events.ICallSessionFailedEvent;
import org.eclipse.ecf.call.events.ICallSessionTerminateEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.internal.provider.skype.SkypeProviderDebugOptions;
import org.eclipse.ecf.provider.skype.identity.SkypeCallSessionID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Call;
import com.skype.CallStatusChangedListener;
import com.skype.SkypeException;
import com.skype.Call.Status;

public class SkypeCallSession implements ICallSession {

	SharedObjectCallContainerAdapter adapter;
	SkypeUserID initiator = null;
	Call skypeCall = null;

	SkypeUserID receiver = null;
	SkypeCallSessionID session = null;
	ICallSessionListener listener = null;

	CallStatusChangedListener callStatusChangeListener = new CallStatusChangedListener() {
		public void statusChanged(Status status) throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), getID().getName()
					+ ".statusChanged(" + status + ")");
			if (status.equals(Status.FAILED)) fireFailedEvent();
		}
	};

	protected void fireFailedEvent() {
		listener.handleCallSessionEvent(new ICallSessionFailedEvent() {

			public ICallSession getCallSession() {
				return SkypeCallSession.this;
			}

			public void replyTerminate() {
				sendTerminate0();
			}});
	}
	/**
	 * @param sharedObjectCallContainerAdapter
	 */
	public SkypeCallSession(
			SharedObjectCallContainerAdapter sharedObjectCallContainerAdapter,
			Call call, ICallSessionListener listener) throws SkypeException {
		this.adapter = sharedObjectCallContainerAdapter;
		this.initiator = this.adapter.getUserID();
		this.skypeCall = call;
		this.receiver = new SkypeUserID(skypeCall.getPartnerId());
		this.session = new SkypeCallSessionID(skypeCall.getId());
		this.listener = listener;
		this.skypeCall.addCallStatusChangedListener(callStatusChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSession#getCallSessionState()
	 */
	public synchronized State getState() {
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
	 * @see org.eclipse.ecf.call.ICallSession#sendTerminate()
	 */
	public void sendTerminate() throws CallException {
		try {
			skypeCall.finish();
		} catch (SkypeException e) {
			throw new CallException(
					Messages.SharedObjectCallContainerAdapter_Exception_Skype,
					e);
		} finally {
			fireTerminate();
		}
	}

	protected void sendTerminate0() {
		try {
			sendTerminate();
		} catch (CallException e) {
			Trace.catching(Activator.getDefault(), SkypeProviderDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "sendTerminate0", e);
		}
	}
	private void fireTerminate() {
		listener.handleCallSessionEvent(new ICallSessionTerminateEvent() {
			public ICallSession getCallSession() {
				return SkypeCallSession.this;
			}

			public void replyTerminate() {
			}});
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public synchronized ID getID() {
		return session;
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

}
