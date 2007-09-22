/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.iax;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.iax.container.IAXContainer;
import org.eclipse.ecf.provider.iax.identity.IAXCallID;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionErrorDetails;
import org.eclipse.ecf.telephony.call.CallSessionFailureReason;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;

/**
 *
 */
public class IAXCallSession implements ICallSession {

	private final ID callSessionID;

	private final IAXContainer container;

	private final IAXCallID initiator;

	private final IAXCallID receiver;

	private final ICallSessionListener listener;

	private final Map properties;

	private CallSessionState callState;

	private CallSessionErrorDetails callSessionErrorDetails;

	private CallSessionFailureReason callSessionFailureReason;

	public IAXCallSession(ID callSessionID, IAXContainer container, IAXCallID initiator, IAXCallID receiver, ICallSessionListener listener, Map properties) {
		this.callSessionID = callSessionID;
		Assert.isNotNull(this.callSessionID);
		this.container = container;
		Assert.isNotNull(this.container);
		this.initiator = initiator;
		Assert.isNotNull(this.initiator);
		this.receiver = receiver;
		Assert.isNotNull(this.receiver);
		this.listener = listener;
		Assert.isNotNull(this.listener);
		this.properties = properties;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getErrorDetails()
	 */
	public CallSessionErrorDetails getErrorDetails() {
		return callSessionErrorDetails;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getFailureReason()
	 */
	public CallSessionFailureReason getFailureReason() {
		return callSessionFailureReason;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getInitiator()
	 */
	public ID getInitiator() {
		return initiator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getListener()
	 */
	public ICallSessionListener getListener() {
		return listener;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getReceiver()
	 */
	public ID getReceiver() {
		return receiver;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#getState()
	 */
	public CallSessionState getState() {
		return callState;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSession#sendTerminate()
	 */
	public void sendTerminate() throws CallException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return callSessionID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
