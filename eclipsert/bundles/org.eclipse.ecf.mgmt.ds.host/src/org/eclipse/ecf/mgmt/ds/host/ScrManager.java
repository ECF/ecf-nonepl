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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
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

	private Hashtable compRefs;
	private Object compRefsLock = new Object();
	private static long curID = 0;

	private synchronized long generateID() {
		return curID++;
	}

	class CompRef {
		long bid;
		String name;
		long id=-1;

		public CompRef(long bundleId, String name) {
			this.bid = bundleId;
			this.name = name;
		}

		public boolean equals(Object other) {
			if (other instanceof CompRef) {
				CompRef oRef = (CompRef) other;
				return (oRef.bid == bid && name.equals(oRef.name));
			}
			return false;
		}

		public int hashCode() {
			return name.hashCode();
		}
	}

	public ScrManager(BundleContext context, LogService log) {
		Assert.isNotNull(context);
		this.context = context;
		this.log = log;
	}

	public ScrManager(BundleContext context) {
		this(context, null);
	}
	
	private CompRef getCompRef(long bid, String componentName) {
		synchronized (compRefsLock) {
			if (compRefs == null) compRefs = new Hashtable(101);
		}
		CompRef cRef = new CompRef(bid, componentName);
		synchronized (compRefs) {
			CompRef ref = (CompRef) compRefs.get(cRef);
			if (ref == null) {
				ref = cRef;
				ref.id = generateID();
				compRefs.put(ref, ref);
			}
			return ref;
		}
	}

	public IComponentInfo getComponent(Long componentId) {
		if (componentId == null)
			return null;
		ScrService scrService = getScrService();
		if (scrService == null)
			return null;
		Component comp = scrService.getComponent(componentId.longValue());
		if (comp == null)
			return null;
		CompRef cRef = getCompRef(comp.getId(), comp.getName());
		if (cRef == null) return null;
		return new ComponentInfo(cRef.id, comp, getBundleDescription(comp.getBundle()));
	}

	private Component[] getComponentsForBundle(ScrService scrService, Bundle bundle) {
		List results = new ArrayList();
		if (bundle == null) {
			Bundle[] bundles = context.getBundles();
			if (bundles != null) {
				for(int i=0; i < bundles.length; i++) {
					Component[] comps = scrService.getComponents(bundles[i]);
					if (comps != null) for(int j=0; j < comps.length; j++) results.add(comps[j]);
				}
			}
		} else {
            Component[] comps = scrService.getComponents(bundle);
			if (comps != null) for(int j=0; j < comps.length; j++) results.add(comps[j]);
		}
		return (Component[]) results.toArray(new Component[] {});
	}
	
	private Collection getComponentInfoForBundle(ScrService scrService, Bundle bundle) {
		Component[] components = getComponentsForBundle(scrService, bundle);
		if (components == null || components.length == 0)
			return null;
		List results = new ArrayList();
		for (int i = 0; i < components.length; i++) {
			String componentName = components[i].getName();
			if (bundle == null)
				bundle = components[i].getBundle();
			CompRef cRef = getCompRef(bundle.getBundleId(), componentName);
			if (cRef != null)
			results.add(new ComponentInfo(cRef.id, components[i],
					getBundleDescription(bundle)));
		}
		return results;
	}

	private BundleDescription getBundleDescription(Bundle bundle) {
		PlatformAdmin platformAdmin = getPlatformAdmin();
		if (platformAdmin == null)
			return null;
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
				scrTracker = new ServiceTracker(context,
						org.apache.felix.scr.ScrService.class.getName(), null);
				scrTracker.open();
			}
			return (ScrService) scrTracker.getService();
		}
	}

	public IComponentInfo[] getComponents() {
		ScrService scrService = getScrService();
		if (scrService == null)
			return null;
		return (IComponentInfo[]) getComponentInfoForBundle(scrService, null).toArray(
				new IComponentInfo[] {});
	}

	private IStatus enableDisableComponent(Long id, boolean enable) {
		if (id == null)
			return createErrorStatus("id cannot be null"); //$NON-NLS-1$
		ScrService scrService = getScrService();
		if (scrService == null)
			return createErrorStatus("scrService is not available"); //$NON-NLS-1$
		CompRef cRef = findCRefForId(id.longValue());
		if (cRef == null)
			return createErrorStatus("Component with id=" + id + " cannot be found"); //$NON-NLS-1$ //$NON-NLS-2$
		Component comp = findComponent(scrService, cRef);
		if (comp == null)
			return createErrorStatus("Component with id=" + id + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			if (enable)
				comp.enable();
			else
				comp.disable();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (IllegalStateException e) {
			return createErrorStatus(
					"Component with id=" + id + " cannot be " + ((enable) ? "enabled" : "disabled"), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	public IStatus enable(Long id) {
		return enableDisableComponent(id, true);
	}

	private Component findComponent(ScrService scrService, CompRef cRef) {
		Bundle bundle = findBundleForId(cRef.bid);
		if (bundle == null)
			return null;
		Component[] bundleComponents = scrService.getComponents(bundle);
		if (bundleComponents == null)
			return null;
		for (int i = 0; i < bundleComponents.length; i++) {
			if (cRef.name != null
					&& cRef.name.equals(bundleComponents[i].getName()))
				return bundleComponents[i];
		}
		return null;
	}

	private Bundle findBundleForId(long bid) {
		Bundle[] bundles = context.getBundles();
		if (bundles == null)
			return null;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getBundleId() == bid)
				return bundles[i];
		}
		return null;
	}

	private CompRef findCRefForId(long id) {
		synchronized (compRefsLock) {
			if (compRefs == null)
				compRefs = new Hashtable(101);
		}
		synchronized (compRefs) {
			for (Iterator i = compRefs.keySet().iterator(); i.hasNext();) {
				CompRef cr = (CompRef) i.next();
				if (cr.id == id)
					return cr;
			}
		}
		return null;
	}

	public IStatus disable(Long id) {
		return enableDisableComponent(id, false);
	}

	public IComponentInfo[] getComponents(IBundleId bundleId) {
		ScrService scrService = getScrService();
		if (scrService == null)
			return null;
		Bundle bundle = getBundle(bundleId);
		if (bundle == null)
			return null;
		return (IComponentInfo[]) getComponentInfoForBundle(scrService, bundle).toArray(
				new IComponentInfo[] {});
	}

	private Bundle getBundle(IBundleId bundleId) {
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if (match(bundles[i], bundleId))
				return bundles[i];
		}
		return null;
	}

	private boolean match(Bundle bundle, IBundleId bundleId) {
		if (bundle.getSymbolicName().equals(bundleId.getSymbolicName())) {
			String bidVersion = bundleId.getVersion();
			if (bidVersion == null)
				return true;
			return bidVersion.equals(bundle.getVersion().toString());
		}
		return false;
	}

	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		final IAdapterManager adapterManager = Activator.getDefault()
				.getAdapterManager();
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
