/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.p2.repository.host.Activator;
import org.eclipse.ecf.mgmt.p2.IInstallableUnitInfo;
import org.eclipse.ecf.mgmt.p2.InstallableUnitInfo;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryInfo;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryManager;
import org.eclipse.ecf.mgmt.p2.repository.RepositoryInfo;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

public class RepositoryManager implements IRepositoryManager, IAdaptable {

	private IProvisioningAgent agent;

	public RepositoryManager(IProvisioningAgent agent) {
		this.agent = agent;
	}

	public URI[] getKnownArtifactRepositories(Integer flags) {
		IArtifactRepositoryManager manager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;
		if (flags == null)
			return manager
					.getKnownRepositories(IArtifactRepositoryManager.REPOSITORIES_ALL);
		else
			return manager.getKnownRepositories(flags.intValue());
	}

	public URI[] getKnownMetadataRepositories(Integer flags) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;
		if (flags == null)
			return manager
					.getKnownRepositories(IArtifactRepositoryManager.REPOSITORIES_ALL);
		else
			return manager.getKnownRepositories(flags.intValue());
	}

	public IStatus addArtifactRepository(URI location, Integer flags) {
		IArtifactRepositoryManager manager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			if (flags == null)
				manager.loadRepository(location, null);
			else
				manager.loadRepository(location, flags.intValue(), null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}

		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			manager.createRepository(location, repositoryName,
					IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Cannot add artifact repository", e);
		}
	}

	public IStatus addMetadataRepository(URI location, Integer flags) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No metadata repository manager found");
		try {
			if (flags == null)
				manager.loadRepository(location, null);
			else
				manager.loadRepository(location, flags.intValue(), null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}

		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			manager.createRepository(location, repositoryName,
					IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Cannot add metadata repository", e);
		}
	}

	public IStatus removeArtifactRepository(URI location) {
		IArtifactRepositoryManager manager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		manager.removeRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	public IStatus removeMetadataRepository(URI location) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No metadata repository manager found");
		manager.removeRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	public IStatus addRepository(URI location, Integer flags) {
		// add metadata repository
		IStatus metadataStatus = addMetadataRepository(location, flags);
		// If it failed, we're done
		if (!metadataStatus.isOK())
			return metadataStatus;
		// If everything's ok with metadata repo
		IStatus artifactStatus = addArtifactRepository(location, flags);
		if (artifactStatus.isOK())
			return new SerializableStatus(Status.OK_STATUS);
		return new SerializableStatus(new MultiStatus(Activator.PLUGIN_ID,
				IStatus.ERROR,
				new IStatus[] { metadataStatus, artifactStatus },
				"addRepository for location=" + location + " failed", null));
	}

	public IStatus addRepository(URI location) {
		return addRepository(location, null);
	}

	public IStatus removeRepository(URI location) {
		// remove metadata repository
		removeMetadataRepository(location);
		// remove artifact repository
		removeArtifactRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	public IStatus refreshArtifactRepository(URI location) {
		IArtifactRepositoryManager manager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			manager.refreshRepository(location, null);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
		return new SerializableStatus(Status.OK_STATUS);
	}

	public IStatus refreshMetadataRepository(URI location) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			manager.refreshRepository(location, null);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
		return new SerializableStatus(Status.OK_STATUS);
	}

	public IStatus refreshRepository(URI location) {
		// refresh metadata repository
		IStatus metadataStatus = refreshMetadataRepository(location);
		// If it failed, we're done
		if (!metadataStatus.isOK())
			return metadataStatus;
		// If everything's ok with metadata repo
		IStatus artifactStatus = refreshArtifactRepository(location);
		if (artifactStatus.isOK())
			return new SerializableStatus(Status.OK_STATUS);
		return new SerializableStatus(new MultiStatus(Activator.PLUGIN_ID,
				IStatus.ERROR,
				new IStatus[] { metadataStatus, artifactStatus },
				"refresh failed for location=" + location, null));
	}

	private IRepositoryInfo[] getArtifactRepositoryInfo0(URI location,
			Integer flags) {
		IArtifactRepositoryManager manager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;

		URI[] locations = null;
		if (location != null) {
			locations = new URI[] { location };
		} else {
			locations = (flags == null) ? manager
					.getKnownRepositories(IArtifactRepositoryManager.REPOSITORIES_ALL)
					: manager.getKnownRepositories(flags.intValue());
		}
		List repos = new ArrayList();
		for (int i = 0; i < locations.length; i++) {
			try {
				IArtifactRepository repo = manager.loadRepository(locations[i],
						null);
				repos.add(new RepositoryInfo(repo));
			} catch (ProvisionException e) {
				// skip
			}
		}
		return (IRepositoryInfo[]) repos.toArray(new IRepositoryInfo[] {});
	}

	public IRepositoryInfo[] getArtifactRepositoryInfo(Integer flags) {
		return getArtifactRepositoryInfo0(null, flags);
	}

	public IRepositoryInfo getArtifactRepositoryInfo(URI location, Integer flags) {
		return getArtifactRepositoryInfo0(location, flags)[0];
	}

	private IRepositoryInfo[] getMetadataRepositoryInfo0(URI location,
			Integer flags) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;

		URI[] locations = null;
		if (location != null) {
			locations = new URI[] { location };
		} else {
			locations = (flags == null) ? manager
					.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL)
					: manager.getKnownRepositories(flags.intValue());
		}
		List repos = new ArrayList();
		for (int i = 0; i < locations.length; i++) {
			try {
				IMetadataRepository repo = manager.loadRepository(locations[i],
						null);
				repos.add(new RepositoryInfo(repo));
			} catch (ProvisionException e) {
				// skip
			}
		}
		return (IRepositoryInfo[]) repos.toArray(new IRepositoryInfo[] {});
	}

	public IRepositoryInfo[] getMetadataRepositoryInfo(Integer flags) {
		return getMetadataRepositoryInfo0(null, flags);
	}

	public IRepositoryInfo getMetadataRepositoryInfo(URI location, Integer flags) {
		if (location == null)
			return null;
		return getMetadataRepositoryInfo0(location, flags)[0];
	}

	public URI[] getKnownMetadataRepositories() {
		return getKnownMetadataRepositories(null);
	}

	public URI[] getKnownArtifactRepositories() {
		return getKnownArtifactRepositories(null);
	}

	public IStatus addMetadataRepository(URI location) {
		return addMetadataRepository(location, null);
	}

	public IStatus addArtifactRepository(URI location) {
		return addArtifactRepository(location, null);
	}

	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		final IAdapterManager adapterManager = Activator.getDefault()
				.getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

	private IStatus createErrorStatus(String message, Throwable t) {
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.ERROR, message, t);
	}

	private IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

	public void close() {
		this.agent = null;
	}

	public IRepositoryInfo[] getArtifactRepositoryInfo() {
		return getArtifactRepositoryInfo((Integer) null);
	}

	public IRepositoryInfo getArtifactRepositoryInfo(URI location) {
		return getArtifactRepositoryInfo(location, null);
	}

	public IRepositoryInfo[] getMetadataRepositoryInfo() {
		return getMetadataRepositoryInfo((Integer) null);
	}

	public IRepositoryInfo getMetadataRepositoryInfo(URI location) {
		return getMetadataRepositoryInfo(location, null);
	}

	public IInstallableUnitInfo[] getInstallableFeatures(URI location) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;
		IQueryable queryable = null;
		if (location == null) {
			queryable = manager;
		} else {
			try {
				queryable = manager.loadRepository(location, null);
			} catch (Exception e) {
				return null;
			}
		}
		if (queryable == null)
			return null;
		IInstallableUnit[] units = (IInstallableUnit[]) queryable.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class);
		if (units == null)
			return null;
		List results = new ArrayList();
		for (int i = 0; i < units.length; i++)
			results.add(new InstallableUnitInfo(units[i]));
		return (IInstallableUnitInfo[]) results
				.toArray(new IInstallableUnitInfo[] {});
	}
}
