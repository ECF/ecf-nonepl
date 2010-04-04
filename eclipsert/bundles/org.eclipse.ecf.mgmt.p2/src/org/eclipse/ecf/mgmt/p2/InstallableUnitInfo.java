/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.p2.metadata.ICopyright;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.IUpdateDescriptor;
import org.eclipse.equinox.p2.metadata.Version;

public class InstallableUnitInfo implements IInstallableUnitInfo, Serializable {

	private static final long serialVersionUID = 9083209362827317722L;
	private IVersionedId id;
	private Map properties;
	private boolean singleton;
	private boolean resolved;
	private Collection licenses;
	private CopyrightInfo copyright;
	private UpdateDescriptorInfo updateDescriptor;

	public InstallableUnitInfo(IInstallableUnit iu) {
		Assert.isNotNull(iu);
		Version v = iu.getVersion();
		this.id = new VersionedId(iu.getId(), (v == null) ? null : v.toString());
		this.properties = iu.getProperties();
		this.singleton = iu.isSingleton();
		this.resolved = iu.isResolved();
		this.licenses = createLicenses(iu.getLicenses());
		ICopyright cr = iu.getCopyright();
		this.copyright = (cr == null) ? null : new CopyrightInfo(cr);
		IUpdateDescriptor ud = iu.getUpdateDescriptor();
		this.updateDescriptor = (ud == null) ? null : new UpdateDescriptorInfo(
				ud);
	}

	private Collection createLicenses(Collection licenses2) {
		List results = new ArrayList();
		if (licenses2 == null)
			return results;
		for (Iterator i = licenses2.iterator(); i.hasNext();) {
			results.add(new LicenseInfo((ILicense) i.next()));
		}
		return results;
	}

	public IVersionedId getId() {
		return id;
	}

	public Map getProperties() {
		return properties;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isResolved() {
		return resolved;
	}

	public Collection getLicenses() {
		return licenses;
	}

	public ICopyrightInfo getCopyright() {
		return copyright;
	}

	public IUpdateDescriptorInfo getUpdateDescriptor() {
		return updateDescriptor;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("InstallableUnitInfo[id=");
		buffer.append(id);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", singleton=");
		buffer.append(singleton);
		buffer.append(", resolved=");
		buffer.append(resolved);
		buffer.append(", licenses=");
		buffer.append(licenses);
		buffer.append(", copyright=");
		buffer.append(copyright);
		buffer.append(", updateDescriptor=");
		buffer.append(updateDescriptor);
		buffer.append("]");
		return buffer.toString();
	}

}
