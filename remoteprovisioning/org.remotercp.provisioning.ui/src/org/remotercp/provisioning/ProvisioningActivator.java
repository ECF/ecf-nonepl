package org.remotercp.provisioning;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class ProvisioningActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.provisioning.ui";

	// The shared instance
	private static ProvisioningActivator plugin;

	private ServiceTracker serviceTracker;

	private static BundleContext bundlecontext;

	/**
	 * The constructor
	 */
	public ProvisioningActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundlecontext = context;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (this.serviceTracker != null) {
			this.serviceTracker.close();
			this.serviceTracker = null;
		}

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ProvisioningActivator getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundlecontext;
	}

	public static ImageDescriptor getImageDescriptor(String imageFilePath) {
		ImageDescriptor imageDescriptor = imageDescriptorFromPlugin(PLUGIN_ID,
				imageFilePath);
		return imageDescriptor;

	}

}
