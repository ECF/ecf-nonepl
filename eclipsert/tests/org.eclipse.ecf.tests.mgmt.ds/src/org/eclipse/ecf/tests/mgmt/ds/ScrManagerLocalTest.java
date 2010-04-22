/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.mgmt.ds;

import org.eclipse.ecf.mgmt.ds.IComponentInfo;
import org.eclipse.ecf.mgmt.ds.host.ScrManager;
import org.eclipse.ecf.mgmt.framework.BundleId;
import org.eclipse.ecf.tests.ECFAbstractTestCase;

public class ScrManagerLocalTest extends ECFAbstractTestCase {

	ScrManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new ScrManager(Activator.getDefault().getContext());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.close();
	}
	
	public void testGetAllComponents() throws Exception {
		IComponentInfo[] components = manager.getComponents();
		assertNotNull(components);
	}
	
	public void testGetP2BundleComponents() throws Exception {
		IComponentInfo[] components = manager.getComponents(new BundleId("org.eclipse.equinox.p2.core"));
		assertNotNull(components);
		assertTrue(components.length > 0);
	}

}
