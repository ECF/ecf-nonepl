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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.internal.provider.skype.SkypeProviderDebugOptions;
import org.eclipse.ecf.provider.skype.identity.SkypeCallSessionID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserNamespace;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;

import com.skype.Call;
import com.skype.CallListener;
import com.skype.Profile;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.connector.Connector;

public class SharedObjectCallContainerAdapter extends BaseSharedObject
		implements ICallSessionContainerAdapter {

	boolean debugSkype = Boolean.getBoolean(System.getProperty("debugSkype",
			"false"));

	IContainer container;
	
	String skypeVersion;

	Profile userProfile;

	SkypeUserID userID;

	Vector callSessionRequestListeners = new Vector();

	HashMap initiatedCalls = new HashMap();

	HashMap receivedCalls = new HashMap();

	public SharedObjectCallContainerAdapter(IContainer container) {
		this.container = container;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IContainer.class)) return container;
		return super.getAdapter(adapter);
	}
	
	protected void addInitiatedCall(SkypeInitiatorCallSession callSession) {
		initiatedCalls.put(callSession.getID(), callSession);
	}

	protected void removeInitiatedCall(SkypeInitiatorCallSession callSession) {
		initiatedCalls.remove(callSession.getID());
	}

	protected void addReceivedCall(SkypeReceiverCallSession callSession) {
		receivedCalls.put(callSession.getID(), callSession);
	}

	protected void removeReceivedCall(SkypeReceiverCallSession callSession) {
		receivedCalls.remove(callSession.getID());
	}

	CallListener callListener = new CallListener() {
		public void callMaked(Call makedCall) throws SkypeException {
		}

		public void callReceived(Call receivedCall) throws SkypeException {
			fireCallReceived(receivedCall);
		}
	};

	protected SkypeUserID getUserID() {
		return userID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#dispose(org.eclipse.ecf.core.identity.ID)
	 */
	public void dispose(ID containerID) {
		super.dispose(containerID);
		Skype.removeCallListener(callListener);
		try {
			Skype.setDebug(false);
		} catch (SkypeException e) {
		}
		initiatedCalls.clear();
		initiatedCalls = null;
		receivedCalls.clear();
		receivedCalls = null;
	}

	/**
	 * @param receivedCall
	 */
	protected void fireCallReceived(final Call receivedCall) {
		synchronized (callSessionRequestListeners) {
			for (Iterator i = callSessionRequestListeners.iterator(); i
					.hasNext();) {
				ICallSessionRequestListener l = (ICallSessionRequestListener) i
						.next();
				l.handleCallSessionRequest(new ICallSessionRequestEvent() {

					public ICallSession accept(
							ICallSessionListener listener, Map properties) throws CallException {
						try {
							SkypeReceiverCallSession session = new SkypeReceiverCallSession(
									SharedObjectCallContainerAdapter.this,
									userID, new SkypeUserID(receivedCall
											.getPartner()), receivedCall,
									listener);
							receivedCall.answer();
							addReceivedCall(session);
							return session;
						} catch (SkypeException e) {
							throw new CallException("unexpected exception", e);
						}
					}

					public Map getProperties() {
						// XXX todo...get from Skype Call
						return new HashMap();
					}

					public void reject() {
						try {
							receivedCall.cancel();
						} catch (SkypeException e) {
							return;
						}
					}

					public ID getInitiator() {
						try {
							return new SkypeUserID(receivedCall.getPartner());
						} catch (SkypeException e) {
							return null;
						}
					}

					public ID getReceiver() {
						return userID;
					}

					public ID getSessionID() {
						return new SkypeCallSessionID(receivedCall.getId());
					}

				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#initialize()
	 */
	protected void initialize() throws SharedObjectInitException {
		super.initialize();
		try {
			skypeVersion = Skype.getVersion();

			userProfile = Skype.getProfile();
			userID = new SkypeUserID(userProfile.getId());
			Skype.setDebug(true);
			Skype.setDeamon(false);
			//SkypeClient.setSilentMode(true);
			Skype.addCallListener(callListener);

			if (debugSkype) {
				Connector.getInstance().setDebugOut(
						new PrintWriter(new Writer() {
							public void write(char[] cbuf, int off, int len)
									throws IOException {
								Trace.trace(Activator.PLUGIN_ID, "SKYPEDEBUG." //$NON-NLS-1$
										+ new String(cbuf, off, len));
							}

							public void flush() throws IOException {
							}

							public void close() throws IOException {
							}
						}));
				Connector.getInstance().setDebug(true);

			}

			Trace
					.trace(
							Activator.PLUGIN_ID,
							"ECF Skype Adapter initializing with version: " + skypeVersion); //$NON-NLS-1$
			Trace.trace(Activator.PLUGIN_ID,
					"UserID is: '" + userID.getName() + "'"); //$NON-NLS-1$

		} catch (Exception e) {
			Trace.catching(Activator.PLUGIN_ID,
					SkypeProviderDebugOptions.EXCEPTIONS_CATCHING, this
							.getClass(), "initialize", e); //$NON-NLS-1$
			Trace.throwing(Activator.PLUGIN_ID,
					SkypeProviderDebugOptions.EXCEPTIONS_THROWING, this
							.getClass(), "initialize", e); //$NON-NLS-1$
			throw new SharedObjectInitException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#getCallSessionNamespace()
	 */
	public Namespace getReceiverNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				SkypeUserNamespace.NAMESPACE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#addCallSessionRequestListener(org.eclipse.ecf.call.ICallSessionRequestListener)
	 */
	public void addCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#removeCallSessionRequestListener(org.eclipse.ecf.call.ICallSessionRequestListener)
	 */
	public void removeCallSessionRequestListener(
			ICallSessionRequestListener listener) {
		callSessionRequestListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#sendCallRequest(org.eclipse.ecf.core.identity.ID[],
	 *      org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(ID[] receivers, ICallSessionListener listener,
			Map properties) throws CallException {
		throw new CallException("conference call request not yet supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#sendCallRequest(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(final ID receiver,
			ICallSessionListener listener, Map options) throws CallException {
		Assert.isNotNull(listener,
				Messages.SharedObjectCallContainerAdapter_Exception_Not_Null);
		if (receiver instanceof SkypeUserID) {
			SkypeUserID rcvrID = (SkypeUserID) receiver;
			try {
				addInitiatedCall(new SkypeInitiatorCallSession(this, userID,
						rcvrID, Skype.call(rcvrID.getUser()), listener));

			} catch (SkypeException e) {
				throw new CallException(
						Messages.SharedObjectCallContainerAdapter_Exception_Skype,
						e);
			}
		} else
			throw new CallException(
					Messages.SkypeCallSession_Exception_Invalid_Receiver);
	}

}
