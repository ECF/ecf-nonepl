/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.oscar;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;

import junit.framework.TestCase;

public class IDCreateTest extends TestCase {

	public static final String OSCAR_NAMESPACE = "ecf.oscar";

	private Namespace namespace;

	protected void setUp() throws Exception {
		namespace = IDFactory.getDefault().getNamespaceByName(OSCAR_NAMESPACE);
		assertNotNull(namespace);
	}

	public void testOSCARCreateID() throws Exception {
		final ID oscarID = IDFactory.getDefault().createID(namespace, "217709");
		assertNotNull(oscarID);
	}

	public void testOSCARCreateID1() throws Exception {
		try {
			IDFactory.getDefault().createID(namespace, "21770a");
			fail();
		}
		catch (final IDCreateException e) {
			// this construction shuld fail
		}
	}

	public void testOSCARCreateIDWithNull() throws Exception {
		try {
			IDFactory.getDefault().createID(namespace, "");
			fail();
		}
		catch (final IDCreateException e) {
			// this construction shuld fail
		}
	}
}
