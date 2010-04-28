/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceInfo implements IServiceInfo, Serializable {

	private static final long serialVersionUID = 7705971104116138176L;
	
	private long bundleId;
	private Map properties;
	private long usingIds[];

	public ServiceInfo(ServiceReference serviceReference, State platformState) {
		bundleId = serviceReference.getBundle().getBundleId();
		Properties props = new Properties();
		String keys[] = serviceReference.getPropertyKeys();
		for (int i = 0; i < keys.length; i++)
			props.put(keys[i], serviceReference.getProperty(keys[i]));

		properties = props;
		Bundle bundles[] = serviceReference.getUsingBundles();
		if (bundles != null) {
			usingIds = new long[bundles.length];
			for (int i = 0; i < bundles.length; i++)
				usingIds[i] = bundles[i].getBundleId();
		}
	}

	public long getBundleId() {
		return bundleId;
	}

	public String[] getServices() {
		Object o = getProperties().get(Constants.OBJECTCLASS);
		if (o instanceof String[])
			return (String[]) o;
		else
			return null;
	}

	public long getId() {
		Object serviceid = getProperties().get(Constants.SERVICE_ID);
		return (serviceid instanceof Long) ? ((Long) serviceid).longValue()
				: 0L;
	}

	public Map getProperties() {
		return properties;
	}

	public int getRanking() {
		Object ranking = getProperties().get(Constants.SERVICE_RANKING);
		return (ranking instanceof Integer) ? ((Integer) ranking).intValue()
				: 0;
	}

	public long[] getUsingBundleIds() {
		return usingIds;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ServiceInfo[bundleId="); //$NON-NLS-1$
		buffer.append(bundleId);
		buffer.append(", properties="); //$NON-NLS-1$
		buffer.append(properties);
		buffer.append(", usingIds="); //$NON-NLS-1$
		buffer.append(usingIds != null ? arrayToString(usingIds,
				usingIds.length) : null);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

	private String arrayToString(Object array, int len) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("["); //$NON-NLS-1$
		for (int i = 0; i < len; i++) {
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			if (array instanceof long[])
				buffer.append(((long[]) array)[i]);
		}
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
