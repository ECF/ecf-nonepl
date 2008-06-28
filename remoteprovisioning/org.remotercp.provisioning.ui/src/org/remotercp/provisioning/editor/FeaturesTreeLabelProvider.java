package org.remotercp.provisioning.editor;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class FeaturesTreeLabelProvider extends LabelProvider {

	private Image userImage = ProvisioningActivator.getImageDescriptor(
			ImageKeys.GROUP).createImage();

	@Override
	public String getText(Object element) {
		TreeNode node = (TreeNode) element;

		if (node.getValue() instanceof SerializedFeatureWrapper) {
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();
			return feature.getLabel();
		}

		if (node.getValue() instanceof ID) {
			ID userId = (ID) node.getValue();
			return userId.getName();
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		TreeNode node = (TreeNode) element;
		if (node.getValue() instanceof ID) {
			return userImage;
		}
		return null;
	}

	@Override
	public void dispose() {
		userImage.dispose();
	}

}
