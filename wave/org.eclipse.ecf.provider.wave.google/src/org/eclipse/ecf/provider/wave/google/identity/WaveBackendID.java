/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.identity;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;

public class WaveBackendID extends BaseID {

	private static final long serialVersionUID = 5842059732119395881L;

	private String userAtDomain;
	private String host;
	private int port;
	private int hashCode;
	
	public WaveBackendID(Namespace namespace, String userAtDomain, String host, int port) {
		super(namespace);

		this.userAtDomain = userAtDomain;
		this.host = host;
		this.port = port;
		this.hashCode = hashCode();
	}

	public String getUserAtDomain() {
		return userAtDomain;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	protected int namespaceCompareTo(BaseID o) {
		if (!(o instanceof WaveBackendID)) return 0;
		return getName().compareTo(o.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (this == o) return true;
		if (this == null) return false;
		if (!(o instanceof WaveBackendID)) return false;
		WaveBackendID other = (WaveBackendID) o;
		return (userAtDomain.equals(other.getUserAtDomain()) && host.equals(other.getHost()) && port == other.getPort());
	}

	protected String namespaceGetName() {
		return userAtDomain + " [" + host + ":" + port + "]";
	}

	protected int namespaceHashCode() {
		return hashCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		result = prime * result	+ ((userAtDomain == null) ? 0 : userAtDomain.hashCode());
		return result;
	}

}
