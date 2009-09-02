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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.ObjectCallback;
import org.eclipse.ecf.core.security.UnsupportedCallbackException;
import org.eclipse.ecf.provider.call.sip.container.SipContainer;
import org.eclipse.ecf.provider.call.sip.identity.SipLocalParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipRemoteParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipUriID;
import org.eclipse.ecf.provider.call.sip.identity.SipUriNamespace;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;


public class SipCallSessionContainerAdapter implements
		ICallSessionContainerAdapter {

	private SipContainer container;
	private Vector callSessionRequestListener = new Vector<ICallSessionRequestListener>();
	private SipUriID userId;
	private SipCall sipCall;

	/**
	 * 
	 */
	public SipCallSessionContainerAdapter(SipContainer container) {
		this.container = container;
		sipCall = SipCall.getDefault(this);

	}

	protected Callback[] createAuthorizationCallbacks() {
		final Callback[] cbs = new Callback[1];
		cbs[0] = new ObjectCallback();
		return cbs;
	}

	protected Object getConnectData(ID remote, IConnectContext joinContext)
			throws IOException, UnsupportedCallbackException {
		final Callback[] callbacks = createAuthorizationCallbacks();
		if (joinContext != null && callbacks != null && callbacks.length > 0) {
			final CallbackHandler handler = joinContext.getCallbackHandler();
			if (handler != null) {
				handler.handle(callbacks);
			}
			if (callbacks[0] instanceof ObjectCallback) {
				final ObjectCallback cb = (ObjectCallback) callbacks[0];
				return cb.getObject();
			}
		}
		return null;
	}

	public void createVoiceConnection(ID remote, IConnectContext joinContext) {

		SipLocalParticipant localUser;
		try {
			final Object connectData = getConnectData(remote, joinContext);

			SipUriID initiatorId = (SipUriID) new SipUriNamespace()
					.createInstance(new Object[] { "<sip:" + remote.getName()
							+ ">" });
			String initiatorName = null;
			String password = (String) connectData;
			String proxyServer = "proxy.sipthor.net";//TODO Remove hard code

			localUser = new SipLocalParticipant(initiatorId, initiatorName,
					password, proxyServer);

			sipCall.connect(localUser);

			userId = initiatorId;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}

	}

	public void disconnect() {
		sipCall = null;
	}

	public void incomingCallRequest(final SipUriID callerId){
		
		Iterator it=callSessionRequestListener.iterator();
		while(it.hasNext()){
			
			((ICallSessionRequestListener) it.next())
            .handleCallSessionRequest(new ICallSessionRequestEvent() {

                    public ICallSession accept(
                                    ICallSessionListener listener, Map properties)
                                    throws CallException {
                            SipCallSession session = new SipCallSession(
                                            userId, callerId, listener,
                                            SipCallSessionContainerAdapter.this);
                            sipCall.getDefault(
                                            SipCallSessionContainerAdapter.this)
                                            .acceptIncomingCall(callerId);
                            return session;
                    }

                    public CallSessionState getCallSessionState() {
                            return CallSessionState.PENDING;
                    }

                    public ID getInitiator() {
                            SipUriID initiatorId = (SipUriID) new SipUriNamespace()
							.createInstance(new Object[] { "<sip:" + callerId.getName()
									+ ">" });
							    return initiatorId;
                    }

                    public Map getProperties() {
                            // TODO Auto-generated method stub
                            return null;
                    }

                    public ID getReceiver() {
                            return IDFactory.getDefault()
                                            .createStringID(userId.getSIPUrl());
                    }

                    public ID getSessionID() {
                            return null;
                    }

                    public void reject() {
                            sipCall.getDefault(
                                            SipCallSessionContainerAdapter.this)
                                            .rejectIncomingCall();
                    }

            });


		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#
	 * addCallSessionRequestListener
	 * (org.eclipse.ecf.telephony.call.ICallSessionRequestListener)
	 */
	@Override
	public void addCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListener.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#
	 * getReceiverNamespace()
	 */
	@Override
	public Namespace getReceiverNamespace() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#
	 * removeCallSessionRequestListener
	 * (org.eclipse.ecf.telephony.call.ICallSessionRequestListener)
	 */
	@Override
	public void removeCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListener.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#sendCallRequest
	 * (org.eclipse.ecf.core.identity.ID[],
	 * org.eclipse.ecf.telephony.call.ICallSessionListener, java.util.Map)
	 */
	@Override
	public void sendCallRequest(ID[] receivers, ICallSessionListener listener,
			Map properties) throws CallException {
		throw new CallException("Conference Call support not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#sendCallRequest
	 * (org.eclipse.ecf.core.identity.ID,
	 * org.eclipse.ecf.telephony.call.ICallSessionListener, java.util.Map)
	 */
	@Override
	public void sendCallRequest(ID receiver, ICallSessionListener listener,
			Map properties) throws CallException {
		
		SipUriID receipient=(SipUriID) new SipUriNamespace()
		.createInstance(new Object[] { "<sip:"+receiver.getName()+">" });
		SipRemoteParticipant remoteParty = new SipRemoteParticipant(
				receipient,
				null);
		
		sipCall.initiateCall(remoteParty, new SipCallSession(userId, receipient, listener, this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void hangupActiveCall(){
		sipCall.hangupActiveCall(1000);
	}
	
	public void rejectIncomingCall(){
		sipCall.rejectIncomingCall();
	}
	
	public void cancelUnansweredCall(){
		sipCall.createCallCancel();
	}

	public SipContainer getContainer() {
		return container;
	}

	public void setContainer(SipContainer container) {
		this.container = container;
	}

	public Vector getCallSessionRequestListener() {
		return callSessionRequestListener;
	}

	public void setCallSessionRequestListener(Vector callSessionRequestListener) {
		this.callSessionRequestListener = callSessionRequestListener;
	}

	public SipUriID getUserId() {
		return userId;
	}

	public void setUserId(SipUriID userId) {
		this.userId = userId;
	}

	public SipCall getSipCall() {
		return sipCall;
	}

	public void setSipCall(SipCall sipCall) {
		this.sipCall = sipCall;
	}
}
