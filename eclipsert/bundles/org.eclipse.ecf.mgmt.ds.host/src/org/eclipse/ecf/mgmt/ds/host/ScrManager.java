/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.ds.host;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.ds.host.Activator;
import org.eclipse.ecf.mgmt.ds.ComponentInfo;
import org.eclipse.ecf.mgmt.ds.IComponentInfo;
import org.eclipse.ecf.mgmt.ds.IScrManager;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class ScrManager implements IAdaptable, IScrManager {

	private BundleContext context;
	private LogService log;
	private Object lock = new Object();
	private ServiceTracker scrTracker;
	private ServiceTracker platformAdminServiceTracker;
	
	public ScrManager(BundleContext context, LogService log) {
		Assert.isNotNull(context);
		this.context = context;
		this.log = log;
	}
	
	public ScrManager(BundleContext context) {
		this(context,null);
	}
	
	public IComponentInfo getComponentInfo(Long componentId) {
		if (componentId == null) return null;
		ScrService scrService = getScrService();
		if (scrService == null) return null;
		Component comp = scrService.getComponent(componentId.longValue());
		if (comp == null) return null;
		BundleDescription bd = getBundleDescription(comp.getBundle());
		if (bd == null) return null;
		return (comp == null)?null:new ComponentInfo(comp,bd);
	}

	private BundleDescription getBundleDescription(Bundle bundle) {
		PlatformAdmin platformAdmin = getPlatformAdmin();
		if (platformAdmin == null) return null;
		return platformAdmin.getState(false).getBundle(bundle.getBundleId());
	}

	private PlatformAdmin getPlatformAdmin() {
		synchronized (lock) {
			if (platformAdminServiceTracker == null) {
				platformAdminServiceTracker = new ServiceTracker(context,
						PlatformAdmin.class.getName(), null);
				platformAdminServiceTracker.open();
			}
			return (PlatformAdmin) platformAdminServiceTracker.getService();
		}
	}


	private ScrService getScrService() {
		synchronized (lock) {
			if (scrTracker == null) {
				scrTracker = new ServiceTracker(context,org.apache.felix.scr.ScrService.class.getName(),null);
				scrTracker.open();
			}
			return (ScrService) scrTracker.getService();
		}
	}

	public IComponentInfo[] getAllComponentInfo() {
		ScrService scrService = getScrService();
		if (scrService == null) return null;
		Component[] comps = scrService.getComponents();
		if (comps != null) {
			IComponentInfo[] results = new IComponentInfo[comps.length];
			for(int i=0; i < comps.length; i++) {
				BundleDescription bd = getBundleDescription(comps[i].getBundle());
				if (bd == null) continue;
				results[i] = new ComponentInfo(comps[i],bd);
			}
			return results;
		}
		return null;
	}

	public IStatus enable(Long componentId) {
		if (componentId == null) return createErrorStatus("componentId cannot be null"); //$NON-NLS-1$
		ScrService scrService = getScrService();
		if (scrService == null) return createErrorStatus("scrService is null"); //$NON-NLS-1$
		Component comp = scrService.getComponent(componentId.longValue());
		if (comp == null) return createErrorStatus("Component with id="+componentId+" not found"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			comp.enable();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (IllegalStateException e) {
			return createErrorStatus("Component with id="+componentId+" cannot be enabled",e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public IStatus disable(Long componentId) {
		if (componentId == null) return createErrorStatus("componentId cannot be null"); //$NON-NLS-1$
		ScrService scrService = getScrService();
		if (scrService == null) return createErrorStatus("scrService is null"); //$NON-NLS-1$
		Component comp = scrService.getComponent(componentId.longValue());
		if (comp == null) return createErrorStatus("Component with id="+componentId+" not found"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			comp.disable();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (IllegalStateException e) {
			return createErrorStatus("Component with id="+componentId+" cannot be disabled",e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public IComponentInfo[] getComponentInfo(IBundleId bundleId) {
		ScrService scrService = getScrService();
		if (scrService == null) return null;
		Component[] comps = scrService.getComponents();
		if (comps != null) {
			List results = new ArrayList();
			for(int i=0; i < comps.length; i++) {
				Bundle bundle = comps[i].getBundle();
				if (match(bundle,bundleId)) {
					BundleDescription bd = getBundleDescription(bundle);
					results.add(new ComponentInfo(comps[i],bd));
				}
			}
			return (IComponentInfo[]) results.toArray(new IComponentInfo[] {});
		}
		return null;
	}

	private boolean match(Bundle bundle, IBundleId bundleId) {
		if (bundle.getSymbolicName().equals(bundleId.getSymbolicName())) {
			String bidVersion = bundleId.getVersion();
			if (bidVersion == null) return true;
			return bidVersion.equals(bundle.getVersion().toString());
		}
		return false;
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
			if (scrTracker != null) {
				scrTracker.close();
				scrTracker = null;
			}
			if (platformAdminServiceTracker != null) {
				platformAdminServiceTracker.close();
				platformAdminServiceTracker = null;
			}
			context = null;
			log = null;
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


}
