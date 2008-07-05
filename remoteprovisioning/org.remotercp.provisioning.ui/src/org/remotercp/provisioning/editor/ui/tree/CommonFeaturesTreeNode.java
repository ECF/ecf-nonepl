package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;

public class CommonFeaturesTreeNode extends TreeNode implements
		Comparable<CommonFeaturesTreeNode> {

	boolean isVersionDifferent = false;

	private CommonFeaturesUserTreeNode[] children;

	public CommonFeaturesTreeNode(Object value) {
		super(value);
	}

	public boolean isVersionDifferent() {
		return isVersionDifferent;
	}

	public void setVersionDifferent(boolean isVersionDifferent) {
		this.isVersionDifferent = isVersionDifferent;
	}

	@Override
	public void setChildren(TreeNode[] children) {
		/*
		 * Set children in super constructro, otherwise has children will return
		 * false
		 */
		super.setChildren(children);

		this.children = new CommonFeaturesUserTreeNode[children.length];

		for (int child = 0; child < children.length; child++) {
			this.children[child] = (CommonFeaturesUserTreeNode) children[child];
		}
	}

	public void addChild(TreeNode child) {
		TreeNode[] children = super.getChildren();
		TreeNode[] newChildren = null;
		if (children != null) {
			newChildren = new TreeNode[children.length + 1];
			int i;
			for (i = 0; i < children.length; i++) {
				newChildren[i] = children[i];
			}
			newChildren[i] = child;
		} else {
			newChildren = new TreeNode[1];
			newChildren[0] = child;
		}

		setChildren(newChildren);
	}

	@Override
	public CommonFeaturesUserTreeNode[] getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CommonFeaturesTreeNode) {
			CommonFeaturesTreeNode node = (CommonFeaturesTreeNode) object;
			SerializedFeatureWrapper featureToCompare = (SerializedFeatureWrapper) node
					.getValue();

			SerializedFeatureWrapper thisFeature = (SerializedFeatureWrapper) this
					.getValue();

			if (thisFeature.getIdentifier().equals(
					featureToCompare.getIdentifier())) {
				return true;
			}
		}
		return false;
	}

	public int compareTo(CommonFeaturesTreeNode node) {
		SerializedFeatureWrapper thisFeature = (SerializedFeatureWrapper) getValue();
		SerializedFeatureWrapper featureToCompare = (SerializedFeatureWrapper) node
				.getValue();
		return thisFeature.getIdentifier().compareTo(
				featureToCompare.getIdentifier());
	}

}
