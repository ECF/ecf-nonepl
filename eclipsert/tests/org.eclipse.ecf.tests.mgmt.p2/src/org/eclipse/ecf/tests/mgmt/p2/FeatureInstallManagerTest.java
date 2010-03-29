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

import java.net.URI;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IVersionedId;
import org.eclipse.ecf.mgmt.p2.VersionedId;
import org.eclipse.ecf.mgmt.p2.install.host.FeatureInstallManager;
import org.eclipse.ecf.mgmt.p2.profile.host.ProfileManager;
import org.eclipse.ecf.mgmt.p2.repository.host.RepositoryManager;

public class FeatureInstallManagerTest extends TestCase {

	public static final String REPO_LOCATION = "http://download.eclipse.org/rt/ecf/3.2/3.6/site.p2";

	public static final String FEATURE_ID = "org.eclipse.ecf.core.feature.group";
	public static final String FEATURE_VERSION = "3.2.0.v20100219-1253";

	private FeatureInstallManager featureInstallManager;
	private RepositoryManager repositoryManager;
	private ProfileManager profileManager;

	protected void setUp() throws Exception {
		super.setUp();
		repositoryManager = new RepositoryManager(Activator.getDefault()
				.getProvisioningAgent());
		featureInstallManager = new FeatureInstallManager(Activator
				.getDefault().getContext(), Activator.getDefault()
				.getProvisioningAgent());
		profileManager = new ProfileManager(
				Activator.getDefault().getContext(), Activator.getDefault()
						.getProvisioningAgent());
	}

	protected void tearDown() throws Exception {
		if (featureInstallManager != null) {
			featureInstallManager.close();
			featureInstallManager = null;
		}
		if (repositoryManager != null) {
			repositoryManager.close();
			repositoryManager = null;
		}
		if (profileManager != null) {
			profileManager.close();
			profileManager = null;
		}
		super.tearDown();
	}

	public void testFeatureInstallManagerGetFeatures() throws Exception {
		IVersionedId[] featureIds = featureInstallManager
				.getInstalledFeatures();
		assertNotNull(featureIds);
	}

	public void testFeatureInstall() throws Exception {
		// Add repo
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());

		// Install ecf core
		IVersionedId featureId = new VersionedId(FEATURE_ID, FEATURE_VERSION);
		result = featureInstallManager.installFeature(featureId);
		assertNotNull(result);
		assertTrue(result.isOK());
		// Now uninstall feature
		result = featureInstallManager.uninstallFeature(featureId);
		assertNotNull(result);
		assertTrue(result.isOK());

		result = repositoryManager.removeRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
	}
}
