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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.framework.BundleId;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.mgmt.framework.IBundleInfo;
import org.eclipse.ecf.mgmt.framework.host.BundleManager;
import org.eclipse.ecf.tests.ECFAbstractTestCase;
import org.osgi.framework.Bundle;

public class BundleManagerLocalTest extends ECFAbstractTestCase {

	BundleManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new BundleManager(Activator.getDefault().getContext());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.close();
	}
	
	public void testGetBundleIds() throws Exception {
		String[] bundleIds = manager.getBundleSymbolicIds();
		assertNotNull(bundleIds);
		assertTrue(bundleIds.length > 2);
		List l = Arrays.asList(bundleIds);
		assertTrue(l.contains("org.eclipse.osgi"));
	}
	
	public void testGetSystemBundleInfo() throws Exception {
		IBundleInfo bundleInfo = manager.getBundle(new Long(0));
		assertNotNull(bundleInfo);
		assertTrue(bundleInfo.getId() == 0);
	}
	
	public void testGetECFBundleInfo() throws Exception {
		IBundleInfo[] bundleInfo = manager.getBundles(new BundleId("org.eclipse.ecf"));
		assertNotNull(bundleInfo);
		assertTrue(bundleInfo.length == 1);
		assertTrue(bundleInfo[0].getBundleId().getSymbolicName().equals("org.eclipse.ecf"));
	}
	
	protected void verifyBundlePresent(IBundleInfo[] bundleInfo, long bundleid) {
		boolean bundleFound = false;
		for(int i=0; i < bundleInfo.length; i++ ) if (bundleInfo[i].getId() == bundleid) bundleFound = true;
		assertTrue(bundleFound);
	}
	public void testGetAllBundleInfo() throws Exception {
		IBundleInfo[] bundleInfo = manager.getBundles();
		assertNotNull(bundleInfo);
		assertTrue(bundleInfo.length > 5);
		verifyBundlePresent(bundleInfo,0);
	}
	
	public void testStopAndStart() throws Exception {
		IBundleInfo[] bundleInfo = manager.getBundles(new BundleId("org.eclipse.ecf"));
		assertNotNull(bundleInfo);
		assertTrue(bundleInfo.length == 1);
		long ecfBundleid = bundleInfo[0].getId();
		int ecfState = bundleInfo[0].getState();
		IBundleId ecfId = bundleInfo[0].getBundleId();
		
		if (ecfState == Bundle.ACTIVE) {
			IStatus result = manager.stop(ecfId);
			assertNotNull(result);
			assertTrue(result.isOK());
			IBundleInfo bundleInfoUpdated = manager.getBundle(new Long(ecfBundleid));
			assertTrue(bundleInfoUpdated.getState() == Bundle.RESOLVED);
			result = manager.start(ecfId);
			assertNotNull(result);
			assertTrue(result.isOK());
			bundleInfoUpdated = manager.getBundle(new Long(ecfBundleid));
			assertTrue(bundleInfoUpdated.getState() == Bundle.ACTIVE);
		} else {
			IStatus result = manager.start(ecfId);
			assertNotNull(result);
			assertTrue(result.isOK());
			IBundleInfo bundleInfoUpdated = manager.getBundle(new Long(ecfBundleid));
			assertTrue(bundleInfoUpdated.getState() == Bundle.ACTIVE);
			result = manager.stop(ecfId);
			assertNotNull(result);
			assertTrue(result.isOK());
			bundleInfoUpdated = manager.getBundle(new Long(ecfBundleid));
			assertTrue(bundleInfoUpdated.getState() == Bundle.RESOLVED);
		}
	}
}
