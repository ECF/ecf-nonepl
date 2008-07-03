package org.remotercp.provisioning.editor.ui.tree;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

/**
 * The DecoratingLabelProvider allows to use columns in a TreeViewer. But to use
 * columns in a tree a TableLabelProvider must be specified.
 * 
 * @author Eugen Reiswich.
 * 
 */
public class FeaturesTreeLabelProvider extends DecoratingLabelProvider
		implements ITableLabelProvider {

	private final ITableLabelProvider provider;
	private final ILabelDecorator decorator;

	// private final Color blue = new Color(getDisplay(), 126, 192, 238);
	//
	// private final Color red = new Color(getDisplay(), 205, 92, 92);

	public FeaturesTreeLabelProvider(ILabelProvider provider,
			ILabelDecorator decorator) {
		super(provider, decorator);
		this.provider = (ITableLabelProvider) provider;
		this.decorator = decorator;
	}

	private Image userImage = ProvisioningActivator.getImageDescriptor(
			ImageKeys.GROUP).createImage();

	@Override
	public void dispose() {
		userImage.dispose();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = provider.getColumnImage(element, columnIndex);
		if (decorator != null) {
			Image decorated = decorator.decorateImage(image, element);
			if (decorated != null) {
				return decorated;
			}
		}
		return image;
	}

	public String getColumnText(Object element, int columnIndex) {
		String text = provider.getColumnText(element, columnIndex);
		if (decorator != null) {
			String decorated = decorator.decorateText(text, element);
			if (decorated != null) {
				return decorated;
			}
		}
		return text;
	}

	private Display getDisplay() {
		if (Display.getCurrent() != null) {
			return Display.getCurrent();
		} else {
			return Display.getDefault();
		}
	}

	// @Override
	// public Color getBackground(Object element) {
	// Color color = null;
	// if (element instanceof CommonFeaturesTreeNode) {
	// CommonFeaturesTreeNode commonNode = (CommonFeaturesTreeNode) element;
	// if (commonNode.isVersionDifferent) {
	// color = blue;
	// }
	// }
	//
	// if (element instanceof DifferentFeaturesUserTreeNode) {
	// DifferentFeaturesUserTreeNode differentNode =
	// (DifferentFeaturesUserTreeNode) element;
	// if (!differentNode.hasUserFeatureInstalled()) {
	// color = red;
	// }
	// }
	//
	// return color;
	//	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing

	}

	public boolean isLabelProperty(Object element, String property) {
		// do nothing
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing

	}

}
