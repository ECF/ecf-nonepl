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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.mgmt.framework.IServiceInfo;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.ServiceInfo;
import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ServiceManager extends AbstractFrameworkManager implements IServiceManager, IAdaptable {

	public ServiceManager(BundleContext context, LogService logger) {
		super(context,logger);
	}

	public ServiceManager(BundleContext context) {
		this(context, null);
	}
	
	private synchronized ServiceReference[] getAllServiceReferences() {
		try {
			return context.getAllServiceReferences(null, null);
		} catch (InvalidSyntaxException e) {
			// cant happen
		}
		return null;
	}

	public IServiceInfo[] getServices(IBundleId bundleId) {
		ServiceReference srs[] = getAllServiceReferences();
		if (srs == null)
			return null;
		State platformState = getPlatformState();
		if (platformState == null)
			return null;
		List results = new ArrayList();
		for (int i = 0; i < srs.length; i++)
			if (bundleId == null) {
				results.add(new ServiceInfo(srs[i], platformState));
			} else {
				Bundle b = srs[i].getBundle();
				String version = getBundleVersion(b);
				String bundleIdVersion = bundleId.getVersion();
				if (b.getSymbolicName().equals(bundleId.getSymbolicName())) {
					if (bundleIdVersion == null || (version.equals(bundleIdVersion))) results.add(new ServiceInfo(srs[i], platformState));
				}
			}

		return (IServiceInfo[]) results.toArray(new IServiceInfo[0]);
	}

	public IServiceInfo[] getServices() {
		return getServices((IBundleId) null);
	}

	public IServiceInfo getService(Long serviceid) {
		if (serviceid == null) return null;
		ServiceReference srs[] = getAllServiceReferences();
		if (srs == null)
			return null;
		State platformState = getPlatformState();
		if (platformState == null)
			return null;

		for(int i=0; i < srs.length; i++) {
			Object o = srs[i].getProperty(Constants.SERVICE_ID);
			long sid = (o instanceof Long) ? ((Long) o).longValue()
					: 0L;
			if (sid == serviceid.longValue()) return new ServiceInfo(srs[i],platformState);
		}
		return null;
	}

}
