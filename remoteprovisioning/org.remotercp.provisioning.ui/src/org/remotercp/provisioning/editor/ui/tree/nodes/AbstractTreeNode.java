package org.remotercp.provisioning.editor.ui.tree.nodes;

import org.eclipse.jface.viewers.TreeNode;

public abstract class AbstractTreeNode extends TreeNode {

	public AbstractTreeNode(Object value) {
		super(value);
	}

	public abstract String getLabel();

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

		super.setChildren(newChildren);
	}
}
