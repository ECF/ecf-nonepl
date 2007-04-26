/****************************************************************************
 * Copyright (c) 2006, 2007 Pierre-Henry Perret, work2gather, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pierre-Henry Perret <phperret@gmail.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.xmpp.jingle;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.jingle.IncomingJingleSession;
import org.jivesoftware.smackx.jingle.JingleListener;
import org.jivesoftware.smackx.jingle.JingleManager;
import org.jivesoftware.smackx.jingle.JingleSessionRequest;
import org.jivesoftware.smackx.jingle.PayloadType;
import org.jivesoftware.smackx.nat.STUNResolver;
import org.jivesoftware.smackx.nat.TransportResolver;

public class XMPPJingleContainer implements ICallSessionContainerAdapter, ICallSessionListener {

	private TransportResolver tm= null;
	private XMPPConnection conn= null;
	private JingleManager jm= null;
	private String jID= null;
	private IContainer container= null;
	
	public XMPPJingleContainer(IContainer container,  XMPPConnection conn){
		this.container = container;
		this.conn= conn;
		tm= new STUNResolver();
		jm= new JingleManager( conn , tm );
		jID= conn.getConnectionID();
		
		// install request requestListener for incoming sessions
		jm.addJingleSessionRequestListener(new JingleListener.SessionRequest() {
			/**
			 * Called when a new session request is detected
			 */
			public void sessionRequested(final JingleSessionRequest request) {
				System.out.println("Session request detected, from "+ request.getFrom());
				// We accept the request
				IncomingJingleSession session1 = request.accept(getTestPayloads());
				try {
					session1.start(request);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected XMPPConnection getConnection() {
		return conn;
	}

	public Namespace getReceiverNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public void handleCallSessionEvent(ICallSessionEvent event) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Generate a list of payload types
	 * 
	 * @return A testing list
	 */
	private ArrayList getTestPayloads() {
		ArrayList result = new ArrayList();
	
		result.add(new PayloadType.Audio(34, "supercodec-1", 2, 14000));
		result.add(new PayloadType.Audio(56, "supercodec-2", 1, 44000));
		result.add(new PayloadType.Audio(36, "supercodec-3", 2, 28000));
		result.add(new PayloadType.Audio(45, "supercodec-4", 1, 98000));
	
		return result;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#initiateCall(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(ID receiver, ICallSessionListener listener,
			Map options) throws CallException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#initiateCall(org.eclipse.ecf.core.identity.ID[], org.eclipse.ecf.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(ID[] receivers, ICallSessionListener listener,
			Map options) throws CallException {
		// TODO Auto-generated method stub
		
	}

	public Namespace getCallSessionNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#addListener(org.eclipse.ecf.call.ICallSessionRequestListener)
	 */
	public void addCallSessionRequestListener(ICallSessionRequestListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.call.ICallSessionContainerAdapter#removeListener(org.eclipse.ecf.call.ICallSessionRequestListener)
	 */
	public void removeCallSessionRequestListener(ICallSessionRequestListener listener) {
		// TODO Auto-generated method stub
		
	}

}
