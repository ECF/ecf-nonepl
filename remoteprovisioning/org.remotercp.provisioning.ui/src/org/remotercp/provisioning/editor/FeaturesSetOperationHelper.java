package org.remotercp.provisioning.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;

public class FeaturesSetOperationHelper {

	private final static Logger logger = Logger
			.getLogger(FeaturesSetOperationHelper.class.getName());

	private final Set<SerializedFeatureWrapper> commonFeatures = new TreeSet<SerializedFeatureWrapper>();
	private final Set<SerializedFeatureWrapper> differentFeatures = new TreeSet<SerializedFeatureWrapper>();
	private final Set<SerializedFeatureWrapper> allFeatures = new TreeSet<SerializedFeatureWrapper>();

	private final Map<ID, Collection<SerializedFeatureWrapper>> userFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();
	private Map<SerializedFeatureWrapper, Collection<ID>> differentFeaturesToUser;

	@SuppressWarnings("unchecked")
	public Collection<IStatus> handleInstalledFeatures(
			List<IInstalledFeaturesService> serviceList,
			IProgressMonitor monitor) {

		Collection<IStatus> errorMessages = new ArrayList<IStatus>();

		for (final IInstalledFeaturesService service : serviceList) {

			try {
				ID userID = service.getUserID();

				monitor.subTask("Receiving features from user: "
						+ userID.getName());
				Collection<SerializedFeatureWrapper> installedFeatures = (Collection<SerializedFeatureWrapper>) service
						.getInstalledFeatures();

				// report error
				if (installedFeatures == null || installedFeatures.isEmpty()) {
					errorMessages.add(this.createStatus(IStatus.WARNING,
							"No features received from user: "
									+ userID.getName(), null));
				}

				/*
				 * store the relationship between user and features
				 */
				userFeatures.put(userID, installedFeatures);
				allFeatures.addAll(installedFeatures);

				logger.info("Remote installed bundles received");

				if (commonFeatures.isEmpty()) {
					// start with any collection
					commonFeatures.addAll(installedFeatures);

				} else {
					// get the intersection of bundles
					commonFeatures.retainAll(installedFeatures);
				}

				monitor.worked(1);
			} catch (Exception e) {
				errorMessages
						.add(this
								.createStatus(
										IStatus.ERROR,
										"Unable to get installed features on the remote rpc application",
										e));

			}
		}

		// the difference between all bundles and interception
		allFeatures.removeAll(commonFeatures);
		differentFeatures.addAll(allFeatures);

		differentFeaturesToUser = getRelationshipDifferentBundleToUser(
				userFeatures, differentFeatures);

		// all tasks finished
		monitor.done();

		return errorMessages;
	}

	/*
	 * In oder to see which user have one or more different bundles we have to
	 * map now bundles to user. The result should be like bundle
	 * org.eclipse.example is used by user John and Sandy but not by Peter.
	 */
	protected Map<SerializedFeatureWrapper, Collection<ID>> getRelationshipDifferentBundleToUser(
			Map<ID, Collection<SerializedFeatureWrapper>> userFeatures,
			Set<SerializedFeatureWrapper> differentFeatures) {
		Map<SerializedFeatureWrapper, Collection<ID>> differentFeaturesToUser = new HashMap<SerializedFeatureWrapper, Collection<ID>>();

		for (SerializedFeatureWrapper differentFeature : differentFeatures) {
			for (ID userID : userFeatures.keySet()) {
				/*
				 * check whether user has a different bundle installed
				 */
				Collection<SerializedFeatureWrapper> userFeaturesCollection = userFeatures
						.get(userID);

				if (userFeaturesCollection.contains(differentFeature)) {
					/*
					 * Check if there is already a key for the given bundle. If
					 * this is the case add additional user ID
					 */
					if (differentFeaturesToUser.containsKey(differentFeature)) {
						Collection<ID> collection = differentFeaturesToUser
								.get(differentFeature);
						collection.add(userID);
					} else {
						// create new key and collection
						Collection<ID> user = new ArrayList<ID>();
						user.add(userID);
						differentFeaturesToUser.put(differentFeature, user);
					}
				}
			}
		}

		return differentFeaturesToUser;
	}

	public Set<SerializedFeatureWrapper> getCommonArtifacts() {
		return this.commonFeatures;
	}

	public Set<SerializedFeatureWrapper> getDifferentArtifacts() {
		return this.differentFeatures;
	}

	public Map<SerializedFeatureWrapper, Collection<ID>> getDifferentArtifactToUser() {
		return this.differentFeaturesToUser;
	}

	private IStatus createStatus(int severity, String message, Exception e) {
		IStatus error = new Status(severity, ProvisioningActivator.PLUGIN_ID,
				message, e);
		return error;
	}

}
