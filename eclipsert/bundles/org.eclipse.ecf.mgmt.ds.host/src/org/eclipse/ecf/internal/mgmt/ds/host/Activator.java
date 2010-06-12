package org.eclipse.ecf.internal.mgmt.ds.host;

import org.eclipse.core.runtime.IAdapterManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.ecf.mgmt.ds.host"; //$NON-NLS-1$

	private static Activator instance;

	private ServiceTracker adapterManagerTracker;
	private BundleContext context;

	public static Activator getDefault() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
	}

	public IAdapterManager getAdapterManager() {
		// First, try to get the adapter manager via
		if (adapterManagerTracker == null) {
			adapterManagerTracker = new ServiceTracker(this.context,
					IAdapterManager.class.getName(), null);
			adapterManagerTracker.open();
		}
		return (IAdapterManager) adapterManagerTracker.getService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (adapterManagerTracker != null) {
			adapterManagerTracker.close();
			adapterManagerTracker = null;
		}
		this.context = null;
		instance = null;
	}

}
