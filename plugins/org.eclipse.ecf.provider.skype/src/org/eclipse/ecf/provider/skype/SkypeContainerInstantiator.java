/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;

import com.skype.Profile;
import com.skype.Skype;

/**
 * 
 */
public class SkypeContainerInstantiator implements IContainerInstantiator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			Profile skypeProfile = Skype.getProfile();
			return new SkypeContainer(skypeProfile, skypeProfile.getId());
		} catch (Exception e) {
			throw new ContainerCreateException(
					"Could not create skype container", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return new String[] { IPresenceContainerAdapter.class.getName(),
				ICallSessionContainerAdapter.class.getName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		return null;
	}

}