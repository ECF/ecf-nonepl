/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.PlatformHelper;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class OSCARPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.oscar"; //$NON-NLS-1$

	protected static final String NAMESPACE_IDENTIFIER = "ecf.oscar.icqlib"; //$NON-NLS-1$

	// The shared instance
	private static OSCARPlugin plugin;

	private ServiceTracker logServiceTracker = null;

	private Map services;

	public OSCARPlugin() {
		// Empty constructor
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		services = new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OSCARPlugin getDefault() {
		return plugin;
	}

	/**
	 * Get logger for OK messages (info or debug)
	 *
	 * @param message
	 */
	public static void log(String message) {
		getDefault().log(new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, null));
	}

	/**
	 * Get logger for Error messages (warn or error)
	 *
	 * @param message
	 * @param e
	 */
	public static void log(String message, Throwable e) {
		getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e));
	}

	protected LogService getLogService() {
		if (logServiceTracker == null) {
			logServiceTracker = new ServiceTracker(getContext(), LogService.class.getName(), null);
			logServiceTracker.open();
		}
		return (LogService) logServiceTracker.getService();
	}

	public void log(IStatus status) {
		final LogService logService = getLogService();
		if (logService != null) {
			logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
		}
	}

	public BundleContext getContext() {
		return plugin.getBundle().getBundleContext();
	}

	private ServiceTracker adapterManagerTracker = null;

	public IAdapterManager getAdapterManager() {
		// First, try to get the adapter manager via service tracker
		if (adapterManagerTracker == null) {
			adapterManagerTracker = new ServiceTracker(plugin.getContext(), IAdapterManager.class.getName(), null);
			adapterManagerTracker.open();
		}

		IAdapterManager adapterManager = (IAdapterManager) adapterManagerTracker.getService();

		// Then, if the service isn't there, try to get from Platform class via PlatformHelper class
		if (adapterManager == null)
			adapterManager = PlatformHelper.getPlatformAdapterManager();
		if (adapterManager == null)
			getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Cannot get adapter manager", null)); //$NON-NLS-1$

		return adapterManager;
	}

	public String getNamespaceIdentifier() {
		return NAMESPACE_IDENTIFIER;
	}

	public void registerService(IPresenceService service) {
		if (plugin.getContext() != null) {
			services.put(service, plugin.getContext().registerService(IPresenceService.class.getName(), service, null));
		}
	}

	public void unregisterService(IPresenceService service) {
		final ServiceRegistration registration = (ServiceRegistration) services.remove(service);
		if (registration != null) {
			registration.unregister();
		}
	}
}
