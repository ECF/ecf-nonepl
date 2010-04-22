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

	public IComponentInfo[] getComponents(IBundleId bundleId);
	public IComponentInfo getComponent(Long id);
	public IComponentInfo[] getComponents();
	
	public IStatus enable(Long id);
	public IStatus disable(Long id);
}
