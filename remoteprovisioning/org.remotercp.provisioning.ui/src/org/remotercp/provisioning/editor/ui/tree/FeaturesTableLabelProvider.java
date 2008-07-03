package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

/**
 * Label Provider for the TreeViewer. Allows to use columns in a TreeViewer.
 * 
 * @author eugrei
 * 
 */
public class FeaturesTableLabelProvider implements ITableLabelProvider,
		ILabelProvider {

	private final static int FEATURE_NAME = 0;

	private final static int FEATURE_VERSION = 1;

	// private Image plugin = ProvisioningActivator.getImageDescriptor(
	// ImageKeys.PLUGIN).createImage();

	private Image feature = ProvisioningActivator.getImageDescriptor(
			ImageKeys.FEATURE).createImage();

	private Image circle_red = ProvisioningActivator.getImageDescriptor(
			ImageKeys.CIRCLE_RED).createImage();

	private Image warn = ProvisioningActivator.getImageDescriptor(
			ImageKeys.WARN).createImage();

	private Image circle_blue = ProvisioningActivator.getImageDescriptor(
			ImageKeys.CIRCLE_BLUE).createImage();

	private Image circle_green = ProvisioningActivator.getImageDescriptor(
			ImageKeys.CIRCLE_GREEN).createImage();

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;

		if (element instanceof CommonFeaturesTreeNode
				|| element instanceof DifferentFeaturesTreeNode) {
			switch (columnIndex) {
			case FEATURE_NAME:
				image = feature;
				break;
			case FEATURE_VERSION:
				if (element instanceof CommonFeaturesTreeNode) {
					CommonFeaturesTreeNode node = (CommonFeaturesTreeNode) element;
					if (node.isVersionDifferent) {
						image = warn;
					}
				}
				break;
			default:
				return image;
			}
		}

		// mark user who have not a feature installed
		if (element instanceof DifferentFeaturesUserTreeNode) {
			DifferentFeaturesUserTreeNode node = (DifferentFeaturesUserTreeNode) element;
			switch (columnIndex) {
			case FEATURE_NAME:
				if (!node.hasUserFeatureInstalled()) {
					image = circle_red;
				} else {
					image = circle_green;
				}
				break;

			default:
				break;
			}
		}

		return image;

	}

	@SuppressWarnings("unchecked")
	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof CommonFeaturesTreeNode
				|| element instanceof DifferentFeaturesTreeNode) {
			if (columnIndex == FEATURE_NAME) {
				Object value = ((TreeNode) element).getValue();
				SerializedFeatureWrapper feature = (SerializedFeatureWrapper) value;
				return feature.getLabel();
			}
		}

		if (element instanceof CommonFeaturesUserTreeNode) {
			String text = null;
			CommonFeaturesUserTreeNode node = ((CommonFeaturesUserTreeNode) element);
			ID userId = node.getUserId();
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();
			switch (columnIndex) {
			case FEATURE_NAME:
				text = userId.getName();
				break;
			case FEATURE_VERSION:
				text = feature.getVersion();
				break;
			default:
				break;
			}
			return text;
		}

		if (element instanceof DifferentFeaturesUserTreeNode) {
			String text = null;
			DifferentFeaturesUserTreeNode node = (DifferentFeaturesUserTreeNode) element;
			ID userId = (ID) node.getValue();
			SerializedFeatureWrapper feature = node.getFeature();

			switch (columnIndex) {
			case FEATURE_NAME:
				text = userId.getName();
				break;
			case FEATURE_VERSION:
				if (node.hasUserFeatureInstalled())
					text = feature.getVersion();
			default:
				break;
			}
			return text;
		}

		return null;
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore

	}

	// free ressources
	public void dispose() {
		// this.plugin.dispose();
		this.feature.dispose();
		// this.circle_blue.dispose();
		// this.circle_green.dispose();
		// this.circle_red.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore

	}

	public Image getImage(Object element) {
		// do nothing
		return null;
	}

	public String getText(Object element) {
		// do nothing
		return null;
	}

}