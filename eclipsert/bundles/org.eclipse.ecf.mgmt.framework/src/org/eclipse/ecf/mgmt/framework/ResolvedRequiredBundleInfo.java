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

import org.eclipse.osgi.service.resolver.BundleDescription;

public class ResolvedRequiredBundleInfo extends ResolvedDependencyInfo
		implements IResolvedRequiredBundleInfo, Serializable {

	private static final long serialVersionUID = -7317193997634523498L;

	public ResolvedRequiredBundleInfo(BundleDescription bundleDescription) {
		super(bundleDescription.getName());
		this.bundleId = new BundleId(bundleDescription.getSymbolicName(),
				bundleDescription.getVersion().toString());
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ResolvedRequiredBundleInfo[name="); //$NON-NLS-1$
		buffer.append(getName());
		buffer.append(", resolvingBundle="); //$NON-NLS-1$
		buffer.append(getResolvingBundle());
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
