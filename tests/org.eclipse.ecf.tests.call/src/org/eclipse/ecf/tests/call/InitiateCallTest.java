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

package org.eclipse.ecf.tests.call;

import junit.framework.TestCase;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;

/**
 * 
 */
public class InitiateCallTest extends TestCase {

	private static final String DEFAULT_CLIENT = "ecf.generic.client"; //$NON-NLS-1$

	protected ICallSessionContainerAdapter getCallContainerAdapter() throws Exception {
		IContainer container = ContainerFactory.getDefault().createContainer(
				DEFAULT_CLIENT);
		return (ICallSessionContainerAdapter) container
				.getAdapter(ICallSessionContainerAdapter.class);
	}

	public void testCallContainerAdapterAccess() throws Exception {
		assertNotNull(getCallContainerAdapter());
	}

	protected ICallSessionRequestListener getRequestListener() {
		return new ICallSessionRequestListener() {

			public void handleCallSessionRequest(ICallSessionRequestEvent event) {
				// TODO Auto-generated method stub
				System.out.println("handleCallSessionRequest("+event+")");
			}
			
		};
	}
	protected ICallSessionListener getListener() {
		return new ICallSessionListener() {
			public void handleCallSessionEvent(ICallSessionEvent event) {
				System.out.println("handleCallSessionEvent(" + event + ")");
			}
		};
	}

	protected String getReceiver() {
		return System.getProperty("receiver");
	}
	
	public void testInitiateCall() throws Exception {
		ICallSessionContainerAdapter adapter = getCallContainerAdapter();
		assertNotNull(adapter);
		String receiver = getReceiver();
		System.out.println("sending call request to user "+receiver);
		adapter.sendCallRequest(IDFactory.getDefault().createID(
				adapter.getReceiverNamespace(), getReceiver()), getListener(),
				null);
	}
}
