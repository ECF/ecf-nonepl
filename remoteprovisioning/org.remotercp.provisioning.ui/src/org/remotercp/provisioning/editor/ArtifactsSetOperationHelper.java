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

	private final Set<Type> commonBundles = new TreeSet<Type>();
	private final Set<Type> differentBundles = new TreeSet<Type>();
	private final Set<Type> allBundles = new TreeSet<Type>();
	private final Map<ID, Collection<Type>> userBundles = new HashMap<ID, Collection<Type>>();
	private Map<Type, Collection<ID>> differentBundleToUser;

	@SuppressWarnings("unchecked")
	public void handleInstalledArtifacts(
			List<IInstalledFeaturesService> serviceList, Class<Type> wrapperType) {

		for (final IInstalledFeaturesService featureService : serviceList) {

			try {

				Collection<Type> installedBundles = null;
				if (wrapperType.isAssignableFrom(SerializedBundleWrapper.class)) {
					installedBundles = (Collection<Type>) featureService
							.getInstalledBundles();

				}

				if (wrapperType
						.isAssignableFrom(SerializedFeatureWrapper.class)) {
					installedBundles = (Collection<Type>) featureService
							.getInstalledFeatures();
				}

				/*
				 * store the relationship between user and bundles
				 */
				ID userID = featureService.getUserID();
				userBundles.put(userID, installedBundles);
				allBundles.addAll(installedBundles);

				logger.info("Remote installed bundles received");

				if (commonBundles.isEmpty()) {
					// start with any collection
					commonBundles.addAll(installedBundles);

				} else {
					// get the intersection of bundles
					commonBundles.retainAll(installedBundles);
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
		allBundles.removeAll(commonBundles);
		differentBundles.addAll(allBundles);

		differentBundleToUser = getRelationshipDifferentBundleToUser(
				userBundles, differentBundles);
	}

	/*
	 * In oder to see which user have one or more different bundles we have to
	 * map now bundles to user. The result should be like bundle
	 * org.eclipse.example is used by user John and Sandy but not by Peter.
	 */
	protected Map<Type, Collection<ID>> getRelationshipDifferentBundleToUser(
			Map<ID, Collection<Type>> userBundles, Set<Type> differentBundles) {
		Map<Type, Collection<ID>> differentBundleToUser = new HashMap<Type, Collection<ID>>();

		for (Type differentBundle : differentBundles) {
			for (ID userID : userBundles.keySet()) {
				/*
				 * check whether user has a different bundle installed
				 */
				Collection<Type> userBundleCollection = userBundles.get(userID);

				if (userBundleCollection.contains(differentBundle)) {
					/*
					 * Check if there is already a key for the given bundle. If
					 * this is the case add additional user ID
					 */
					if (differentBundleToUser.containsKey(differentBundle)) {
						Collection<ID> collection = differentBundleToUser
								.get(differentBundle);
						collection.add(userID);
					} else {
						// create new key and collection
						Collection<ID> user = new ArrayList<ID>();
						user.add(userID);
						differentBundleToUser.put(differentBundle, user);
					}
				}
			}
		}

		return differentBundleToUser;
	}

	public Set<Type> getCommonArtifacts() {
		return this.commonBundles;
	}

	public Set<Type> getDifferentArtifacts() {
		return this.differentBundles;
	}

	public Map<Type, Collection<ID>> getDifferentArtifactToUser() {
		return this.differentBundleToUser;
	}

}
