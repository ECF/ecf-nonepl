package org.remotercp.provisioning.editor.ui.tree.nodes;

import org.eclipse.core.runtime.IStatus;

public class ResultUserTreeNode extends AbstractTreeNode {

	private IStatus updateResults;

	public ResultUserTreeNode(Object value) {
		super(value);
	}

	@Override
	public String getLabel() {
		return null;
	}

	public IStatus getUpdateResult() {
		return updateResults;
	}

	public void setUpdateResult(IStatus updateResults) {
		this.updateResults = updateResults;
	}

}
