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

	public IStatus installFeature(IVersionedId featureId, String profileId) {
		if (featureId == null)
			return createErrorStatus("featureid to install must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;

		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no matching profile");
		IProgressMonitor monitor = new NullProgressMonitor();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		IQueryResult ius = getInstallableUnits(agent, (URI) null, QueryUtil
				.createIUQuery(unitId, Version.create(unitVersion)), monitor);
		if (ius.isEmpty()) {
			StringBuffer error = new StringBuffer();
			error.append("Installable unit not found: " + unitId + ' '
					+ unitVersion + '\n');
			error.append("Repositories searched:\n");
			URI[] repos = getMetadataRepositories(agent);
			if (repos != null) {
				for (int i = 0; i < repos.length; i++)
					error.append(repos[i] + "\n");
			}
			return createErrorStatus("installable unit with id=" + unitId
					+ " and version=" + unitVersion + " not found",
					new ProvisionException(error.toString()));
		}
		IPlanner planner = (IPlanner) agent.getService(IPlanner.SERVICE_NAME);
		if (planner == null)
			return createErrorStatus("no planner available");
		IEngine engine = (IEngine) agent.getService(IEngine.SERVICE_NAME);
		if (engine == null)
			return createErrorStatus("No engine available");

		ProvisioningContext provContext = new ProvisioningContext(agent);

		IProfileChangeRequest request = planner.createChangeRequest(profile);
		request.addAll(ius.toUnmodifiableSet());
		// Get provisioning plan
		IProvisioningPlan result = planner.getProvisioningPlan(request,
				provContext, monitor);
		// Execute plan
		IStatus engineResult = executePlan(result, engine, provContext, monitor);
		return new SerializableStatus(engineResult);
	}

	public IStatus installFeature(IVersionedId featureId) {
		return installFeature(featureId, null);
	}

	public IStatus uninstallFeature(IVersionedId featureId, String profileId) {
		if (featureId == null)
			return createErrorStatus("featureid to uninstall must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;

		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no matching profile=" + profileId);

		IProgressMonitor monitor = new NullProgressMonitor();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		IQueryResult units = profile.query(QueryUtil.createIUQuery(unitId,
				Version.create(unitVersion)), monitor);

		if (units.isEmpty()) {
			StringBuffer error = new StringBuffer();
			error.append("Installable unit not found: " + unitId + ' '
					+ unitVersion + '\n');
			error.append("Repositories searched:\n");
			URI[] repos = getMetadataRepositories(agent);
			if (repos != null) {
				for (int i = 0; i < repos.length; i++)
					error.append(repos[i] + "\n");
			}
			return createErrorStatus("installable unit with id=" + unitId
					+ " and version=" + unitVersion + " not found",
					new ProvisionException(error.toString()));
		}

		IPlanner planner = (IPlanner) agent.getService(IPlanner.SERVICE_NAME);
		if (planner == null)
			return createErrorStatus("no planner available");
		IEngine engine = (IEngine) agent.getService(IEngine.SERVICE_NAME);
		if (engine == null)
			return createErrorStatus("No engine available");

		ProvisioningContext provContext = new ProvisioningContext(agent);

		IProfileChangeRequest request = planner.createChangeRequest(profile);
		request.removeAll(units.toUnmodifiableSet());
		// Get provisioning plan
		IProvisioningPlan result = planner.getProvisioningPlan(request,
				provContext, monitor);
		// Execute plan
		IStatus engineResult = executePlan(result, engine, provContext, monitor);
		return new SerializableStatus(engineResult);
	}

	public IStatus uninstallFeature(IVersionedId featureId) {
		return uninstallFeature(featureId, null);
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
		return new SerializableStatus(4, Activator.PLUGIN_ID, 4, message, t);
	}

	private IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
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

	private IQueryResult getInstallableUnits(IProvisioningAgent agent,
			URI location, IQuery query, IProgressMonitor monitor) {
		IQueryable queryable = (location == null) ? (IQueryable) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME)
				: getMetadataRepository(agent, location, monitor);
		if (queryable != null)
			return queryable.query(query, monitor);
		return Collector.emptyCollector();
	}

	private IMetadataRepository getMetadataRepository(IProvisioningAgent agent,
			URI location, IProgressMonitor monitor) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			throw new IllegalStateException(
					"No metadata repository manager found");
		try {
			return manager.loadRepository(location, monitor);
		} catch (ProvisionException e) {
			return null;
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
		return executePlan(result, engine, PhaseSetFactory
				.createDefaultPhaseSet(), context, progress);
	}

	private IStatus executePlan(IProvisioningPlan result, IEngine engine,
			IPhaseSet phaseSet, ProvisioningContext context,
			IProgressMonitor progress) {
		if (!result.getStatus().isOK())
			return result.getStatus();

		if (result.getInstallerPlan() != null) {
			IStatus installerPlanStatus = engine.perform(result
					.getInstallerPlan(), phaseSet, progress);
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

}
