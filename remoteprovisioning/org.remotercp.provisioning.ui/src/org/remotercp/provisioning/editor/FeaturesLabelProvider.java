package org.remotercp.provisioning.editor;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.update.core.IFeature;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

/**
 * Label Provider for the installed features table
 * 
 * @author eugrei
 * 
 */
public class FeaturesLabelProvider implements ITableLabelProvider {

	private Image plugin = ProvisioningActivator.getImageDescriptor(
			ImageKeys.PLUGIN).createImage();

	private Image feature = ProvisioningActivator.getImageDescriptor(
			ImageKeys.FEATURE).createImage();

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;

		switch (columnIndex) {
		case 0:
			if (element instanceof IFeature) {
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
		if (element instanceof IFeature) {
			IFeature feature = (IFeature) element;
			return feature.getVersionedIdentifier().getIdentifier();
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