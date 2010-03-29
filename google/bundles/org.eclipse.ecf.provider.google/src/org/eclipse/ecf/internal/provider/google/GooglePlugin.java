/*******************************************************************************
/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.PlatformHelper;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class GooglePlugin implements BundleActivator {
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.google"; //$NON-NLS-1$
	protected static final String NAMESPACE_IDENTIFIER = "ecf.google"; //$NON-NLS-1$
	// The shared instance.
	private static GooglePlugin plugin;

	private BundleContext context = null;

	private ServiceTracker logServiceTracker = null;

	private Map services;

	private ServiceTracker adapterManagerTracker = null;

	public static void log(String message) {
		getDefault().log(
				new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, null));
	}

	public static void log(String message, Throwable e) {
		getDefault().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e));
	}

	public IAdapterManager getAdapterManager() {
		// First, try to get the adapter manager via
		if (adapterManagerTracker == null) {
			adapterManagerTracker = new ServiceTracker(this.context,
					IAdapterManager.class.getName(), null);
			adapterManagerTracker.open();
		}
		IAdapterManager adapterManager = (IAdapterManager) adapterManagerTracker
				.getService();
		// Then, if the service isn't there, try to get from Platform class via
		// PlatformHelper class
		if (adapterManager == null)
			adapterManager = PlatformHelper.getPlatformAdapterManager();
		if (adapterManager == null)
			getDefault().log(
					new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR,
							"Cannot get adapter manager", null)); //$NON-NLS-1$
		return adapterManager;
	}

	/**
	 * The constructor.
	 */
	public GooglePlugin() {
		super();
		plugin = this;
	}

	protected LogService getLogService() {
		if (logServiceTracker == null) {
			logServiceTracker = new ServiceTracker(this.context,
					LogService.class.getName(), null);
			logServiceTracker.open();
		}
		return (LogService) logServiceTracker.getService();
	}

	public void log(IStatus status) {
		final LogService logService = getLogService();
		if (logService != null) {
			logService.log(LogHelper.getLogCode(status), LogHelper
					.getLogMessage(status), status.getException());
		}
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		services = new HashMap();
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}
		if (adapterManagerTracker != null) {
			adapterManagerTracker.close();
			adapterManagerTracker = null;
		}
		this.context = null;
		plugin = null;
	}

	public void registerService(IPresenceService service) {
		if (context != null) {
			services.put(service, context.registerService(
					IPresenceService.class.getName(), service, null));
		}
	}

	public void unregisterService(IPresenceService service) {
		final ServiceRegistration registration = (ServiceRegistration) services
				.remove(service);
		if (registration != null) {
			registration.unregister();
		}
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return default instance of xmpp plugin.
	 */
	public synchronized static GooglePlugin getDefault() {
		if (plugin == null) {
			plugin = new GooglePlugin();
		}
		return plugin;
	}

	public String getNamespaceIdentifier() {
		return NAMESPACE_IDENTIFIER;
	}

}
