package org.remotercp.provisioning.editor.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.viewers.TreeNode;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;

public class FeaturesTreeCreator {

	private final static Logger logger = Logger
			.getLogger(FeaturesTreeCreator.class.getName());

	private final Set<SerializedFeatureWrapper> allFeatures = new TreeSet<SerializedFeatureWrapper>();
	private final Map<ID, Collection<SerializedFeatureWrapper>> userFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();
	private final Set<SerializedFeatureWrapper> commonFeatures = new TreeSet<SerializedFeatureWrapper>();
	private final Set<SerializedFeatureWrapper> differentFeatures = new TreeSet<SerializedFeatureWrapper>();

	private Collection<CommonFeaturesTreeNode> commonFeaturesNodes;
	private Collection<DifferentFeaturesTreeNode> differentFeaturesNodes;

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

				logger.info("Remote installed bundles received");

				// store common features
				if (commonFeatures.isEmpty()) {
					// start with any collection
					commonFeatures.addAll(installedFeatures);

				} else {
					// get the intersection of bundles
					commonFeatures.retainAll(installedFeatures);
				}

				/*
				 * store the relationship between user and features
				 */
				userFeatures.put(userID, installedFeatures);
				allFeatures.addAll(installedFeatures);

				logger.info("Remote installed bundles received");

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

		/*
		 * Different features are determined as the difference between all
		 * featueres and common features
		 */
		allFeatures.removeAll(commonFeatures);
		differentFeatures.addAll(allFeatures);

		// create common tree nodes
		this.commonFeaturesNodes = this.createCommonFeaturesNodes(
				commonFeatures, userFeatures);

		// create different tree nodes
		this.differentFeaturesNodes = this.createDifferentFeaturesNodes(
				differentFeatures, userFeatures);

		// all tasks finished
		monitor.done();

		return errorMessages;
	}

	protected Collection<CommonFeaturesTreeNode> createCommonFeaturesNodes(
			Set<SerializedFeatureWrapper> commonFeatures,
			Map<ID, Collection<SerializedFeatureWrapper>> userFeatures) {
		final Collection<CommonFeaturesTreeNode> commonFeaturesNodes = new ArrayList<CommonFeaturesTreeNode>();

		for (SerializedFeatureWrapper feature : commonFeatures) {
			CommonFeaturesTreeNode node = new CommonFeaturesTreeNode(feature);
			Collection<CommonFeaturesUserTreeNode> children = new ArrayList<CommonFeaturesUserTreeNode>();

			for (ID userId : userFeatures.keySet()) {
				Collection<SerializedFeatureWrapper> userFeaturesCollection = userFeatures
						.get(userId);

				// has user feature installed? if not ignore
				if (userFeaturesCollection.contains(feature)) {
					/*
					 * get the relevant feature from users list. We do not take
					 * the feature from above because user's feature might have
					 * a different version
					 */
					SerializedFeatureWrapper userFeature = getFeatureFromCollection(
							feature, userFeaturesCollection);

					CommonFeaturesUserTreeNode child = new CommonFeaturesUserTreeNode(
							userFeature);
					child.setUserId(userId);
					child.setParent(node);

					/*
					 * check if user feature version is different from parent.
					 * If this is the case don't compare all children
					 */
					if (!feature.getVersion().equals(userFeature.getVersion())) {
						node.setVersionDifferent(true);
					} else {
						// compare also children with each other
						boolean versionDifferent = this
								.isFeatureVersionDifferent(children,
										userFeature);
						if (versionDifferent) {
							node.setVersionDifferent(true);
						}
					}

					children.add(child);
				}

			}
			node.setChildren((TreeNode[]) children
					.toArray(new TreeNode[children.size()]));

			commonFeaturesNodes.add(node);
		}
		return commonFeaturesNodes;
	}

	protected Collection<DifferentFeaturesTreeNode> createDifferentFeaturesNodes(
			Set<SerializedFeatureWrapper> differentFeatures,
			Map<ID, Collection<SerializedFeatureWrapper>> userFeatures) {
		final Collection<DifferentFeaturesTreeNode> differentFeaturesNodes = new ArrayList<DifferentFeaturesTreeNode>();

		for (SerializedFeatureWrapper feature : differentFeatures) {
			DifferentFeaturesTreeNode node = new DifferentFeaturesTreeNode(
					feature);
			Collection<DifferentFeaturesUserTreeNode> children = new ArrayList<DifferentFeaturesUserTreeNode>();

			for (ID userId : userFeatures.keySet()) {
				Collection<SerializedFeatureWrapper> userFeatureCollection = userFeatures
						.get(userId);

				DifferentFeaturesUserTreeNode child = new DifferentFeaturesUserTreeNode(
						userId);

				if (userFeatureCollection.contains(feature)) {
					SerializedFeatureWrapper userFeature = this
							.getFeatureFromCollection(feature,
									userFeatureCollection);
					// user has feature installed
					child.setFeature(userFeature);
					child.setParent(node);
				}
				children.add(child);
			}

			node.setChildren((TreeNode[]) children
					.toArray(new TreeNode[children.size()]));
			differentFeaturesNodes.add(node);
		}

		return differentFeaturesNodes;
	}

	protected SerializedFeatureWrapper getFeatureFromCollection(
			SerializedFeatureWrapper feature,
			Collection<SerializedFeatureWrapper> featuresCollection) {
		Iterator<SerializedFeatureWrapper> iter = featuresCollection.iterator();
		while (iter.hasNext()) {
			SerializedFeatureWrapper tempFeature = (SerializedFeatureWrapper) iter
					.next();
			if (feature.equals(tempFeature)) {
				return tempFeature;
			}
		}
		return null;
	}

	/*
	 * compare
	 */
	protected boolean isFeatureVersionDifferent(
			Collection<CommonFeaturesUserTreeNode> children,
			SerializedFeatureWrapper userFeature) {
		boolean versionDifferent = false;
		for (CommonFeaturesUserTreeNode node : children) {
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();
			if (!feature.getVersion().equals(userFeature.getVersion())) {
				versionDifferent = true;
				break;
			}
		}
		return versionDifferent;
	}

	public Collection<CommonFeaturesTreeNode> getCommonFeaturesNodes() {
		return this.commonFeaturesNodes;
	}

	public Collection<DifferentFeaturesTreeNode> getDifferentFeaturesNodes() {
		return this.differentFeaturesNodes;
	}

	private IStatus createStatus(int severity, String message, Exception e) {
		IStatus error = new Status(severity, ProvisioningActivator.PLUGIN_ID,
				message, e);
		return error;
	}

}
