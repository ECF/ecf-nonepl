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

import org.osgi.service.application.ApplicationHandle;

public class ApplicationInstanceInfo implements IApplicationInstanceInfo {

	private String id;
	private String state;
	private IApplicationInfo application;

	public ApplicationInstanceInfo(ApplicationHandle appInstance) {
		this.id = appInstance.getInstanceId();
		this.state = appInstance.getState();
		this.application = new ApplicationInfo(
				appInstance.getApplicationDescriptor());
	}

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public IApplicationInfo getApplication() {
		return application;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ApplicationInstanceInfo[id="); //$NON-NLS-1$
		buffer.append(id);
		buffer.append(", state="); //$NON-NLS-1$
		buffer.append(state);
		buffer.append(", application="); //$NON-NLS-1$
		buffer.append(application);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
