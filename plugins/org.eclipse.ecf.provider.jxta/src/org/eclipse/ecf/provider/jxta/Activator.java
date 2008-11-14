package org.eclipse.ecf.provider.jxta;

import net.jxta.ext.example.presence.MyNetwork;
import net.jxta.ext.example.presence.MyServices;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	private MyNetwork network;
	private MyServices service;
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.jxta";

    private String id = System.getProperty("user.name") + "-" + System.currentTimeMillis();

    // The shared instance
	private static Activator plugin;
	
	public String getId() {
		return id;
	}

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void setStatus(final String id, final String statusId) {
		
	    if (service != null) {
	        service.setStatus(id, statusId);
	    }        
	}

}
