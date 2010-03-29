/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.profile;

import java.util.Map;

import org.eclipse.equinox.p2.engine.IProfile;

public class ProfileInfo implements IProfileInfo {

	private String id;
	private Map properties;
	private long timestamp;

	public ProfileInfo(IProfile profile) {
		this.id = profile.getProfileId();
		this.properties = profile.getProperties();
		this.timestamp = profile.getTimestamp();
	}

	public String getProfileId() {
		return id;
	}

	public Map getProperties() {
		return properties;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ProfileInfo[id=");
		buffer.append(id);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", timestamp=");
		buffer.append(timestamp);
		buffer.append("]");
		return buffer.toString();
	}

}
