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
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;

import com.skype.Profile;
import com.skype.Skype;

/**
 * 
 */
public class SkypeContainerInstantiator extends GenericContainerInstantiator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
		try {
			final Profile skypeProfile = Skype.getProfile();
			return new SkypeContainer(skypeProfile, skypeProfile.getId());
		} catch (final Exception e) {
			throw new ContainerCreateException(Messages.SkypeContainerInstantiator_EXCEPTION_COULD_NOT_CREATE_CONTAINER, e);
		}
	}

}
