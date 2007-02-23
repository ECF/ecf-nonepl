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
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.call.CallException;
import org.eclipse.ecf.call.ICallContainerAdapter;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionError;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.events.ICallSessionInitiateAcknowledgeEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.internal.provider.skype.SkypeProviderDebugOptions;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.provider.skype.identity.SkypeUserNamespace;

import com.skype.Call;
import com.skype.CallListener;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Profile;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;

public class SharedObjectCallContainerAdapter extends BaseSharedObject
		implements ICallContainerAdapter {

	boolean debugSkype = true;

	String skypeVersion;

	Profile userProfile;

	SkypeUserID userID;

	CallListener callListener = new CallListener() {
		public void callMaked(Call makedCall) throws SkypeException {
			Trace.trace(Activator.getDefault(), "callMade(" + makedCall + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public void callReceived(Call receivedCall) throws SkypeException {
			Trace.trace(Activator.getDefault(), "callReceived(" + receivedCall //$NON-NLS-1$
					+ ")"); //$NON-NLS-1$
		}
	};

	ChatMessageListener chatMessageListener = new ChatMessageListener() {

		public void chatMessageReceived(ChatMessage receivedChatMessage)
				throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "chatMessageReceived(" //$NON-NLS-1$
					+ receivedChatMessage + ")"); //$NON-NLS-1$
		}

		public void chatMessageSent(ChatMessage sentChatMessage)
				throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "chatMessageSent(" //$NON-NLS-1$
					+ sentChatMessage + ")"); //$NON-NLS-1$
		}

	};

	ConnectorListener connectorListener = new ConnectorListener() {

		public void messageReceived(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "messageReceived(" //$NON-NLS-1$
					+ event.getMessage() + ")"); //$NON-NLS-1$
		}

		public void messageSent(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "messageSent(" //$NON-NLS-1$
					+ event.getMessage() + ")"); //$NON-NLS-1$
		}

		public void statusChanged(ConnectorStatusEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "statusChanged(" //$NON-NLS-1$
					+ event.getStatus() + ")"); //$NON-NLS-1$
		}

	};

	protected SkypeUserID getUserID() {
		return userID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#initialize()
	 */
	protected void initialize() throws SharedObjectInitException {
		super.initialize();
		try {
			Connector.getInstance().addConnectorListener(connectorListener);
			skypeVersion = Skype.getVersion();

			userProfile = Skype.getProfile();
			userID = new SkypeUserID(userProfile.getId());
			Skype.setDeamon(true);
			Trace
					.trace(
							Activator.getDefault(),
							"ECF Skype Adapter initializing with version: " + skypeVersion); //$NON-NLS-1$
			Skype.addCallListener(callListener);
			Skype.addChatMessageListener(chatMessageListener);

			if (debugSkype) {
				Connector.getInstance().setDebugOut(
						new PrintWriter(new Writer() {
							public void write(char[] cbuf, int off, int len)
									throws IOException {
								// XXX TODO
								Trace.trace(Activator.getDefault(),
										"SKYPEDEBUG." //$NON-NLS-1$
												+ new String(cbuf, off, len));
							}

							public void flush() throws IOException {
							}

							public void close() throws IOException {
							}
						}));
				Connector.getInstance().setDebug(true);

			}
		} catch (Exception e) {
			Trace.catching(Activator.getDefault(),
					SkypeProviderDebugOptions.EXCEPTIONS_CATCHING, this
							.getClass(), "initialize", e); //$NON-NLS-1$
			Trace.throwing(Activator.getDefault(),
					SkypeProviderDebugOptions.EXCEPTIONS_THROWING, this
							.getClass(), "initialize", e); //$NON-NLS-1$
			throw new SharedObjectInitException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#addListener(org.eclipse.ecf.call.ICallSessionListener)
	 */
	public void addListener(ICallSessionListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#getCallSessionNamespace()
	 */
	public Namespace getReceiverNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				SkypeUserNamespace.NAMESPACE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#removeListener(org.eclipse.ecf.call.ICallSessionListener)
	 */
	public void removeListener(ICallSessionListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#initiateCall(org.eclipse.ecf.core.identity.ID[],
	 *      org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void initiateCall(ID[] receivers, ICallSessionListener listener,
			Map options) throws CallException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#initiateCall(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void initiateCall(ID receiver, ICallSessionListener listener,
			Map options) throws CallException {
		Assert.isNotNull(listener,
				Messages.SharedObjectCallContainerAdapter_Exception_Not_Null);
		if (receiver instanceof SkypeUserID) {
			SkypeUserID rcvrID = (SkypeUserID) receiver;
			synchronized (this) {
				try {
					final SkypeCallSession session = new SkypeCallSession(this,
							Skype.call(rcvrID.getUser()), listener);
					listener
							.handleCallSessionEvent(new ICallSessionInitiateAcknowledgeEvent() {

								public ICallSession getCallSession() {
									return session;
								}

								public void replyAcknowledge() {
								}

								public void replyError(ICallSessionError error) {
								}

								public void replyTerminate() {
									try {
										session.sendTerminate();
									} catch (CallException e) {
										Trace
												.catching(
														Activator.getDefault(),
														SkypeProviderDebugOptions.EXCEPTIONS_CATCHING,
														this.getClass(),
														"replyTerminate", e); //$NON-NLS-1$
									}
								}

								public String toString() {
									StringBuffer buffer = new StringBuffer(
											"ICallSessionInitiateAcknowledgeEvent["); //$NON-NLS-1$
									buffer.append("sessionid=").append( //$NON-NLS-1$
											session.getID()).append("]"); //$NON-NLS-1$
									return buffer.toString();
								}
							});
				} catch (SkypeException e) {
					Trace.catching(Activator.getDefault(),
							SkypeProviderDebugOptions.EXCEPTIONS_CATCHING, this
									.getClass(), "sendInitiateCall", e); //$NON-NLS-1$
					Trace.throwing(Activator.getDefault(),
							SkypeProviderDebugOptions.EXCEPTIONS_THROWING, this
									.getClass(), "sendInitiateCall", e); //$NON-NLS-1$
					throw new CallException(
							Messages.SharedObjectCallContainerAdapter_Exception_Skype,e);
				}
			}
		} else
			throw new CallException(
					Messages.SkypeCallSession_Exception_Invalid_Receiver);
	}

}
