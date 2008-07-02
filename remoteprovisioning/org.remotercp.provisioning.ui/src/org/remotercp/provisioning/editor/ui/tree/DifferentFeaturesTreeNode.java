package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.TreeNode;

public class DifferentFeaturesTreeNode extends TreeNode {
	
	boolean isFeatureInstalled = true;

	public DifferentFeaturesTreeNode(Object value) {
		super(value);
	}

	public boolean isFeatureInstalled() {
		return isFeatureInstalled;
	}

	public void setFeatureInstalled(boolean isFeatureInstalled) {
		this.isFeatureInstalled = isFeatureInstalled;
	}
	
	

}
