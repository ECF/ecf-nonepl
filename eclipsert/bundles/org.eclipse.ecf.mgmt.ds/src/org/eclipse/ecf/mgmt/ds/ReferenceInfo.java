/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.ds;

import java.io.Serializable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ReferenceInfo implements IReferenceInfo, Serializable {

	private static final long serialVersionUID = -2100180202415162228L;

	private String name;
	private String serviceName;
	private long[] serviceReferenceIds;
	private boolean isSatisfied;
	private boolean isOptional;
	private boolean isMultiple;
	private boolean isStatic;
	private String target;
	private String bindMethodName;
	private String unbindMethodName;

	public ReferenceInfo(org.apache.felix.scr.Reference ref) {
		this.name = ref.getName();
		this.serviceName = ref.getServiceName();
		ServiceReference[] srefs = ref.getServiceReferences();
		if (srefs != null) {
			this.serviceReferenceIds = new long[srefs.length];
			for (int i = 0; i < srefs.length; i++) {
				this.serviceReferenceIds[i] = getServiceId(srefs[i]);
			}

		}
		this.isSatisfied = ref.isSatisfied();
		this.isOptional = ref.isOptional();
		this.isMultiple = ref.isMultiple();
		this.isStatic = ref.isStatic();
		this.target = ref.getTarget();
		this.bindMethodName = ref.getBindMethodName();
		this.unbindMethodName = ref.getUnbindMethodName();
	}

	private long getServiceId(ServiceReference serviceReference) {
		Object o = serviceReference.getProperty(Constants.SERVICE_ID);
		if (o instanceof Long)
			return ((Long) o).longValue();
		return 0;
	}

	public String getName() {
		return name;
	}

	public String getService() {
		return serviceName;
	}

	public long[] getIds() {
		return serviceReferenceIds;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public boolean isMultiple() {
		return isMultiple;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getTarget() {
		return target;
	}

	public String getBindMethodName() {
		return bindMethodName;
	}

	public String getUnbindMethodName() {
		return unbindMethodName;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ReferenceInfo[name=");
		buffer.append(name);
		buffer.append(", serviceName=");
		buffer.append(serviceName);
		buffer.append(", serviceReferenceIds=");
		buffer.append(serviceReferenceIds != null ? arrayToString(
				serviceReferenceIds, serviceReferenceIds.length) : null);
		buffer.append(", isSatisfied=");
		buffer.append(isSatisfied);
		buffer.append(", isOptional=");
		buffer.append(isOptional);
		buffer.append(", isMultiple=");
		buffer.append(isMultiple);
		buffer.append(", isStatic=");
		buffer.append(isStatic);
		buffer.append(", target=");
		buffer.append(target);
		buffer.append(", bindMethodName=");
		buffer.append(bindMethodName);
		buffer.append(", unbindMethodName=");
		buffer.append(unbindMethodName);
		buffer.append("]");
		return buffer.toString();
	}

	private String arrayToString(Object array, int len) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < len; i++) {
			if (i > 0)
				buffer.append(", ");
			if (array instanceof long[])
				buffer.append(((long[]) array)[i]);
		}
		buffer.append("]");
		return buffer.toString();
	}

}
