package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;

public class CommonFeaturesTreeNode extends TreeNode {

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

	@Override
	public CommonFeaturesUserTreeNode[] getChildren() {
		return children;
	}

}
