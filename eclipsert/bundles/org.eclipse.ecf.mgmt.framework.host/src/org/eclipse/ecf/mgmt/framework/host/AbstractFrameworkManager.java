/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.framework.host;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.framework.host.Activator;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractFrameworkManager implements IAdaptable {

	protected BundleContext context;
	protected LogService log;
	protected Object lock = new Object();
	private ServiceTracker platformAdminServiceTracker;

	public AbstractFrameworkManager(BundleContext context, LogService log) {
		Assert.isNotNull(context);
		this.context = context;
		this.log = log;
	}

	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

	public void close() {
		synchronized (lock) {
			if (platformAdminServiceTracker != null) {
				platformAdminServiceTracker.close();
				platformAdminServiceTracker = null;
			}
			context = null;
			log = null;
		}
	}

	protected PlatformAdmin getPlatformAdmin() {
		synchronized (lock) {
			if (platformAdminServiceTracker == null) {
				platformAdminServiceTracker = new ServiceTracker(context,
						PlatformAdmin.class.getName(), null);
				platformAdminServiceTracker.open();
			}
			return (PlatformAdmin) platformAdminServiceTracker.getService();
		}
	}

	protected State getPlatformState() {
		synchronized (lock) {
			PlatformAdmin platformAdmin = getPlatformAdmin();
			if (platformAdmin == null)
				return null;
			else
				return platformAdmin.getState(false);
		}
	}

	protected Bundle[] getAllBundles() {
		synchronized (lock) {
			return context.getBundles();
		}
	}

	protected IStatus createErrorStatus(String message, Throwable t) {
		logError(message, t);
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				message, t);
	}

	protected IStatus createErrorStatus(String message) {
		logError(message, null);
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				message, null);
	}

	protected void logError(String message, Throwable t) {
		if (log != null) {
			log.log(LogService.LOG_ERROR, message, t);
		}
		System.err.println(message);
		if (t != null)
			t.printStackTrace(System.err);
	}

	protected String getBundleVersion(Bundle bundle) {
		return (String) bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
	}

}
