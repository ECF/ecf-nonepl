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

import org.eclipse.ecf.call.CallSessionException;
import org.eclipse.ecf.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.ICallSessionRequestListener;
import org.eclipse.ecf.call.events.ICallSessionEvent;
import org.eclipse.ecf.call.events.ICallSessionRequestEvent;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;

/**
 * 
 */
public class ReceiveCallTest extends TestCase {

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

			public void handleCallSessionRequest(final ICallSessionRequestEvent event) {
						if (event.getInitiator().getName().equals(initiator)) {
							try {
								event.accept(new ICallSessionListener() {
									public void handleCallSessionEvent(
											ICallSessionEvent event) {
										System.out.println("receiver.handleCallSessionEvent("+event+")");
									}
								});
							} catch (CallSessionException e) {
								e.printStackTrace();
							}
						} else
							event.reject();

		}};
	}

	String initiator = System.getProperty("initiator");
	
	public void testReceiveCall() throws Exception {
		ICallSessionContainerAdapter adapter = getCallContainerAdapter();
		assertNotNull(adapter);
		adapter.addCallSessionRequestListener(getRequestListener());
		System.out.println("waiting for call from "+initiator);
	}
}
