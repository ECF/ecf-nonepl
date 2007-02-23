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

import org.eclipse.ecf.call.ICallContainerAdapter;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.skype.Activator;
import org.eclipse.ecf.internal.provider.skype.SkypeProviderDebugOptions;

import com.skype.Call;
import com.skype.CallListener;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
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

	CallListener callListener = new CallListener() {
		public void callMaked(Call makedCall) throws SkypeException {
			Trace.trace(Activator.getDefault(), "callMade(" + makedCall + ")");
		}

		public void callReceived(Call receivedCall) throws SkypeException {
			Trace.trace(Activator.getDefault(), "callReceived(" + receivedCall
					+ ")");
		}
	};

	ChatMessageListener chatMessageListener = new ChatMessageListener() {

		public void chatMessageReceived(ChatMessage receivedChatMessage)
				throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "chatMessageReceived("
					+ receivedChatMessage + ")");
		}

		public void chatMessageSent(ChatMessage sentChatMessage)
				throws SkypeException {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "chatMessageSent("
					+ sentChatMessage + ")");
		}

	};

	ConnectorListener connectorListener = new ConnectorListener() {

		public void messageReceived(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "messageReceived("
					+ event.getMessage() + ")");
		}

		public void messageSent(ConnectorMessageEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "messageSent("
					+ event.getMessage() + ")");
		}

		public void statusChanged(ConnectorStatusEvent event) {
			// TODO Auto-generated method stub
			Trace.trace(Activator.getDefault(), "statusChanged("
					+ event.getStatus() + ")");
		}

	};

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
										"SKYPEDEBUG."
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
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#createCallSession()
	 */
	public ICallSession createCallSession() throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#createCallSession(org.eclipse.ecf.core.identity.ID)
	 */
	public ICallSession createCallSession(ID sessionID) throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#getCallSession(org.eclipse.ecf.core.identity.ID)
	 */
	public ICallSession getCallSession(ID callSessionID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#getCallSessionNamespace()
	 */
	public Namespace getCallSessionNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				StringID.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#removeCallSession(org.eclipse.ecf.core.identity.ID)
	 */
	public boolean removeCallSession(ID callSessionID) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.call.ICallContainerAdapter#removeListener(org.eclipse.ecf.call.ICallSessionListener)
	 */
	public void removeListener(ICallSessionListener listener) {
		// TODO Auto-generated method stub

	}

}
