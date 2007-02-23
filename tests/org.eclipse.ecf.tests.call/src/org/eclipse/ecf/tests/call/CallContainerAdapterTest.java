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

import org.eclipse.ecf.call.ICallContainerAdapter;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;

import junit.framework.TestCase;

/**
 *
 */
public class CallContainerAdapterTest extends TestCase {

	private static final String DEFAULT_CLIENT = "ecf.generic.client"; //$NON-NLS-1$

	protected ICallContainerAdapter getCallContainerAdapter() throws Exception {
		IContainer container = ContainerFactory.getDefault().createContainer(DEFAULT_CLIENT);
		return (ICallContainerAdapter) container.getAdapter(ICallContainerAdapter.class);
	}
	
	public void testCallContainerAdapterAccess() throws Exception {
		assertNotNull(getCallContainerAdapter());
	}
	
    public void testCreateCallSession() throws Exception {
    	ICallContainerAdapter adapter = getCallContainerAdapter();
    	assertNotNull(adapter);
    	ICallSession callSession = adapter.createCallSession();
    	assertNotNull(callSession);
    	
    }
}
