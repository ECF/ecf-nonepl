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
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IInstallableUnitInfo;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryInfo;
import org.eclipse.ecf.mgmt.p2.repository.host.RepositoryManager;

public class RepositoryManagerTest extends TestCase {

	public static final String REPO_LOCATION = "http://www.composent.com/knoplerfish/repo";
	
	private RepositoryManager repositoryManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		repositoryManager = new RepositoryManager(Activator.getDefault().getProvisioningAgent());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		if (repositoryManager != null) {
			repositoryManager.close();
			repositoryManager = null;
		}
	}
	
	public void testRepositoryManagerGetKnownArtifactRepositories() throws Exception {
		URI[] knownRepos = repositoryManager.getKnownArtifactRepositories();
		assertNotNull(knownRepos);
	}
	public void testRepositoryManagerGetKnownMetadataRepositories() throws Exception {
		URI[] knownRepos = repositoryManager.getKnownArtifactRepositories();
		assertNotNull(knownRepos);
	}
	
	protected boolean checkForMetadataRepository(URI location) throws Exception {
		URI[] knownRepos = repositoryManager.getKnownMetadataRepositories();
		assertNotNull(knownRepos);
		return Arrays.asList(knownRepos).contains(location);
	}

	protected boolean checkForArtifactRepository(URI location) throws Exception {
		URI[] knownRepos = repositoryManager.getKnownArtifactRepositories();
		assertNotNull(knownRepos);
		return Arrays.asList(knownRepos).contains(location);
	}

	public void testRepositoryManagerAddMetadataRepository() throws Exception {
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addMetadataRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		assertTrue(checkForMetadataRepository(repoLocation));
		// remove location and test that it's no longer there
		IStatus removeStatus = repositoryManager.removeMetadataRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForMetadataRepository(repoLocation));
	}
	
	public void testRepositoryManagerAddArtifactRepository() throws Exception {
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addArtifactRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		assertTrue(checkForArtifactRepository(repoLocation));
		// remove location and test that it's no longer there
		IStatus removeStatus = repositoryManager.removeArtifactRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForArtifactRepository(repoLocation));
	}
	
	public void testRepositoryManagerAddRepository() throws Exception {
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		assertTrue(checkForMetadataRepository(repoLocation));
		assertTrue(checkForArtifactRepository(repoLocation));
		// remove location and test that it's no longer there
		IStatus removeStatus = repositoryManager.removeRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForMetadataRepository(repoLocation));
		assertFalse(checkForArtifactRepository(repoLocation));
	}
	
	public void testGetArtifactRepositoryInfo() throws Exception {
		// First add it
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addArtifactRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		// Then get info
		IRepositoryInfo repoInfo = repositoryManager.getArtifactRepositoryInfo(repoLocation, null);
		printRepoInfo(repoInfo);
		assertNotNull(repoInfo);
		assertNotNull(repoInfo.getLocation());
		assertNotNull(repoInfo.getType());
		
		// Then removeit 
		IStatus removeStatus = repositoryManager.removeArtifactRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForArtifactRepository(repoLocation));
	}

	public void testGetMetadataRepositoryInfo() throws Exception {
		// First add it
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addMetadataRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		// Then get info
		IRepositoryInfo repoInfo = repositoryManager.getMetadataRepositoryInfo(repoLocation, null);
		printRepoInfo(repoInfo);
		assertNotNull(repoInfo);
		assertNotNull(repoInfo.getLocation());
		assertNotNull(repoInfo.getType());
		
		// Then removeit 
		IStatus removeStatus = repositoryManager.removeMetadataRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForMetadataRepository(repoLocation));
	}

	public void testGetMetadataRepositoriesInfo() throws Exception {
		// First add it
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addMetadataRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		// Then get info
		IRepositoryInfo[] repoInfo = repositoryManager.getMetadataRepositoryInfo();
		assertNotNull(repoInfo);
		for(int i=0; i < repoInfo.length; i++) {
			printRepoInfo(repoInfo[i]);
			assertNotNull(repoInfo[i].getLocation());
			assertNotNull(repoInfo[i].getType());
		}
		
		// Then removeit 
		IStatus removeStatus = repositoryManager.removeMetadataRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForMetadataRepository(repoLocation));
	}

	public void testGetArtifactRepositoriesInfo() throws Exception {
		// First add it
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addArtifactRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		// Then get info
		IRepositoryInfo[] repoInfo = repositoryManager.getArtifactRepositoryInfo();
		assertNotNull(repoInfo);
		for(int i=0; i < repoInfo.length; i++) {
			printRepoInfo(repoInfo[i]);
			assertNotNull(repoInfo[i].getLocation());
			assertNotNull(repoInfo[i].getType());
		}
		
		// Then removeit 
		IStatus removeStatus = repositoryManager.removeArtifactRepository(repoLocation);
		assertNotNull(removeStatus);
		assertTrue(removeStatus.isOK());
		assertFalse(checkForArtifactRepository(repoLocation));
	}

	public void testGetFeatures() throws Exception {
		// First add it
		URI repoLocation = new URI(REPO_LOCATION);
		IStatus result = repositoryManager.addMetadataRepository(repoLocation);
		assertNotNull(result);
		assertTrue(result.isOK());
		
		// Then get features
		IInstallableUnitInfo[] ius = repositoryManager.getInstallableFeatures(null);
		assertNotNull(ius);
		assertTrue(ius.length > 0);
		
		repositoryManager.removeMetadataRepository(repoLocation);
		
	}
	private void printRepoInfo(IRepositoryInfo repoInfo) {
		System.out.println("repoInfo="+repoInfo);
		System.out.println("  name="+repoInfo.getName());
		System.out.println("  type="+repoInfo.getType());
		System.out.println("  location="+repoInfo.getLocation());
		System.out.println("  provider="+repoInfo.getProvider());
		System.out.println("  properties="+repoInfo.getProperties());
		System.out.println("  modifiable="+repoInfo.isModifiable());
		System.out.println("  description="+repoInfo.getDescription());
		System.out.println("  version="+repoInfo.getVersion());
	}
}
