package org.remotercp.provisioning.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

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
			List<IInstalledFeaturesService> serviceList, Class<Type> wrapperType) {

		for (final IInstalledFeaturesService service : serviceList) {

			try {

				Collection<Type> installedBundles = null;
				if (wrapperType.isAssignableFrom(SerializedBundleWrapper.class)) {
					installedBundles = (Collection<Type>) service
							.getInstalledBundles();

				}

				if (wrapperType
						.isAssignableFrom(SerializedFeatureWrapper.class)) {
					installedBundles = (Collection<Type>) service
							.getInstalledFeatures();
				}

				/*
				 * store the relationship between user and bundles
				 */
				ID userID = service.getUserID();
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

			} catch (Exception e) {
				IStatus error = new Status(
						IStatus.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"Unable to get installed bundles on the remote rpc application",
						e);
				ErrorView.addError(error);
			}
		}

		// the difference between all bundles and interception
		allArtifacts.removeAll(commonArtifacts);
		differentArtifacts.addAll(allArtifacts);

		differentArtifactsToUser = getRelationshipDifferentBundleToUser(
				userArtifacts, differentArtifacts);
	}

	/*
	 * In oder to see which user have one or more different bundles we have to
	 * map now bundles to user. The result should be like bundle
	 * org.eclipse.example is used by user John and Sandy but not by Peter.
	 */
	protected Map<Type, Collection<ID>> getRelationshipDifferentBundleToUser(
			Map<ID, Collection<Type>> userArtifacts, Set<Type> differentArtifacts) {
		Map<Type, Collection<ID>> differentArtifactsToUser = new HashMap<Type, Collection<ID>>();

		for (Type differentArtifact : differentArtifacts) {
			for (ID userID : userArtifacts.keySet()) {
				/*
				 * check whether user has a different bundle installed
				 */
				Collection<Type> userArtifactsCollection = userArtifacts.get(userID);

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

}
