package org.remotercp.provisioning.editor;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.osgi.framework.Bundle;

@Deprecated
public class BundleAdapterFactory implements IAdapterFactory {

	private IWorkbenchAdapter bundleTableAdapter = new IWorkbenchAdapter() {

		public Object[] getChildren(Object o) {
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public String getLabel(Object o) {
			if (o instanceof Bundle) {
				Bundle bundle = (Bundle) o;

				int status = bundle.getState();
				String installationStatus = null;
				switch (status) {
				case Bundle.INSTALLED:
					installationStatus = "Installed";
					break;
				case Bundle.ACTIVE:
					installationStatus = "Active";
					break;
				case Bundle.RESOLVED:
					installationStatus = "Resolved";
					break;
				case Bundle.UNINSTALLED:
					installationStatus = "Uninstalled";
					break;
				case Bundle.STARTING:
					installationStatus = "Starting";
					break;

				}

				return bundle.getSymbolicName() + "           -Status: "
						+ installationStatus;
			} else {
				return o.toString();
			}
		}

		public Object getParent(Object o) {
			return null;
		}

	};

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return bundleTableAdapter;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
