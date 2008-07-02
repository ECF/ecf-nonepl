package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;

public class CommonFeaturesTreeNode extends TreeNode {

	boolean isVersionDifferent = false;

	public CommonFeaturesTreeNode(Object value) {
		super(value);
	}

	public boolean isVersionDifferent() {
		return isVersionDifferent;
	}

	public void setVersionDifferent(boolean isVersionDifferent) {
		this.isVersionDifferent = isVersionDifferent;
	}

}
