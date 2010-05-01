/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.internal.remoteservice.soap.host;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.LogHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @since 3.4
 * 
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice.soap.host"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private BundleContext context = null;

	private SOAPServiceTracker serviceTracker;
	
	public SOAPServiceTracker getServiceTracker() {
		return serviceTracker;
	}

	private ServiceTracker logServiceTracker = null;
	
	public void start(BundleContext context) throws Exception {
		plugin = this;
		this.context = context;
		serviceTracker = new SOAPServiceTracker(context);
		serviceTracker.open();

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
		LogService logService = getLogService();
		if (logService != null) {
			logService.log(LogHelper.getLogCode(status), LogHelper
					.getLogMessage(status), status.getException());
		}
	}

	public void stop(BundleContext context) throws Exception {
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}

		if (serviceTracker != null) {
			serviceTracker.close();
			serviceTracker = null;
		}

		plugin = null;
		this.context = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public BundleContext getContext() {
		return context;
	}
}
