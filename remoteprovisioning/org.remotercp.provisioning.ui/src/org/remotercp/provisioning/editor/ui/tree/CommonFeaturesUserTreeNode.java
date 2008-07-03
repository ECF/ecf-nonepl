package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.viewers.TreeNode;

public class CommonFeaturesUserTreeNode extends TreeNode {

	private ID userId;

	public CommonFeaturesUserTreeNode(Object value) {
		super(value);
	}

	public ID getUserId() {
		return userId;
	}

	public void setUserId(ID userId) {
		this.userId = userId;
	}

}
