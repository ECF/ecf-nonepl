package org.remotercp.provisioning.editor;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

/**
 * Label Provider for the installed features table
 * 
 * @author eugrei
 * 
 */
public class ArtifactsLabelProvider implements ITableLabelProvider {

	private Image plugin = ProvisioningActivator.getImageDescriptor(
			ImageKeys.PLUGIN).createImage();

	private Image feature = ProvisioningActivator.getImageDescriptor(
			ImageKeys.FEATURE).createImage();

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;

		switch (columnIndex) {
		case 0:
			if (element instanceof SerializedFeatureWrapper) {
				image = feature;
			}
			if (element instanceof SerializedBundleWrapper) {
				image = plugin;
			}
		default:
			return image;
		}

	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof SerializedFeatureWrapper) {
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) element;

			switch (columnIndex) {
			case 0:
				return feature.getLabel();

			case 1:
				// TODO: return features version
				break;
			default:
				break;
			}

		}
		if (element instanceof SerializedBundleWrapper) {
			SerializedBundleWrapper bundle = (SerializedBundleWrapper) element;
			switch (columnIndex) {
			case 0:
				return bundle.getSymbolicName();
			case 1:
				return bundle.getBundleVersion();

			default:
				break;
			}
		}
		return element.toString();
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore

	}

	// free ressources
	public void dispose() {
		this.plugin.dispose();
		this.plugin = null;

		this.feature.dispose();
		this.feature = null;
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore

	}

}