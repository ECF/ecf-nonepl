package org.remotercp.preferences;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreferencesActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.preferences";

	// The shared instance
	private static BundleContext bundleContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		bundleContext = context;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}
