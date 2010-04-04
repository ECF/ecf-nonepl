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

import org.eclipse.equinox.p2.metadata.ICopyright;

public class CopyrightInfo implements ICopyrightInfo, Serializable {

	private static final long serialVersionUID = 3852708688892784596L;
	private URI location;
	private String body;

	public CopyrightInfo(ICopyright cr) {
		this.location = cr.getLocation();
		this.body = cr.getBody();
	}

	public URI getLocation() {
		return location;
	}

	public String getBody() {
		return body;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CopyrightInfo[location=");
		buffer.append(location);
		buffer.append(", body=");
		buffer.append(body);
		buffer.append("]");
		return buffer.toString();
	}

}
