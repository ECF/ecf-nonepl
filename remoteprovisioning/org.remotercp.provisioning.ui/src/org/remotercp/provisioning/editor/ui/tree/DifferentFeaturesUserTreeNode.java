package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;

/**
 * As not every user has all features installed the value of this class is the
 * User ID. For the users who have this feature installed the method
 * getFeature() will not return null.
 * 
 * @author eugrei
 * 
 */
public class DifferentFeaturesUserTreeNode extends TreeNode {

	private SerializedFeatureWrapper feature;

	public DifferentFeaturesUserTreeNode(Object value) {
		super(value);
	}

	public SerializedFeatureWrapper getFeature() {
		return feature;
	}

	public void setFeature(SerializedFeatureWrapper feature) {
		this.feature = feature;
	}

	public boolean hasUserFeatureInstalled() {
		return feature == null ? false : true;
	}

}
