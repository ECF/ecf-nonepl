/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar;

import java.util.Dictionary;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceContainerAdapterFactory;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

public class OSCARRemoteServiceAdapterFactory extends RemoteServiceContainerAdapterFactory {

	class OSCARRegistrySharedObject extends RegistrySharedObject {
		/* (non-Javadoc)
		 * @see org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject#getTargetsFromProperties(
		 * 			java.util.Dictionary)
		 */
		protected ID[] getTargetsFromProperties(Dictionary properties) {
			return super.getTargetsFromProperties(properties);
			/*if (properties == null) return null;
			List results = new ArrayList();
			Object o = properties.get(Constants.SERVICE_REGISTRATION_TARGETS);
			if (o != null) {
				if (o instanceof ID) results.add(o);
				if (o instanceof ID[]) {
					ID [] targets = (ID[]) o;
					for(int i=0; i < targets.length; i++) results.add(targets[i]);
				}
			}
			return (ID []) results.toArray(new ID[] {});*/
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#getLocalContainerID()
		 */
		protected ID getLocalContainerID() {
			// For OSCAR, the local container ID is its connected ID.
			return getContext().getConnectedID();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceContainerAdapterFactory#createAdapter(org.eclipse.ecf.core.sharedobject.ISharedObjectContainer, java.lang.Class, org.eclipse.ecf.core.identity.ID)
	 */
	protected ISharedObject createAdapter(ISharedObjectContainer container, Class adapterType, ID adapterID) {
		if (adapterType.equals(IRemoteServiceContainerAdapter.class)) {
			return new OSCARRegistrySharedObject();
		} else {
			return null;
		}
	}
}
