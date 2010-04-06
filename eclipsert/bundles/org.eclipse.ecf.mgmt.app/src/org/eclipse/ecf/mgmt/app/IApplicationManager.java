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

import org.eclipse.core.runtime.IStatus;

public interface IApplicationManager {

	public IApplicationInfo[] getApplications();

	public IApplicationInstanceInfo[] getRunningApplications();

	public IStatus start(String applicationId, String[] applicationArgs);
	
	public IStatus stop(String applicationInstanceId);
	
	public IStatus lock(String applicationId);
	
	public IStatus unlock(String applicationId);
}
