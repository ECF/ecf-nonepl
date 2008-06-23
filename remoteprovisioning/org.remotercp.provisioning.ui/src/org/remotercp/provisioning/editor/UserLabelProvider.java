package org.remotercp.provisioning.editor;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class UserLabelProvider implements ITableLabelProvider {

	public static final int COLUMN_IMAGE = 0;

	public static final int COLUMN_USER = 1;

	public Image user = ProvisioningActivator
			.getImageDescriptor(ImageKeys.USER).createImage();

	public Image getColumnImage(Object element, int columnIndex) {

		switch (columnIndex) {
		case COLUMN_IMAGE:
			return user;
		default:
			break;
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case COLUMN_IMAGE:
			return ((ID) element).getName();
		default:
			return null;
		}
	}

	public void addListener(ILabelProviderListener listener) {

	}

	public void dispose() {
		this.user.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

}
