package org.remotercp.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.preferences.service.RemotePreferencesServiceImpl;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreferencesActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.preferences";

	// The shared instance
	private static PreferencesActivator plugin;

	private static BundleContext bundleContext;

	private ISessionService sessionService;

	/**
	 * The constructor
	 */
	public PreferencesActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundleContext = context;

		this.registerRemoteServices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		this.sessionService.ungetRemoteService(null,
				IRemotePreferenceService.class.getName(), null);
		super.stop(context);

	}

	protected void registerRemoteServices() {
		this.sessionService = OsgiServiceLocatorUtil.getOSGiService(
				bundleContext, ISessionService.class);
		Assert.isNotNull(sessionService);

		this.sessionService.registerRemoteService(
				IRemotePreferenceService.class.getName(),
				new RemotePreferencesServiceImpl(), null);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PreferencesActivator getDefault() {
		return plugin;
	}
	
	public static BundleContext getBundleContext(){
		return bundleContext;
	}

}
