/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.install.host;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.p2.install.host.Activator;
import org.eclipse.ecf.mgmt.p2.IVersionedId;
import org.eclipse.ecf.mgmt.p2.VersionedId;
import org.eclipse.ecf.mgmt.p2.install.IFeatureInstallManager;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IPhaseSet;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.PhaseSetFactory;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.query.Collector;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class FeatureInstallManager implements IFeatureInstallManager,
		IAdaptable {

	private BundleContext context;
	private IProvisioningAgent agent;
	private ServiceTracker configuratorTracker;
	private ServiceTracker profileRegistryTracker;

	public FeatureInstallManager(BundleContext context, IProvisioningAgent agent) {
		this.context = context;
		this.agent = agent;
	}

	public IStatus installFeature(IVersionedId featureId, URI[] repoLocations,
			String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("installFeature: featureid to install must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get local profile registry...another sanity check
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("installFeature: no profile registry available");
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("installFeature: no profile matching profileId="
					+ profileId);

		IProgressMonitor monitor = new NullProgressMonitor();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		// Query available/specified repository locations for given installable
		// unit id and unitVersion
		List featuresToInstall = new ArrayList();
		try {
			IQueryResult[] qresults = getInstallableUnits(
					agent,
					repoLocations,
					QueryUtil.createIUQuery(unitId, Version.create(unitVersion)),
					monitor);
			for (int i = 0; i < qresults.length; i++) {
				for (Iterator it = qresults[i].iterator(); it.hasNext();) {
					featuresToInstall.add(it.next());
				}
			}
		} catch (ProvisionException e) {
			// Could not load a metadata repository...report back in failed
			String message = "installFeature: could not load metadata repository.  FeatureId="
					+ featureId.getId() + ",v=" + featureId.getVersion();
			logException(message, e);
			return createErrorStatus(message, repoLocations, e);
		}

		if (featuresToInstall.isEmpty())
			return createErrorStatus("installFeature: feature="
					+ unitId
					+ ",v="
					+ unitVersion
					+ " not found in repositories.  Cannot continue with install.");
		
		// We take the first one...if there are multiple returned from given set
		// of repos This means that we take the feature to install from the first repo
		// that has the given feature (with given version)
		IInstallableUnit featureToInstall = (IInstallableUnit) featuresToInstall
				.get(0);
		List fToInstall = new ArrayList();
		fToInstall.add(featureToInstall);
		return installOrUninstallIUs(fToInstall, profile, monitor, true);
	}

	private IStatus installOrUninstallIUs(Collection features,
			IProfile profile, IProgressMonitor monitor, boolean install) {
		// Make sure we have planner
		IPlanner planner = (IPlanner) agent.getService(IPlanner.SERVICE_NAME);
		if (planner == null)
			return createErrorStatus("no planner available");
		// Make sure we have engine
		IEngine engine = (IEngine) agent.getService(IEngine.SERVICE_NAME);
		if (engine == null)
			return createErrorStatus("No engine available");

		// Create provisioning context
		ProvisioningContext provContext = new ProvisioningContext(agent);
		// Create profile change request
		IProfileChangeRequest request = planner.createChangeRequest(profile);
		// Add feature to install
		if (install) request.addAll(features);
		else request.removeAll(features);
		// Get provisioning plan
		IProvisioningPlan result = planner.getProvisioningPlan(request,
				provContext, monitor);
		// Execute plan
		IStatus engineResult = executePlan(result, engine, provContext, monitor);
		return new SerializableStatus(engineResult);
	}

	public IStatus installFeature(IVersionedId featureId, URI[] repoLocations) {
		return installFeature(featureId, repoLocations, null);
	}

	public IStatus installFeature(IVersionedId featureId, String profileId) {
		return installFeature(featureId, null, profileId);
	}

	public IStatus installFeature(IVersionedId featureId) {
		return installFeature(featureId, null, null);
	}

	public IStatus updateFeature(IVersionedId featureId, URI[] repoLocations,
			String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("featureid to update must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get local profile registry...another sanity check
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no profile matching profileId="
					+ profileId);

		// For given profile, we should find some version to remove
		IProgressMonitor monitor = new NullProgressMonitor();
		IQueryResult installedFeatures = profile.query(
				QueryUtil.createIUQuery(featureId.getId()), monitor);
		// If no installed features for given featureId
		if (installedFeatures.isEmpty())
			return createErrorStatus("updateFeature: Feature="
					+ featureId.getId() + " not found installed in profile="
					+ profileId + " so it cannot be updated");

		// We have some installed features...so we select the one with the
		// highest version
		IInstallableUnit maxInstalledFeature = null;
		for (Iterator i = installedFeatures.iterator(); i.hasNext();) {
			IInstallableUnit current = (IInstallableUnit) i.next();
			if (maxInstalledFeature == null)
				maxInstalledFeature = current;
			else {
				Version currentV = current.getVersion();
				Version maxV = maxInstalledFeature.getVersion();
				int compareV = currentV.compareTo(maxV);
				if (compareV > 0)
					maxInstalledFeature = current;
			}
		}

		// Now have maxInstalledVersion
		org.eclipse.equinox.p2.metadata.VersionedId p2VersionedId = new org.eclipse.equinox.p2.metadata.VersionedId(
				featureId.getId(), featureId.getVersion());
		Version updateVersion = p2VersionedId.getVersion();

		Version maxInstalledFeatureVersion = maxInstalledFeature.getVersion();
		// If the installed version is greater than the requested version, then
		// we have nothing to do
		if (maxInstalledFeatureVersion.compareTo(updateVersion) > 0)
			return createErrorStatus("updateFeature: Nothing to update.  Feature="
					+ featureId.getId()
					+ ",v="
					+ featureId.getVersion()
					+ " is older than installed feature v="
					+ p2VersionedId.getVersion());

		// Now query metadata repositories for desired features
		List possibleFeaturesToInstall = new ArrayList();
		try {
			IQueryResult[] qresults = getInstallableUnits(
					agent,
					repoLocations,
					QueryUtil.createIUQuery(featureId.getId(),
							p2VersionedId.getVersion()), monitor);
			for (int i = 0; i < qresults.length; i++) {
				for (Iterator it = qresults[i].iterator(); it.hasNext();) {
					possibleFeaturesToInstall.add(it.next());
				}
			}
		} catch (ProvisionException e) {
			String message = "updateFeature: Could not load metadata repository.  FeatureId="
					+ featureId.getId() + ",v=" + featureId.getVersion();
			logException(message, e);
			return createErrorStatus(message, repoLocations, e);

		}
		IInstallableUnit featureToUpdate = null;
		// Find iu to update
		for (Iterator i = possibleFeaturesToInstall.iterator(); i.hasNext();) {
			IInstallableUnit current = (IInstallableUnit) i.next();
			Version currentV = current.getVersion();
			if ((currentV.compareTo(maxInstalledFeatureVersion) > 0)
					&& (featureToUpdate == null || currentV
							.compareTo(featureToUpdate.getVersion()) > 0))
				featureToUpdate = current;
		}

		if (featureToUpdate == null)
			return createErrorStatus("updateFeature: Nothing to update.  Could not find feature="
					+ featureId.getId()
					+ " with version="
					+ featureId.getVersion()
					+ " greater than in installed version in repositories");

		List fToInstall = new ArrayList();
		fToInstall.add(featureToUpdate);
		return installOrUninstallIUs(fToInstall, profile, monitor, true);
	}

	public IStatus updateFeature(IVersionedId featureId, URI[] repoLocations) {
		return updateFeature(featureId, repoLocations, null);
	}

	public IStatus updateFeature(IVersionedId featureId, String profileId) {
		return updateFeature(featureId, null, profileId);
	}

	public IStatus updateFeature(IVersionedId featureId) {
		return updateFeature(featureId, null, null);
	}

	public IStatus uninstallFeature(IVersionedId featureId,
			URI[] repoLocations, String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("featureid to install must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get local profile registry...another sanity check
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no matching profileId=" + profileId);

		IProgressMonitor monitor = new NullProgressMonitor();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		// For given profile, we should find the specific version to remove
		IQueryResult units = profile.query(
				QueryUtil.createIUQuery(unitId, Version.create(unitVersion)),
				monitor);
		
		// If we didn't find it installed then we can't uninstall it
		if (units.isEmpty()) return createErrorStatus("uninstallFeature: Feature="+unitId+" with v="+unitVersion+" not found installed, so cannot be uninstalled");

		return installOrUninstallIUs(units.toUnmodifiableSet(), profile, monitor, false);

	}

	public IStatus uninstallFeature(IVersionedId featureId, URI[] repoLocations) {
		return uninstallFeature(featureId, repoLocations, null);
	}

	public IStatus uninstallFeature(IVersionedId featureId, String profileId) {
		return uninstallFeature(featureId, null, profileId);
	}

	public IStatus uninstallFeature(IVersionedId featureId) {
		return uninstallFeature(featureId, null, null);
	}

	public IStatus applyConfiguration() {
		Configurator configurator = getConfigurator();
		if (configurator == null)
			return createErrorStatus("Configurator is null");
		try {
			configurator.applyConfiguration();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (IOException e) {
			return createErrorStatus("Could not apply configuration", e);
		}
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

	public void close() {
		if (configuratorTracker != null) {
			configuratorTracker.close();
			configuratorTracker = null;
		}
		if (profileRegistryTracker != null) {
			profileRegistryTracker.close();
			profileRegistryTracker = null;
		}
		this.agent = null;
		this.context = null;
	}

	private IStatus createErrorStatus(String message, Throwable t) {
		return createErrorStatus(message, null, t);
	}

	private IStatus createErrorStatus(String message) {
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.ERROR, message, null);
	}

	private IStatus createErrorStatus(String message, URI[] repoLocations,
			Throwable t) {
		repoLocations = (repoLocations == null) ? getMetadataRepositories(agent)
				: repoLocations;
		StringBuffer sb = new StringBuffer(message);
		if (repoLocations != null) {
			sb.append("\n\t").append("Repository locations accessed:\n");
			for (int i = 0; i < repoLocations.length; i++)
				sb.append("\t\t").append(repoLocations[i]).append("\n");
		} else
			sb.append("\n\t").append("No repositories accessed").append("\n");
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.ERROR, message, t);
	}

	protected void logException(String s, Throwable throwable) {
	}

	private synchronized Configurator getConfigurator() {
		if (configuratorTracker == null) {
			configuratorTracker = new ServiceTracker(
					context,
					org.eclipse.equinox.internal.provisional.configurator.Configurator.class
							.getName(), null);
			configuratorTracker.open();
		}
		return (Configurator) configuratorTracker.getService();
	}

	private IQueryResult[] getInstallableUnits(IProvisioningAgent agent,
			URI[] locations, IQuery query, IProgressMonitor monitor)
			throws ProvisionException {
		IQueryable[] queryables = (locations == null) ? new IQueryable[] { (IMetadataRepository) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME) }
				: getMetadataRepositories(agent, locations, monitor);
		if (queryables != null) {
			List queryResults = new ArrayList();
			for (int i = 0; i < queryables.length; i++) {
				IQueryResult queryResult = queryables[i].query(query, monitor);
				if (queryResult != null)
					queryResults.add(queryResult);
			}
			return (IQueryResult[]) queryResults.toArray(new IQueryResult[] {});
		}
		return new IQueryResult[] { Collector.emptyCollector() };
	}

	private IMetadataRepository getMetadataRepository(IProvisioningAgent agent,
			URI location, IProgressMonitor monitor) throws ProvisionException {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			throw new ProvisionException("No metadata repository manager found");
		return manager.loadRepository(location, monitor);
	}

	private IMetadataRepository[] getMetadataRepositories(
			IProvisioningAgent agent, URI[] locations, IProgressMonitor monitor)
			throws ProvisionException {
		if (locations == null)
			return new IMetadataRepository[] { getMetadataRepository(agent,
					null, monitor) };
		else {
			List results = new ArrayList();
			for (int i = 0; i < locations.length; i++) {
				results.add(getMetadataRepository(agent, locations[i], monitor));
			}
			return (IMetadataRepository[]) results
					.toArray(new IMetadataRepository[] {});
		}
	}

	private URI[] getMetadataRepositories(IProvisioningAgent agent) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			// TODO log here
			return null;
		URI[] repos = manager
				.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
		if (repos.length > 0)
			return repos;
		return null;
	}

	private IStatus executePlan(IProvisioningPlan result, IEngine engine,
			ProvisioningContext context, IProgressMonitor progress) {
		return executePlan(result, engine,
				PhaseSetFactory.createDefaultPhaseSet(), context, progress);
	}

	private IStatus executePlan(IProvisioningPlan result, IEngine engine,
			IPhaseSet phaseSet, ProvisioningContext context,
			IProgressMonitor progress) {
		if (!result.getStatus().isOK())
			return result.getStatus();

		if (result.getInstallerPlan() != null) {
			IStatus installerPlanStatus = engine.perform(
					result.getInstallerPlan(), phaseSet, progress);
			if (!installerPlanStatus.isOK())
				return installerPlanStatus;
		}
		return engine.perform(result, phaseSet, progress);
	}

	public IVersionedId[] getInstalledFeatures() {
		return getInstalledFeatures(null);
	}

	public IVersionedId[] getInstalledFeatures(String profileId) {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return null;
		if (profileId == null)
			profileId = IProfileRegistry.SELF;

		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return null;
		IInstallableUnit[] ius = (IInstallableUnit[]) profile.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class);
		IVersionedId[] featureids = new IVersionedId[ius.length];
		for (int i = 0; i < featureids.length; i++) {
			featureids[i] = new VersionedId(ius[i].getId(), ius[i].getVersion()
					.toString());
		}
		return featureids;
	}

	public IVersionedId[] getInstallableFeatures(URI location) {
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
		for (int i = 0; i < units.length; i++) {
			Version ver = units[i].getVersion();
			results.add(new VersionedId(units[i].getId(), (ver == null) ? null
					: ver.toString()));
		}
		return (IVersionedId[]) results.toArray(new IVersionedId[] {});
	}

	public IVersionedId[] getInstallableFeatures() {
		return getInstallableFeatures(null);
	}

}
