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

import org.eclipse.osgi.service.resolver.ExportPackageDescription;

public class ResolvedImportedPackageInfo extends ResolvedDependencyInfo
		implements IResolvedImportedPackageInfo, Serializable {

	private static final long serialVersionUID = -3555644435597547826L;

	public ResolvedImportedPackageInfo(
			ExportPackageDescription exportPackageDescription) {
		super(exportPackageDescription.getName());
		this.bundleId = new BundleId(exportPackageDescription.getExporter()
				.getSymbolicName(), exportPackageDescription.getExporter()
				.getVersion().toString());
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ResolvedImportedPackageInfo[name="); //$NON-NLS-1$
		buffer.append(getName());
		buffer.append(", resolvingBundle="); //$NON-NLS-1$
		buffer.append(getResolvingBundle());
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
