/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.eclipse.equinox.p2.repository.IRepository;

@SuppressWarnings("rawtypes")
public class RepositoryInfo implements IRepositoryInfo, Serializable {

	private static final long serialVersionUID = 6142653685320084516L;
	private boolean modifiable;
	private String version;
	private String type;
	private String provider;
	private Map properties;
	private String name;
	private URI location;
	private String description;

	public RepositoryInfo(IRepository repo) {
		this.modifiable = repo.isModifiable();
		this.version = repo.getVersion();
		this.type = repo.getType();
		this.provider = repo.getProvider();
		this.properties = repo.getProperties();
		this.name = repo.getName();
		this.location = repo.getLocation();
		this.description = repo.getDescription();
	}

	public String getDescription() {
		return description;
	}

	public URI getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public Map getProperties() {
		return properties;
	}

	public String getProvider() {
		return provider;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RepositoryInfo[description=");
		buffer.append(description);
		buffer.append(", location=");
		buffer.append(location);
		buffer.append(", modifiable=");
		buffer.append(modifiable);
		buffer.append(", name=");
		buffer.append(name);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", provider=");
		buffer.append(provider);
		buffer.append(", type=");
		buffer.append(type);
		buffer.append(", version=");
		buffer.append(version);
		buffer.append("]");
		return buffer.toString();
	}

}
