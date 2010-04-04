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
import java.net.URI;

import org.eclipse.equinox.p2.metadata.ILicense;

public class LicenseInfo implements ILicenseInfo, Serializable {

	private static final long serialVersionUID = -4606908296710108594L;
	private URI location;
	private String body;
	private String uuid;

	public LicenseInfo(ILicense l) {
		this.location = l.getLocation();
		this.body = l.getBody();
		this.uuid = l.getUUID();
	}

	public URI getLocation() {
		return location;
	}

	public String getBody() {
		return body;
	}

	public String getUUID() {
		return uuid;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LicenseInfo[location=");
		buffer.append(location);
		buffer.append(", body=");
		buffer.append(body);
		buffer.append(", uuid=");
		buffer.append(uuid);
		buffer.append("]");
		return buffer.toString();
	}

}
