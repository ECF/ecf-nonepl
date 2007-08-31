/*******************************************************************************
 * Copyright (c) 2007 Moritz Post and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.ecf.provider.jingle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.tests.internal.provider.jingle.XMPPContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for the {@link JingleContainerAdapterFactory} class.
 * 
 * @author Moritz Post
 */
public class JingleContainerAdapterFactoryTest {

	private static JingleContainerAdapterFactory jcaf;

	@BeforeClass
	public static void setupClass() {
		jcaf = new JingleContainerAdapterFactory();
	}

	@Test
	public final void getAdapter() throws Exception {

		// get an xmpp container
		XMPPContainer xmppContainer = XMPPContainerFactory.getInstance().getXMPPContainer();
		
		// correct adaptable class is created
		Object adapter = jcaf.getAdapter(xmppContainer, ICallSessionContainerAdapter.class);
		assertTrue(ICallSessionContainerAdapter.class.isInstance(adapter));

		// no adapter is created
		adapter = jcaf.getAdapter(xmppContainer, null);
		assertNull(adapter);

		adapter = jcaf.getAdapter(null, JingleCallSessionContainerAdapater.class);
		assertNull(adapter);

		adapter = jcaf.getAdapter(null, null);
		assertNull(adapter);

		adapter = jcaf.getAdapter(new String(), String.class);
		assertNull(adapter);

		adapter = jcaf.getAdapter(new String(), null);
		assertNull(adapter);

		adapter = jcaf.getAdapter(null, String.class);
		assertNull(adapter);

	}

	@Test
	public final void getAdapterList() {
		assertArrayEquals(new Class[] { ICallSessionContainerAdapter.class }, jcaf.getAdapterList());
	}

}
