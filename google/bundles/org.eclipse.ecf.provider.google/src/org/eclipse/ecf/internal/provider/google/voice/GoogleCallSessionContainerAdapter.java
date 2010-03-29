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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.provider.google.identity.GoogleNamespace;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.telephony.call.*;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;

public class GoogleCallSessionContainerAdapter implements
		ICallSessionContainerAdapter {

	private GoogleContainer container;
	private Vector callSessionRequestListeners = new Vector<ICallSessionRequestListener>();

	private String userID;
	private static VoiceCallInterface callInterface;

	public GoogleContainer getContainer() {
		return container;
	}

	public GoogleCallSessionContainerAdapter(IContainer container) {
		ICallSessionRequestListener listener;

		this.container = (GoogleContainer) container;
		callInterface = VoiceCallInterface.getDefault(this);
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

		XMPPID jabberURI;
		try {
			final Object connectData = getConnectData(remote, joinContext);

			getVoiceCallInterface().initXMPPSession(remote.getName(),
					(String) connectData);
			userID = remote.getName();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}

	}

	public void incomingCallRequest(final String jid) {
		Iterator it = callSessionRequestListeners.iterator();

		while (it.hasNext()) {

			((ICallSessionRequestListener) it.next())
					.handleCallSessionRequest(new ICallSessionRequestEvent() {

						public ICallSession accept(
								ICallSessionListener listener, Map properties)
								throws CallException {
							GoogleCallSession session = new GoogleCallSession(
									userID, jid, listener,
									GoogleCallSessionContainerAdapter.this);
							VoiceCallInterface.getDefault(
									GoogleCallSessionContainerAdapter.this)
									.AnswerReceivingCall(jid);
							return session;
						}

						public CallSessionState getCallSessionState() {
							return CallSessionState.PENDING;
						}

						public ID getInitiator() {
							try {
								return new XMPPID(new GoogleNamespace(), jid);
							} catch (URISyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return null;
							}
						}

						public Map getProperties() {
							// TODO Auto-generated method stub
							return null;
						}

						public ID getReceiver() {
							return IDFactory.getDefault()
									.createStringID(userID);
						}

						public ID getSessionID() {
							return null;
						}

						public void reject() {
							VoiceCallInterface.getDefault(
									GoogleCallSessionContainerAdapter.this)
									.rejectReceivingCall();
						}

					});

		}
	}

	public void addCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListeners.add(listener);
	}

	public Namespace getReceiverNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListeners.remove(listener);

	}

	public void sendCallRequest(ID[] receivers, ICallSessionListener listener,
			Map properties) throws CallException {
		throw new CallException("Conference Call Not Supported");
	}

	public VoiceCallInterface getVoiceCallInterface() {
		return VoiceCallInterface.getDefault(this);
	}

	public void sendCallRequest(ID receiver, ICallSessionListener listener,
			Map properties) throws CallException {

		callInterface.initVoiceCall(receiver.getName(), new GoogleCallSession(
				userID, receiver.getName(), listener, this));

	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void hangupActiveCall() {
		callInterface.hangupActiveCall();
	}

	public boolean isVoiceEnabled(ID id) {
		// TODO Auto-generated method stub
		return callInterface.isVoiceEnabled(id.getName());
	}

	public String getActiveCallerID() {
		// TODO Auto-generated method stub
		return callInterface.getActiveCallerID();
	}

	public void disconnect() {
		callInterface.disconnect();
	}

	public void muteActiveCall(boolean doMute) {

		if (doMute)
			callInterface.muteActiveCall();
		else
			callInterface.unmuteActiveCall();
	}

	public boolean isActiveCallMute() {
		return callInterface.isActiveCallMute();
	}

}
