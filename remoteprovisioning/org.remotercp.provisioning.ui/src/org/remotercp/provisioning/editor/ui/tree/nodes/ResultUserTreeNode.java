package org.remotercp.provisioning.editor.ui.tree.nodes;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

public class ResultUserTreeNode extends AbstractTreeNode {

	private List<IStatus> updateResults;

	public ResultUserTreeNode(Object value) {
		super(value);
	}

	@Override
	public String getLabel() {
		return null;
	}

	public List<IStatus> getUpdateResults() {
		return updateResults;
	}

	public void setUpdateResults(List<IStatus> updateResults) {
		this.updateResults = updateResults;
	}

}
