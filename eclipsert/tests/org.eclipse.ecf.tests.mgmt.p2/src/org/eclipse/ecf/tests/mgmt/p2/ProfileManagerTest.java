/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.mgmt.p2;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.profile.IProfileInfo;
import org.eclipse.ecf.mgmt.p2.profile.host.ProfileManager;
import junit.framework.TestCase;

public class ProfileManagerTest extends TestCase {

	private static final String PROFILE_ID_1 = "profile1";
	
	private ProfileManager profileManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profileManager = new ProfileManager(Activator.getDefault().getContext(),Activator.getDefault().getProvisioningAgent());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		if (profileManager != null) {
			profileManager.close();
			profileManager = null;
		}
	}
	
	public void testGetProfileIds() throws Exception {
		String[] profileIds = profileManager.getProfileIds();
		assertNotNull(profileIds);
	}
	
	private IStatus addProfile(String profileId, Map props) {
		return profileManager.addProfile(profileId, props);
	}
	
	private IStatus removeProfile(String profileId) {
		return profileManager.removeProfile(profileId);
	}
	
	public void testAddProfile() throws Exception {
		IStatus result = addProfile(PROFILE_ID_1, null);
		assertTrue(result.isOK());
		result = removeProfile(PROFILE_ID_1);
		assertTrue(result.isOK());
	}
	
	public void testGetProfileInfo() throws Exception {
		// Add profile
		addProfile(PROFILE_ID_1,null);
		
		IProfileInfo profileInfo = profileManager.getProfile(PROFILE_ID_1);
		assertNotNull(profileInfo);
		assertTrue(PROFILE_ID_1.equals(profileInfo.getId()));
		
		removeProfile(PROFILE_ID_1);
		
		profileInfo = profileManager.getProfile(PROFILE_ID_1);
		assertNull(profileInfo);

	}
}
