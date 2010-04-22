/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.mgmt.framework;

import org.eclipse.ecf.mgmt.framework.BundleId;
import org.eclipse.ecf.mgmt.framework.IServiceInfo;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.eclipse.ecf.tests.ECFAbstractTestCase;

public class ServiceManagerLocalTest extends ECFAbstractTestCase {

	ServiceManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new ServiceManager(Activator.getDefault().getContext());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.close();
	}
	
	public void testGetServiceZeroInfo() throws Exception {
		IServiceInfo service = manager.getService(new Long(2));
		assertNotNull(service);
		assertTrue(service.getServiceId() == 2);
	}
	
	public void testGetAllServiceInfo() throws Exception {
		IServiceInfo[] services = manager.getServices();
		assertNotNull(services);
		assertTrue(services.length > 2);
	}
	
	public void testGetSystemBundleServices() throws Exception {
		IServiceInfo[] services = manager.getServices(new BundleId("org.eclipse.osgi"));
		assertNotNull(services);
		assertTrue(services.length > 1);
	}
	
}
