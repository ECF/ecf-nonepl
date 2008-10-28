package org.remotercp.provisioning.install;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.IInstallConfiguration;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.internal.operations.IUnconfigureAndUninstallFeatureOperation;
import org.eclipse.update.internal.operations.OperationFactory;
import org.eclipse.update.operations.IInstallFeatureOperation;
import org.eclipse.update.operations.IOperation;
import org.eclipse.update.operations.OperationsManager;
import org.remotercp.common.authorization.IOperationAuthorization;
import org.remotercp.common.provisioning.IInstallFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.common.status.SerializableStatus;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.provisioning.UpdateActivator;
import org.remotercp.provisioning.dialogs.AcceptUpdateDialog;
import org.remotercp.util.authorization.ExtensionRegistryHelper;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.status.StatusUtil;

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
public class InstallFeaturesServiceImpl implements IInstallFeaturesService {

	private static final Logger LOGGER = Logger
			.getLogger(InstallFeaturesServiceImpl.class.getName());

	private final ReentrantLock lock = new ReentrantLock(true);

	/**
	 * This method prepares the {@link IInstallFeatureOperation} and calls the
	 * execution of the installation process.
	 * 
	 * @param features
	 *            Features to install
	 * @return The result of the installation process collected in a List of
	 *         {@link IStatus} objects.
	 */
	private List<IStatus> installFeatures(IFeature[] features) {
		List<IStatus> statusCollector = new ArrayList<IStatus>();
		List<IInstallFeatureOperation> installOperations = new ArrayList<IInstallFeatureOperation>();

		if (!lock.isLocked()) {
			try {
				lock.lock();

				/*
				 * create install operations first.
				 */
				for (IFeature feature : features) {

					/*
					 * TODO: pay attention to the last three parameters and
					 * implement the necessary methods
					 */
					IConfiguredSite configuredSite = getLocalConfiguredSiteForFeature(feature
							.getVersionedIdentifier().getIdentifier());

					IInstallFeatureOperation installOperation = OperationsManager
							.getOperationFactory().createInstallOperation(
									configuredSite, feature, null, null, null);
					installOperations.add(installOperation);

				}

				this.executeInstallOperations(statusCollector,
						installOperations);

			} catch (CoreException e) {
				IStatus status = createStatus(Status.ERROR,
						"Unable to retrieve the IConfigureSite", e);
				statusCollector.add(status);
			} finally {
				lock.unlock();
			}

		} else {
			IStatus status = createStatus(
					Status.ERROR,
					"Concurrent operation occured while trying to install features",
					null);
			statusCollector.add(status);

		}
		return statusCollector;
	}

	/**
	 * This method performs the installation for the given install commands and
	 * records errors occured during the install operations.
	 * 
	 * In addition to that this method calls the uninstall operation, if an old
	 * feature exists for a given new feature to install.
	 * 
	 * @param statusCollector
	 *            List to collect status information e.g. errors or ok status.
	 *            The {@link MultiStatus} object is not used as it is not
	 *            Serializable.
	 * @param installOperations
	 *            List of install operations to execute
	 */
	protected void executeInstallOperations(
			final List<IStatus> statusCollector,
			final List<IInstallFeatureOperation> installOperations) {

		Job installFeatureJob = new Job("Install Features...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(
						"Download and install features from remote site",
						installOperations.size());

				/*
				 * TODO: replace this with
				 * OperationsManager.getOperationFactory(
				 * ).createBatchInstallOperation[]
				 */
				for (IInstallFeatureOperation installOperation : installOperations) {
					IFeature feature = installOperation.getFeature();
					monitor
							.subTask("Installing feature: "
									+ feature.getLabel());

					LOGGER.info("Installing: " + feature.getLabel());
					try {
						boolean success = installOperation.execute(monitor,
								null);
						if (success) {
							IStatus installOk = createStatus(Status.OK,
									"Feature " + feature.getLabel()
											+ " was successfully installed",
									null);
							statusCollector.add(installOk);

							IFeature oldFeature = installOperation
									.getOldFeature();
							if (oldFeature != null) {
								IFeature[] uninstallFeature = new IFeature[1];
								uninstallFeature[0] = oldFeature;
								monitor.subTask("Uninstalling old feature: "
										+ oldFeature.getLabel());
								List<IStatus> uninstallStatus = uninstallFeatures(uninstallFeature);

								int checkStatus = StatusUtil
										.checkStatus(uninstallStatus);

								if (checkStatus == Status.OK) {
									IStatus statusOK = createStatus(
											Status.OK,
											"Feature: "
													+ oldFeature.getLabel()
													+ " was succesfully uninstalled",
											null);
									statusCollector.add(statusOK);
								} else {
									IStatus warningStatus = createStatus(
											Status.WARNING,
											"Feature "
													+ feature.getLabel()
													+ " was installed, but old feature could not be uninstalled",
											null);
									statusCollector.add(warningStatus);
								}
							}
						} else {
							IStatus statusOK = createStatus(Status.OK,
									"Feature: " + feature.getLabel()
											+ " was installed", null);
							statusCollector.add(statusOK);
						}

					} catch (CoreException e) {
						IStatus error = createStatus(Status.ERROR,
								"Unable to install feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
					} catch (InvocationTargetException e) {
						IStatus error = createStatus(Status.ERROR,
								"Unable to install feature: "
										+ feature.getLabel(), e);
						;
						statusCollector.add(error);
					}
					monitor.worked(1);
				}
				monitor.done();

				return Status.OK_STATUS;
			}
		};
		installFeatureJob.setUser(true);
		installFeatureJob.schedule();

	}

	/*
	 * This method performs the uninstall operation for the given features.
	 */
	@SuppressWarnings("restriction")
	private List<IStatus> uninstallFeatures(IFeature[] features) {
		/*
		 * if uninstall was successfull this list will contain only one OK
		 * status, else errors are stored here
		 */
		List<IStatus> statusCollector = new ArrayList<IStatus>();
		List<IOperation> unconfigAndUninstallOperations = new ArrayList<IOperation>();
		Map<IConfiguredSite, IFeature> featuresToDeleteFromDisk = new HashMap<IConfiguredSite, IFeature>();
		if (!lock.isLocked()) {
			try {
				lock.lock();

				// create first uninstall operations
				for (IFeature feature : features) {
					try {
						IConfiguredSite configuredSite = getLocalConfiguredSiteForFeature(feature
								.getVersionedIdentifier().getIdentifier());

						featuresToDeleteFromDisk.put(configuredSite, feature);

						IOperation unconfigAndUninstallOperation = ((OperationFactory) OperationsManager
								.getOperationFactory())
								.createUnconfigureAndUninstallFeatureOperation(
										configuredSite, feature);
						unconfigAndUninstallOperations
								.add(unconfigAndUninstallOperation);

					} catch (CoreException e) {
						IStatus error = createStatus(Status.ERROR,
								"Failed to retrieve configured site for feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
					}
				}

				// execute uninstall operations
				for (IOperation unconfigAndUninstallOperation : unconfigAndUninstallOperations) {
					IFeature feature = ((IUnconfigureAndUninstallFeatureOperation) unconfigAndUninstallOperation)
							.getFeature();

					try {
						unconfigAndUninstallOperation.execute(null, null);
					} catch (CoreException e) {
						IStatus error = createStatus(Status.ERROR,
								"Failed to uninstall feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						IStatus error = createStatus(Status.ERROR,
								"Failed to uninstall feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
						e.printStackTrace();
					}
				}

				// remove files from disk
				new DeleteFeaturesOperation(featuresToDeleteFromDisk).run();

				int uninstallOK = StatusUtil.checkStatus(statusCollector);
				if (uninstallOK == Status.OK) {
					IStatus okStatus = createStatus(Status.OK,
							"Features have been successfully uninstalled", null);
					statusCollector.add(okStatus);
				}

			} finally {
				lock.unlock();
			}

		} else {
			IStatus concurrentError = createStatus(
					Status.ERROR,
					"Concurrent operation error occured while trying to uninstall features",
					null);
			statusCollector.add(concurrentError);
		}
		return statusCollector;
	}

	/**
	 * As the IFeature object is not serializable the Admin will get a
	 * SerializedFeatureWrapper Object which only contains the featureId,
	 * version, label and URL to perform uninstall operations. In order to allow
	 * admin to directly uninstall Features one has first to find the
	 * corresponding locally installed IFeature objects.
	 * 
	 * @param Feature
	 *            Ids retrieved from
	 *            Feature.getVersionedIdentifier().getIdentifier();
	 * @return A list of {@link IStatus} objects which contains the result of
	 *         the uninstall operation e.g. errors, warnings and ok status
	 */
	public List<IStatus> uninstallFeatures(String[] featuresIds, ID fromId) {
		// is user fromId allowed to execute this operation?
		boolean canExecute = this.checkAuthorization(fromId,
				"uninstallFeatures");

		List<IStatus> statusCollector = new ArrayList<IStatus>();
		Set<IFeature> correspondingFeatures = new HashSet<IFeature>();
		if (canExecute) {

			List<String> featureStringIds = Arrays.asList(featuresIds);

			/* get local installed features */
			try {
				IFeatureReference[] featureReferences = getFeatureReferences();

				for (IFeatureReference ref : featureReferences) {
					IFeature feature = ref.getFeature(null);
					String featureId = feature.getVersionedIdentifier()
							.getIdentifier();

					// features found?
					if (featureStringIds.contains(featureId)) {
						correspondingFeatures.add(feature);
					}
				}

				// perform update
				if (!correspondingFeatures.isEmpty()) {
					IFeature[] featuresToUninstall = (IFeature[]) correspondingFeatures
							.toArray(new IFeature[correspondingFeatures.size()]);
					List<IStatus> updateStatus = uninstallFeatures(featuresToUninstall);

					int uninstallOK = StatusUtil.checkStatus(updateStatus);
					if (uninstallOK == Status.OK) {
						IStatus okStatus = createStatus(Status.OK,
								"Features have been uninstalled", null);
						statusCollector.add(okStatus);
					}
				}

			} catch (CoreException e) {
				IStatus error = createStatus(Status.ERROR,
						"Unable to retrieve the ISite configuration", e);
				statusCollector.add(error);
			}

		} else {
			IStatus authorizationFailed = createStatus(Status.ERROR,
					"Authorization failed.", null);
			statusCollector.add(authorizationFailed);
		}

		return statusCollector;
	}

	/**
	 * Returns feature references for all local configured sites.
	 * 
	 * @return
	 * @throws CoreException
	 */
	private IFeatureReference[] getFeatureReferences() throws CoreException {
		List<IFeatureReference> featureReferences = new ArrayList<IFeatureReference>();
		IConfiguredSite[] configuredSites = getConfiguration()
				.getConfiguredSites();

		for (IConfiguredSite site : configuredSites) {
			IFeatureReference[] featureRef = site.getFeatureReferences();
			if (featureRef != null) {
				for (IFeatureReference ref : featureRef) {
					featureReferences.add(ref);
				}
			}
		}

		return featureReferences
				.toArray(new IFeatureReference[featureReferences.size()]);
	}

	private IInstallConfiguration getConfiguration() throws CoreException {
		IInstallConfiguration currentConfiguration = SiteManager.getLocalSite()
				.getCurrentConfiguration();
		return currentConfiguration;
	}

	/**
	 * This method can be called if all installs/updates/uninstalls have been
	 * finished in order to apply changes to the running application.
	 */
	public void restartApplication(ID fromId) {
		// is user fromId allowed to execute this operation?
		boolean canExecute = this.checkAuthorization(fromId,
				"restartApplication");

		if (canExecute) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					PlatformUI.getWorkbench().restart();
				}
			});
		}

	}

	/**
	 * This method can be called to ask the client if updates can be performed
	 * now. The client will get an dialog with a count down where he/she can
	 * choose to perform updates now or cancel.
	 * 
	 * @return The status whether client has accepted (Status.OK) the update or
	 *         cancelled (Status.CANCELLED)
	 */
	public IStatus acceptUpdate(ID fromId) {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		final String userName = sessionService.getConnectionDetails()
				.getUserName();
		/*
		 * XXX: this list is a workaround for syncExec-Operation. As we are only
		 * able to pass final parameters to the run() method there is no other
		 * way? to apply status-information dynamically.
		 */
		final List<IStatus> statusCollector = new ArrayList<IStatus>();

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				AcceptUpdateDialog restartDialog = new AcceptUpdateDialog();
				int result = restartDialog.open();
				if (result == SWT.OK) {
					IStatus status = createStatus(Status.OK, userName
							+ " has accepted update", null);
					statusCollector.add(status);
				} else {
					IStatus status = createStatus(Status.CANCEL, userName
							+ " cancelled update", null);
					statusCollector.add(status);
				}
			}
		});

		return statusCollector.get(0);
	}

	/**
	 * This method will install the provided features in user's RCP application.
	 */
	public List<IStatus> installFeatures(SerializedFeatureWrapper[] features,
			ID fromId) {
		List<IStatus> statusCollector = new ArrayList<IStatus>();
		List<IFeature> featuresToUpdate = new ArrayList<IFeature>();

		// is user fromId allowed to execute this operation?
		boolean canExecute = this.checkAuthorization(fromId, "installFeatures");
		if (canExecute) {

			try {
				for (SerializedFeatureWrapper serializedFeature : features) {
					ISite site = SiteManager.getSite(serializedFeature
							.getUpdateUrl(), null);

					ISiteFeatureReference[] featureReferences = site
							.getFeatureReferences();

					for (ISiteFeatureReference featureReference : featureReferences) {
						IFeature feature = featureReference.getFeature(null);
						String featureId = feature.getVersionedIdentifier()
								.getIdentifier();
						String featureVersion = feature
								.getVersionedIdentifier().getVersion()
								.toString();
						if (featureId.equals(serializedFeature.getIdentifier())) {
							// right feature for update found. now get the right
							// version
							if (featureVersion.equals(serializedFeature
									.getVersion())) {
								featuresToUpdate.add(feature);
							}
						}
					}
				}

				// now perform the update
				IFeature[] featuresReadyForUpdate = featuresToUpdate
						.toArray(new IFeature[featuresToUpdate.size()]);

				List<IStatus> installResult = this
						.installFeatures(featuresReadyForUpdate);
				statusCollector.addAll(installResult);
			} catch (CoreException e) {
				IStatus error = createStatus(
						Status.ERROR,
						"Unable to retrieve the ISite configuration while trying to install features.",
						e);
				statusCollector.add(error);
			}
		} else {
			IStatus authorizationFailed = createStatus(Status.ERROR,
					"Authorization failed.", null);
			statusCollector.add(authorizationFailed);
		}

		return statusCollector;
	}

	/**
	 * TODO: check if a feature can be configured in more than one site!!!
	 * 
	 * Get the local installed Feature for a given featureId.
	 * 
	 * @param featureId
	 *            The id of the feature which local respresentative should be
	 *            found.
	 * @return The local installed feature for the given feature id.
	 * @throws CoreException
	 */
	private IConfiguredSite getLocalConfiguredSiteForFeature(String featureId)
			throws CoreException {
		IConfiguredSite returnSite = null;

		IConfiguredSite[] configuredSites = getConfiguration()
				.getConfiguredSites();
		for (IConfiguredSite site : configuredSites) {
			IFeatureReference[] featureReferences = site.getFeatureReferences();
			for (IFeatureReference featureReference : featureReferences) {
				IFeature feature = featureReference.getFeature(null);
				if (feature.getVersionedIdentifier().getIdentifier().equals(
						featureId)) {
					returnSite = site;
					break;
				}
			}
		}
		if (returnSite == null) {
			// feature is not configured now == new feature
			returnSite = configuredSites[0];
		}
		return returnSite;
	}

	/**
	 * An update is a mix of uninstall old feature and install the new feature.
	 * Therefore we can call installFeatures method.
	 */
	public List<IStatus> updateFeautures(SerializedFeatureWrapper[] features,
			ID fromId) {
		List<IStatus> installResult = new ArrayList<IStatus>();
		// is user fromId allowed to execute this operation?
		boolean canExecute = this.checkAuthorization(fromId, "updateFeatures");
		if (canExecute) {
			installResult = this.installFeatures(features, fromId);
		} else {
			IStatus authorizationFailed = createStatus(Status.ERROR,
					"Authorization failed.", null);
			installResult.add(authorizationFailed);
		}

		return installResult;
	}

	/*
	 * This method will create a serializable status and log the status message.
	 */
	private IStatus createStatus(int severity, String message, Exception e) {
		LOGGER.info(message);

		if (e == null) {
			return new SerializableStatus(severity, UpdateActivator.PLUGIN_ID,
					message);
		} else {
			return new SerializableStatus(severity, UpdateActivator.PLUGIN_ID,
					message, e);
		}

	}

	private boolean checkAuthorization(ID fromId, String methodId) {
		boolean authorized = false;
		try {
			List<Object> executablesForExtensionPoint = ExtensionRegistryHelper
					.getExecutablesForExtensionPoint("org.remotercp.authorization");
			if (executablesForExtensionPoint.isEmpty()) {
				/*
				 * no extension provided, ignore authorization
				 */
				authorized = true;
			} else {
				// authorization provided, check authorization
				for (Object executable : executablesForExtensionPoint) {
					if (executable instanceof IOperationAuthorization) {
						IOperationAuthorization operation = (IOperationAuthorization) executable;
						authorized = operation.canExecute(fromId, methodId);
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return authorized;
	}

	/***************************************************************************
	 * This method does register the remote InstallFeatureService as a remote
	 * operation.
	 */
	public void startServices() {
		LOGGER.info("******* Starting service: "
				+ InstallFeaturesServiceImpl.class.getName() + " *******");

		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		sessionService.registerRemoteService(IInstallFeaturesService.class
				.getName(), new InstallFeaturesServiceImpl(), null);
	}
}
