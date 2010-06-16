package org.remotercp.provisioning.service.install;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.remotercp.authorization.domain.service.IAuthorizationService;
import org.remotercp.provisioning.domain.exception.RemoteOperationException;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;
import org.remotercp.provisioning.domain.version.IVersionedId;
import org.remotercp.provisioning.domain.version.VersionedId;
import org.remotercp.provisioning.service.UpdateActivator;
import org.remotercp.provisioning.service.dialogs.AcceptUpdateDialog;
import org.remotercp.provisioning.service.listener.IProvisioningAgentServiceListener;

/**
 * This class will install, uninstall and update features on the users local RCP
 * application and return a colleciton with status objects in order to inform
 * about the success/flop of the according operation.
 * 
 * TODO: As Eclipse does not remove unistalled features from the disk think
 * about an additional method which will do this step.
 * 
 * @author Eugen Reiswich
 * @date 28.07.2008
 * 
 */
@SuppressWarnings("restriction")
public class InstallFeaturesServiceImpl implements IInstallFeaturesService,
		IProvisioningAgentServiceListener {

	private static final Logger LOGGER = Logger
			.getLogger(InstallFeaturesServiceImpl.class.getName());

	private final ReentrantLock lock = new ReentrantLock(true);

	private IAuthorizationService authorizationService;

	private IProvisioningAgent agent;

	private final IStatus authorizationError = createStatus(Status.ERROR,
			"Administration operation already in progress", null);

	private final IStatus concurrentOperationError = createStatus(Status.ERROR,
			"Administration operation already in progress", null);

	public InstallFeaturesServiceImpl() {
		System.out
				.println("InstallFeaturesServiceImpl.InstallFeaturesServiceImpl()");
		UpdateActivator.getDefault().registerServiceListener(this);
	}

	/**
	 * DS stuff
	 */
	public void bindProvisioningAgent(IProvisioningAgent agent) {
		System.out
				.println("InstallFeaturesServiceImpl.bindProvisioningAgent()");
		this.agent = agent;

	}

	/**
	 * DS stuff
	 */
	public void unbindProvisioningAgent(IProvisioningAgent agent) {
		this.agent = agent;

	}

	/**
	 * DS stuff
	 */
	public void bindAuthorizationService(
			IAuthorizationService authorizationService) {
		System.out
				.println("InstallFeaturesServiceImpl.bindAuthorizationService()");
		this.authorizationService = authorizationService;

	}

	/**
	 * DS stuff
	 */
	public void unbindAuthorizationService(
			IAuthorizationService authorizationService) {
		this.authorizationService = null;
	}

	/**
	 * As Eclipse does not support properly Hot Plugging we need a method to
	 * restart the application after install/update or uninstall operations.
	 */
	public IStatus restartApplication(ID adminId) {
		IStatus result = createStatus(Status.OK, null, null);

		if (lock.tryLock()) {

			if (authorizationService.isAdmin(adminId)) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						PlatformUI.getWorkbench().restart();

					}
				});

				result = createStatus(Status.OK,
						"Application has been sucessfully restarted", null);

			} else {
				result = createStatus(Status.ERROR,
						"The provided user ID is not an admin", null);
			}

			lock.unlock();
		} else {
			result = concurrentOperationError;
		}

		return result;
	}

	/**
	 * Return the installed Features
	 */
	public IVersionedId[] getInstalledFeatures(ID adminId)
			throws RemoteOperationException {
		IVersionedId[] featureids = new IVersionedId[0];

		if (lock.tryLock()) {

			if (authorizationService.isAdmin(adminId)) {

				IProfileRegistry profileRegistry = (IProfileRegistry) agent
						.getService(IProfileRegistry.SERVICE_NAME);
				if (profileRegistry == null) {
					throw new RemoteOperationException(
							"No profile registry found for: "
									);
				}

				IProfile profile = profileRegistry
						.getProfile(IProfileRegistry.SELF);
				if (profile == null)
					return null;
//				IInstallableUnit[] ius = (IInstallableUnit[]) profile.query(
//						QueryUtil.createIUGroupQuery(), null).toArray(
//						IInstallableUnit.class);
				IInstallableUnit[] ius = (IInstallableUnit[]) profile.query(
						QueryUtil.createIUAnyQuery(), null).toArray(
						IInstallableUnit.class);

				featureids = new IVersionedId[ius.length];
				for (int i = 0; i < featureids.length; i++) {
					featureids[i] = new VersionedId(ius[i].getId(), ius[i]
							.getVersion().toString());
				}
			} else {
				throw new RemoteOperationException(
						"The provided user ID is not an admin: "
								+ adminId.toExternalForm());
			}

			lock.unlock();
		} else {
			throw new RemoteOperationException(
					"Administration operation already in progress");
		}
		return featureids;
	}

	public IStatus installFeature(IVersionedId featureId, URI[] repoLocations,
			ID adminId) {

		// TODO: ask user
		IStatus result = new SerializableStatus(Status.OK_STATUS);

		if (lock.tryLock()) {

			if (authorizationService.isAdmin(adminId)) {

				final IProgressMonitor monitor = new NullProgressMonitor();
				ProvisioningSession session = new ProvisioningSession(agent);
				ProvisioningContext context = new ProvisioningContext(agent);

				updateRepositories(context, repoLocations);

				Version version = Version.create(featureId.getVersion());
				IQueryResult<IInstallableUnit> ius = context.getMetadata(
						monitor).query(
						QueryUtil.createIUQuery(featureId.getId(), version),
						monitor);

				final InstallOperation operation = new InstallOperation(
						session, ius.toSet());
				operation.setProvisioningContext(context);
				// operation.setProfileId(profileId);

				result = operation.resolveModal(monitor);
				if (result.isOK()) {
					final ProvisioningJob installJob = operation
							.getProvisioningJob(monitor);
					result = installJob.runModal(monitor);
				}
			} else {
				result = authorizationError;
			}

			lock.unlock();

		} else {
			result = concurrentOperationError;
		}

		return result;
	}

	public IStatus updateFeature(IVersionedId[] versionIds,
			URI[] repoLocations, ID adminId) {
		// TODO: ask user

		IStatus result = new SerializableStatus(Status.OK_STATUS);

		if (lock.tryLock()) {

			if (authorizationService.isAdmin(adminId)) {

				final IProgressMonitor monitor = new NullProgressMonitor();
				ProvisioningSession session = new ProvisioningSession(agent);
				ProvisioningContext context = new ProvisioningContext(agent);

				updateRepositories(context, repoLocations);

				IProfileRegistry profileRegistry = (IProfileRegistry) agent
						.getService(IProfileRegistry.SERVICE_NAME);
				String profileId = IProfileRegistry.SELF;

				IProfile profile = profileRegistry.getProfile(profileId);
				IQueryResult<IInstallableUnit> queryResult = profile.query(
						QueryUtil.createIUGroupQuery(), monitor);

				Set<IInstallableUnit> unitsToUpdate = new HashSet<IInstallableUnit>();
				for (IVersionedId id : versionIds) {
					for (IInstallableUnit unit : queryResult.toSet()) {
						if (id.getId().equals(unit.getId())) {
							unitsToUpdate.add(unit);
						}
					}
				}

				final UpdateOperation operation = new UpdateOperation(session,
						unitsToUpdate);
				operation.setProvisioningContext(context);

				result = operation.resolveModal(monitor);
				if (result.isOK()) {
					final ProvisioningJob updateJob = operation
							.getProvisioningJob(monitor);
					result = updateJob.runModal(monitor);
				}
			} else {
				result = authorizationError;
			}

			lock.unlock();
		} else {
			result = concurrentOperationError;
		}
		return result;
	}

	public IStatus uninstallFeature(IVersionedId featureId, ID adminId) {
		// TODO: ask user
		IStatus result = new SerializableStatus(Status.OK_STATUS);
		if (lock.tryLock()) {

			if (authorizationService.isAdmin(adminId)) {

				final IProgressMonitor monitor = new NullProgressMonitor();
				ProvisioningSession session = new ProvisioningSession(agent);
				ProvisioningContext context = new ProvisioningContext(agent);

				IQueryResult<IInstallableUnit> ius = context.getMetadata(
						monitor).query(
						QueryUtil.createIUQuery(featureId.getId()), monitor);
				final UninstallOperation operation = new UninstallOperation(
						session, ius.toSet());
				operation.setProvisioningContext(context);
				result = operation.resolveModal(monitor);
				if (result.isOK()) {
					final ProvisioningJob uninstallJob = operation
							.getProvisioningJob(monitor);
					result = uninstallJob.runModal(monitor);
				}
			} else {
				result = authorizationError;
			}

			lock.unlock();
		} else {
			result = concurrentOperationError;
		}

		return result;
	}

	private void updateRepositories(ProvisioningContext context, URI[] locations) {
		context.setMetadataRepositories(locations);
		context.setArtifactRepositories(locations);
	}

	/*
	 * This method will create a serializable status and log the status message.
	 */
	private SerializableStatus createStatus(int severity, String message,
			Exception e) {
		LOGGER.info(message);
		if (e == null) {
			return new SerializableStatus(severity, "ID needed here", message);
		} else {
			return new SerializableStatus(severity, "ID needed here", message, e);
		}

	}

	// if (result == SWT.OK) {
	// return true;
	// // statusResult.add(createStatus(Status.OK, userName
	// // + " has accepted update", null));
	// } else {
	// // statusResult.add(createStatus(Status.CANCEL, userName
	// // + " cancelled update", null));
	// }

	public synchronized boolean acceptUpdate(ID fromId) {
		final boolean[] result = new boolean[1];

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				AcceptUpdateDialog restartDialog = new AcceptUpdateDialog();
				int dialogOpen = restartDialog.open();

				result[0] = dialogOpen == SWT.OK;

			}
		});

		return result[0];
	}
}
