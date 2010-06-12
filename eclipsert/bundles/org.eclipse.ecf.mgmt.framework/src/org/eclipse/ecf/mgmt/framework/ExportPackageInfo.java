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

import org.eclipse.osgi.service.resolver.ExportPackageDescription;

public class ExportPackageInfo implements IExportPackageInfo, Serializable {

	private static final long serialVersionUID = -3666535566379690443L;
	private final String name;
	private final String version;
	private final Map attributes;
	private final Map directives;

	public ExportPackageInfo(ExportPackageDescription exportPackageDescription) {
		name = exportPackageDescription.getName();
		version = exportPackageDescription.getVersion().toString();
		attributes = exportPackageDescription.getAttributes();
		directives = exportPackageDescription.getDirectives();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Map getAttributes() {
		return attributes;
	}

	public Map getDirectives() {
		return directives;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ExportPackageInfo[name="); //$NON-NLS-1$
		buffer.append(name);
		buffer.append(", version="); //$NON-NLS-1$
		buffer.append(version);
		buffer.append(", attributes="); //$NON-NLS-1$
		buffer.append(attributes);
		buffer.append(", directives="); //$NON-NLS-1$
		buffer.append(directives);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
