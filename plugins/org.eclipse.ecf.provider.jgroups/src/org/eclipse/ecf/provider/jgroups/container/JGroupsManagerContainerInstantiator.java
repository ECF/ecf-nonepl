/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.container;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsNamespace;

/**
 *
 */
public class JGroupsManagerContainerInstantiator extends BaseContainerInstantiator {

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
		try {
			final ID newID = IDFactory.getDefault().createID(IDFactory.getDefault().getNamespaceByName(JGroupsNamespace.NAME), (String) parameters[0]);
			final JGroupsManagerContainer manager = new JGroupsManagerContainer(new SOContainerConfig(newID));
			manager.start();
			return manager;
		} catch (final Exception e) {
			throw new ContainerCreateException("Exception creating trivial container", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
		// TODO Return String [] with adapter types supported for the given description
		return super.getSupportedAdapterTypes(description);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
		// TODO Auto-generated method stub
		return super.getSupportedParameterTypes(description);
	}
}
