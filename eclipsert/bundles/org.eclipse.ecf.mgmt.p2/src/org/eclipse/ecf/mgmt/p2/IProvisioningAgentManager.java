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

import org.eclipse.ecf.mgmt.p2.install.IFeatureInstallManager;
import org.eclipse.ecf.mgmt.p2.profile.IProfileManager;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryManager;

public interface IProvisioningAgentManager {

	public IFeatureInstallManager getFeatureInstallManager();

	public IRepositoryManager getRepositoryManager();

	public IProfileManager getProfileManager();
	
}
