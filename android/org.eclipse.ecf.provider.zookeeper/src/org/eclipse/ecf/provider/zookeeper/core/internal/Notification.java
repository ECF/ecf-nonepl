/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.net.URI;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.provider.zookeeper.core.DiscoverdService;
import org.eclipse.ecf.provider.zookeeper.core.DiscoveryContainer;
import org.eclipse.ecf.provider.zookeeper.core.DiscoveryContainer.FLAVOR;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public class Notification implements IServiceEvent {

	/**
	 * Notification indicating that a service has been discovered.
	 * <p>
	 * The value of <code>AVAILABLE</code> is 0x00000001.
	 */
	public final static int AVAILABLE = 0x00000001;

	/**
	 * Notification indicating that a previously discovered service is no longer
	 * known to ZooDiscovery.
	 * <p>
	 * The value of <code>UNAVAILABLE</code> is 0x00000002.
	 */
	public final static int UNAVAILABLE = 0x00000002;

	private int type;

	private DiscoverdService discoverdService;

	public Notification(DiscoverdService discoverdService, int type) {
		this.discoverdService = discoverdService;
		this.type = type;
		getLocalContainerID();
	}

	public int getType() {
		return this.type;
	}

	public DiscoverdService getAdvertisedService() {
		return discoverdService;
	}

	public IServiceInfo getServiceInfo() {
		return discoverdService;
	}

	public ID getLocalContainerID() {
		FLAVOR flavor = DiscoveryContainer.getSingleton().getConf().getFlavor();
		URI location = discoverdService.getLocation();
		ID id = DiscoveryContainer.getSingleton().getConnectNamespace()
				.createInstance(
						new String[] { flavor.toString() + "=" + location });//$NON-NLS-1$
		return id;
	}
}
