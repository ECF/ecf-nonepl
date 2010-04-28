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

import java.util.Map;

import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.mgmt.framework.IServiceInfo;

public interface IComponentInfo {

	public long getId();
	
	public long getComponentId();
	
	public String getName();
	
	public int getState();
	
	public IBundleId getBundleId();
	
	public String getFactory();
	
	public boolean isServiceFactory();
	
	public String getClassName();
	
	public boolean isDefaultEnabled();
	
	public boolean isImmediate();
	
	public String[] getServices();
	
	@SuppressWarnings("rawtypes")
	public Map getProperties();
	
	public IReferenceInfo[] getReferences();
	
	public boolean isActivated();
	
    String getActivate();

    boolean isActivateDeclared();

    String getDeactivate();

    boolean isDeactivateDeclared();

    String getModified();

    String getConfigurationPolicy();

    public IServiceInfo getActiveService();
    
}
