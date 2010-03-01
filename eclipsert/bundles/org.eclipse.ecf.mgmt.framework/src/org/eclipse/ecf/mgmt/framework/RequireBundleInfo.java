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

import org.eclipse.osgi.service.resolver.BundleSpecification;

public class RequireBundleInfo implements IRequireBundleInfo, Serializable {

	private static final long serialVersionUID = 964427755647623485L;
	private final String name;
	private final String versionRange;
	private final boolean exported;
	private final boolean optional;
	private final boolean resolved;

	public RequireBundleInfo(BundleSpecification bundleSpecification) {
		name = bundleSpecification.getName();
		versionRange = bundleSpecification.getVersionRange().toString();
		exported = bundleSpecification.isExported();
		optional = bundleSpecification.isOptional();
		resolved = bundleSpecification.isResolved();
	}

	public String getName() {
		return name;
	}

	public String getVersionRange() {
		return versionRange;
	}

	public boolean isExported() {
		return exported;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isResolved() {
		return resolved;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RequireBundleInfo[name="); //$NON-NLS-1$
		buffer.append(name);
		buffer.append(", versionRange="); //$NON-NLS-1$
		buffer.append(versionRange);
		buffer.append(", exported="); //$NON-NLS-1$
		buffer.append(exported);
		buffer.append(", optional="); //$NON-NLS-1$
		buffer.append(optional);
		buffer.append(", resolved="); //$NON-NLS-1$
		buffer.append(resolved);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
