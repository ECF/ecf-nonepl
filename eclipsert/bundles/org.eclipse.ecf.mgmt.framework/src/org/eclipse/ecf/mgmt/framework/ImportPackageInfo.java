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

import org.eclipse.osgi.service.resolver.ImportPackageSpecification;

public class ImportPackageInfo implements IImportPackageInfo, Serializable {

	private static final long serialVersionUID = 4007089813388018633L;
	private final String name;
	private final String versionRange;
	private final Map attributes;
	private final Map directives;
	private final boolean resolved;
	private boolean optional;

	public ImportPackageInfo(ImportPackageSpecification importSpecification) {
		optional = false;
		name = importSpecification.getName();
		versionRange = importSpecification.getVersionRange().toString();
		attributes = importSpecification.getAttributes();
		directives = importSpecification.getDirectives();
		resolved = importSpecification.isResolved();
		String v = (String) directives.get("resolution"); //$NON-NLS-1$
		if (v != null && v.equals("optional")) //$NON-NLS-1$
			optional = true;
	}

	public String getName() {
		return name;
	}

	public String getVersionRange() {
		return versionRange;
	}

	public boolean isResolved() {
		return resolved;
	}

	public boolean isOptional() {
		return optional;
	}

	public Map getAttributes() {
		return attributes;
	}

	public Map getDirectives() {
		return directives;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ImportPackageInfo[name="); //$NON-NLS-1$
		buffer.append(name);
		buffer.append(", versionRange="); //$NON-NLS-1$
		buffer.append(versionRange);
		buffer.append(", attributes="); //$NON-NLS-1$
		buffer.append(attributes);
		buffer.append(", directives="); //$NON-NLS-1$
		buffer.append(directives);
		buffer.append(", resolved="); //$NON-NLS-1$
		buffer.append(resolved);
		buffer.append(", optional="); //$NON-NLS-1$
		buffer.append(optional);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
