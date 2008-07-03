package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;

public class DifferentFeaturesTreeNode extends TreeNode {

	private DifferentFeaturesUserTreeNode[] children;

	public DifferentFeaturesTreeNode(Object value) {
		super(value);
	}

	@Override
	public DifferentFeaturesUserTreeNode[] getChildren() {
		return children;
	}

	@Override
	public void setChildren(TreeNode[] children) {
		/*
		 * Set children in super constructro, otherwise has children will return
		 * false
		 */
		super.setChildren(children);
		this.children = new DifferentFeaturesUserTreeNode[children.length];

		for (int child = 0; child < children.length; child++) {
			this.children[child] = (DifferentFeaturesUserTreeNode) children[child];
		}
	}
}
