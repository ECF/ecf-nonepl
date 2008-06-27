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
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;

public class ArtifactsSetOperationHelper<Type> {

	private final static Logger logger = Logger
			.getLogger(ArtifactsSetOperationHelper.class.getName());

	private final Set<Type> commonArtifacts = new TreeSet<Type>();
	private final Set<Type> differentArtifacts = new TreeSet<Type>();
	private final Set<Type> allArtifacts = new TreeSet<Type>();
	
	private final Map<ID, Collection<Type>> userArtifacts = new HashMap<ID, Collection<Type>>();
	private Map<Type, Collection<ID>> differentArtifactsToUser;

	@SuppressWarnings("unchecked")
	public void handleInstalledArtifacts(
			List<IInstalledFeaturesService> serviceList,
			Class<Type> wrapperType, IProgressMonitor monitor) {

		Collection<IStatus> errorMessages = new ArrayList<IStatus>();

		for (final IInstalledFeaturesService service : serviceList) {
			try {
				ID userID = service.getUserID();

				Collection<Type> installedBundles = null;
				if (wrapperType.isAssignableFrom(SerializedBundleWrapper.class)) {
					monitor.subTask("Receiving bundles from user: "
							+ userID.getName());
					installedBundles = (Collection<Type>) service
							.getInstalledBundles();

					// report error
					if (installedBundles == null || installedBundles.isEmpty()) {
						errorMessages.add(this.createStatus(IStatus.WARNING,
								"No bundles received from user: "
										+ userID.getName(), null));

					}
				}

				if (wrapperType
						.isAssignableFrom(SerializedFeatureWrapper.class)) {
					monitor.subTask("Receiving features from user: "
							+ userID.getName());
					installedBundles = (Collection<Type>) service
							.getInstalledFeatures();

					// report error
					if (installedBundles == null || installedBundles.isEmpty()) {
						errorMessages.add(this.createStatus(IStatus.WARNING,
								"No features received from user: "
										+ userID.getName(), null));
					}
				}

				/*
				 * store the relationship between user and bundles
				 */

				userArtifacts.put(userID, installedBundles);
				allArtifacts.addAll(installedBundles);

				logger.info("Remote installed bundles received");

				if (commonArtifacts.isEmpty()) {
					// start with any collection
					commonArtifacts.addAll(installedBundles);

				} else {
					// get the intersection of bundles
					commonArtifacts.retainAll(installedBundles);
				}

				monitor.worked(1);
			} catch (Exception e) {
				errorMessages
						.add(this
								.createStatus(
										IStatus.ERROR,
										"Unable to get installed bundles on the remote rpc application",
										e));

			}
		}

		// the difference between all bundles and interception
		allArtifacts.removeAll(commonArtifacts);
		differentArtifacts.addAll(allArtifacts);

		differentArtifactsToUser = getRelationshipDifferentBundleToUser(
				userArtifacts, differentArtifacts);

		if (!errorMessages.isEmpty()) {
			ErrorView.addError(errorMessages);
		}

		// all tasks finished
		monitor.done();
	}

	/*
	 * In oder to see which user have one or more different bundles we have to
	 * map now bundles to user. The result should be like bundle
	 * org.eclipse.example is used by user John and Sandy but not by Peter.
	 */
	protected Map<Type, Collection<ID>> getRelationshipDifferentBundleToUser(
			Map<ID, Collection<Type>> userArtifacts,
			Set<Type> differentArtifacts) {
		Map<Type, Collection<ID>> differentArtifactsToUser = new HashMap<Type, Collection<ID>>();

		for (Type differentArtifact : differentArtifacts) {
			for (ID userID : userArtifacts.keySet()) {
				/*
				 * check whether user has a different bundle installed
				 */
				Collection<Type> userArtifactsCollection = userArtifacts
						.get(userID);

				if (userArtifactsCollection.contains(differentArtifact)) {
					/*
					 * Check if there is already a key for the given bundle. If
					 * this is the case add additional user ID
					 */
					if (differentArtifactsToUser.containsKey(differentArtifact)) {
						Collection<ID> collection = differentArtifactsToUser
								.get(differentArtifact);
						collection.add(userID);
					} else {
						// create new key and collection
						Collection<ID> user = new ArrayList<ID>();
						user.add(userID);
						differentArtifactsToUser.put(differentArtifact, user);
					}
				}
			}
		}

		return differentArtifactsToUser;
	}

	public Set<Type> getCommonArtifacts() {
		return this.commonArtifacts;
	}

	public Set<Type> getDifferentArtifacts() {
		return this.differentArtifacts;
	}

	public Map<Type, Collection<ID>> getDifferentArtifactToUser() {
		return this.differentArtifactsToUser;
	}

	private IStatus createStatus(int severity, String message, Exception e) {
		IStatus error = new Status(severity, ProvisioningActivator.PLUGIN_ID,
				message, e);
		return error;
	}

}
