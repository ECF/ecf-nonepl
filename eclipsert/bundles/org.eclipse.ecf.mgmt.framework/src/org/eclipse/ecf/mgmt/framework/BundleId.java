/*******************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

public class BundleId implements IBundleId, Serializable {

	private static final long serialVersionUID = 1175197315866010451L;

	private String symbolicName;
	private String version;

	private int hashCode;

	public BundleId(String symbolicName, String version) {
		Assert.isNotNull(symbolicName);
		this.symbolicName = symbolicName;
		this.version = version;
		int hc = 37 + symbolicName.hashCode();
		this.hashCode = hc
				+ ((this.version == null) ? 0 : this.version.hashCode());
	}

	public BundleId(String symbolicName) {
		this(symbolicName, null);
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getVersion() {
		return version;
	}

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof BundleId))
			return false;
		BundleId other = (BundleId) o;
		if (other.symbolicName.equals(symbolicName))
			return version == null || !version.equals(other.version) ? true
					: true;
		else
			return false;

	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BundleId[symbolicName="); //$NON-NLS-1$
		buffer.append(symbolicName);
		buffer.append(", version="); //$NON-NLS-1$
		buffer.append(version);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
