package org.remotercp.ecf;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.ecf.session.impl.SessionServiceImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class ECFActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.ecf";
	private static BundleContext context;

	/**
	 * The constructor
	 */
	public ECFActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.registerServices(context);
	}

	private void registerServices(BundleContext context) {
		// Dictionary<String, Object> props = new Hashtable<String, Object>();
		// props.put(Constants.SERVICE_VENDOR, "org.eclipsercp");
		// props.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		//
		// context.registerService(ISessionService.class.getName(),
		// new SessionServiceImpl(), props);

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
		return context;
	}
}
