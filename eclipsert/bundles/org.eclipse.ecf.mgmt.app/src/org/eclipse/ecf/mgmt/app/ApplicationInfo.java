/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.app;

import java.io.Serializable;
import java.util.Map;

import org.osgi.service.application.ApplicationDescriptor;

public class ApplicationInfo implements IApplicationInfo, Serializable {

	private static final long serialVersionUID = 4586368331252278310L;
	private String id;
	private String name;
	private Map properties;
	private boolean isLocked;
	private boolean isLaunchable;
	private boolean isVisible;

	public ApplicationInfo(ApplicationDescriptor appDescriptor) {
		this.id = appDescriptor.getApplicationId();
		this.properties = appDescriptor.getProperties(null);
		this.name = (String) properties.get("application.name"); //$NON-NLS-1$
		Object lockedProperty = properties.get("application.locked"); //$NON-NLS-1$
		if (lockedProperty instanceof Boolean) {
			this.isLocked = ((Boolean) lockedProperty).booleanValue();
		} else if (lockedProperty instanceof String) {
			this.isLocked = Boolean.getBoolean((String) lockedProperty);
		}
		Object launchableProperty = properties.get("application.launchable"); //$NON-NLS-1$
		if (launchableProperty instanceof Boolean) {
			this.isLaunchable = ((Boolean) launchableProperty).booleanValue();
		} else if (lockedProperty instanceof String) {
			this.isLaunchable = Boolean.getBoolean((String) launchableProperty);
		}
		Object visibleProperty = properties.get("application.visible"); //$NON-NLS-1$
		if (visibleProperty instanceof Boolean) {
			this.isVisible = ((Boolean) visibleProperty).booleanValue();
		} else if (lockedProperty instanceof String) {
			this.isVisible = Boolean.getBoolean((String) visibleProperty);
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map getProperties() {
		return properties;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public boolean isLaunchable() {
		return isLaunchable;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ApplicationInfo[id="); //$NON-NLS-1$
		buffer.append(id);
		buffer.append(", name="); //$NON-NLS-1$
		buffer.append(name);
		buffer.append(", properties="); //$NON-NLS-1$
		buffer.append(properties);
		buffer.append(", isLocked="); //$NON-NLS-1$
		buffer.append(isLocked);
		buffer.append(", isLaunchable="); //$NON-NLS-1$
		buffer.append(isLaunchable);
		buffer.append(", isVisible="); //$NON-NLS-1$
		buffer.append(isVisible);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
