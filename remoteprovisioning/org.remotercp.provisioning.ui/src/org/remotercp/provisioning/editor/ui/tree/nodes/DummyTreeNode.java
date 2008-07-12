package org.remotercp.provisioning.editor.ui.tree.nodes;

public class DummyTreeNode extends AbstractTreeNode {

	public static final int UPDATESITE = 0;
	public static final int CATEGORY = 1;
	public static final int FEATURE = 2;
	private int nodetype;
	private final String label;

	public DummyTreeNode(Object value, int nodetype, String label) {
		super(value);
		this.nodetype = nodetype;
		this.label = label;
	}

	public int getNodeType() {
		return nodetype;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
