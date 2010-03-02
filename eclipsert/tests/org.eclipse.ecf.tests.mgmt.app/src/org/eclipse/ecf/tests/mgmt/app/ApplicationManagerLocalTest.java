/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.mgmt.app;

import org.eclipse.ecf.mgmt.app.IApplicationInfo;
import org.eclipse.ecf.mgmt.app.IApplicationInstanceInfo;
import org.eclipse.ecf.mgmt.app.host.ApplicationManager;
import org.eclipse.ecf.tests.ECFAbstractTestCase;

public class ApplicationManagerLocalTest extends ECFAbstractTestCase {

	ApplicationManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new ApplicationManager(Activator.getDefault().getContext());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.close();
	}
	
	public void testGetApplications() throws Exception {
		IApplicationInfo[] apps = manager.getApplications();
		assertNotNull(apps);
		assertTrue(apps.length > 1);
	}
	
	public void testGetRunningApplications() throws Exception {
		IApplicationInstanceInfo[] runningApps = manager.getRunningApplications();
		assertNotNull(runningApps);
		assertTrue(runningApps.length > 0);
	}
}
