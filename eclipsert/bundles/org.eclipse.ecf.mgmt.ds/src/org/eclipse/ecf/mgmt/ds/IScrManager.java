/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.ds;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.framework.IBundleId;

public interface IScrManager {

	public IComponentInfo[] getComponentInfo(IBundleId bundleId);
	public IComponentInfo getComponentInfo(Long componentId);
	public IComponentInfo[] getAllComponentInfo();
	
	public IStatus enable(Long componentId);
	public IStatus disable(Long componentId);
}
