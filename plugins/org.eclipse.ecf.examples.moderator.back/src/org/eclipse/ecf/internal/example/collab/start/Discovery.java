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
package org.eclipse.ecf.internal.example.collab.start;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.equinox.concurrent.future.IFuture;

public class Discovery implements IDiscoveryLocator, IDiscoveryAdvertiser {

	IContainer container = null;
	IDiscoveryContainerAdapter discoveryContainer = null;

	public Discovery() throws Exception {
		startDiscovery();
	}

	private void startDiscovery() throws Exception {
		container = ContainerFactory.getDefault().createContainer("ecf.discovery.jmdns"); //$NON-NLS-1$
		container.connect(null, null);
		discoveryContainer = (IDiscoveryContainerAdapter) container.getAdapter(IDiscoveryContainerAdapter.class);
		discoveryContainer.addServiceTypeListener(new CollabServiceTypeListener());
	}

	class CollabServiceTypeListener implements IServiceTypeListener {
		/* (non-Javadoc)
		 * @see org.eclipse.ecf.discovery.IServiceTypeListener#serviceTypeDiscovered(org.eclipse.ecf.discovery.IServiceTypeEvent)
		 */
		public void serviceTypeDiscovered(IServiceTypeEvent event) {
			discoveryContainer.addServiceListener(event.getServiceTypeID(), new CollabServiceListener());
		}
	}

	class CollabServiceListener implements IServiceListener {
		/* (non-Javadoc)
		 * @see org.eclipse.ecf.discovery.IServiceListener#serviceDiscovered(org.eclipse.ecf.discovery.IServiceEvent)
		 */
		public void serviceDiscovered(IServiceEvent anEvent) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ecf.discovery.IServiceListener#serviceUndiscovered(org.eclipse.ecf.discovery.IServiceEvent)
		 */
		public void serviceUndiscovered(IServiceEvent anEvent) {
			// TODO Auto-generated method stub

		}
	}

	public void addServiceListener(IServiceListener listener) {
		// TODO Auto-generated method stub

	}

	public void addServiceListener(IServiceTypeID type, IServiceListener listener) {
		// TODO Auto-generated method stub

	}

	public void addServiceTypeListener(IServiceTypeListener listener) {
		// TODO Auto-generated method stub

	}

	public IFuture getAsyncServiceInfo(IServiceID aServiceID) {
		// TODO Auto-generated method stub
		return null;
	}

	public IFuture getAsyncServiceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFuture getAsyncServices() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFuture getAsyncServices(IServiceTypeID aServiceTypeID) {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceInfo getServiceInfo(IServiceID aServiceID) {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceTypeID[] getServiceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceInfo[] getServices() {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceInfo[] getServices(IServiceTypeID aServiceTypeID) {
		// TODO Auto-generated method stub
		return null;
	}

	public Namespace getServicesNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceInfo[] purgeCache() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeServiceListener(IServiceListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeServiceListener(IServiceTypeID type, IServiceListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeServiceTypeListener(IServiceTypeListener listener) {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerService(IServiceInfo serviceInfo) {
		// TODO Auto-generated method stub

	}

	public void unregisterAllServices() {
		// TODO Auto-generated method stub

	}

	public void unregisterService(IServiceInfo serviceInfo) {
		// TODO Auto-generated method stub

	}
}
