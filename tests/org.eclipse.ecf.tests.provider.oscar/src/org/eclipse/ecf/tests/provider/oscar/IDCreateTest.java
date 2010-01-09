/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.oscar;

import junit.framework.TestCase;
import org.eclipse.ecf.core.identity.*;

public class IDCreateTest extends TestCase {

	public static final String OSCAR_NAMESPACE = "ecf.oscar.icqlib"; //$NON-NLS-1$

	private Namespace namespace;

	protected void setUp() throws Exception {
		namespace = IDFactory.getDefault().getNamespaceByName(OSCAR_NAMESPACE);
		assertNotNull(namespace);
	}

	public void testOSCARCreateID() throws Exception {
		final ID oscarID = IDFactory.getDefault().createID(namespace, "217709"); //$NON-NLS-1$
		assertNotNull(oscarID);
	}

	public void testOSCARCreateID1() throws Exception {
		try {
			IDFactory.getDefault().createID(namespace, "21770a"); //$NON-NLS-1$
			fail();
		} catch (final IDCreateException e) {
			// this construction shuld fail
		}
	}

	public void testOSCARCreateIDWithNull() throws Exception {
		try {
			IDFactory.getDefault().createID(namespace, ""); //$NON-NLS-1$
			fail();
		} catch (final IDCreateException e) {
			// this construction shuld fail
		}
	}
}
