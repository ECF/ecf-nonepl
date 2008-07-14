package org.remotercp.provisioning.editor.ui.tree.nodes;

import org.eclipse.update.core.IFeature;

public class ResultFeatureTreeNode extends AbstractTreeNode {

	public ResultFeatureTreeNode(Object value) {
		super(value);
	}

	@Override
	public String getLabel() {
		IFeature feature = (IFeature) getValue();
		String featureLabel = feature.getLabel();
		String featureVersion = feature.getVersionedIdentifier().getVersion()
				.toString();
		return featureLabel + " " + featureVersion;
	}

}
