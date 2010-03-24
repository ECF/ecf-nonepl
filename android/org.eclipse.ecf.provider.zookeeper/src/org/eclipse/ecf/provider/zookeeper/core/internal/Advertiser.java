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

import org.eclipse.ecf.provider.zookeeper.DiscoveryActivator;
import org.eclipse.ecf.provider.zookeeper.core.IDiscoveryConfig;
import org.eclipse.ecf.provider.zookeeper.node.internal.WatchManager;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public class Advertiser {

	private static Advertiser singleton;
	private ServiceTracker publicationTracker;
	private WatchManager watcher;

	private Advertiser(WatchManager watcher) {
		this.watcher = watcher;
		singleton = this;
	}

	public static Advertiser getSingleton(WatchManager watcher) {
		if (singleton == null)
			new Advertiser(watcher);
		return singleton;
	}

	public void autoPublish() {
		Filter filter = null;
		try {
			filter = DiscoveryActivator.getContext().createFilter(
					"(&(!(service.imported=*))" + "("//$NON-NLS-1$ //$NON-NLS-2$
							+ IDiscoveryConfig.ZOODISCOVERY_ADVERTISE_AUTO
							+ "=true))");//$NON-NLS-1$ 
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		this.publicationTracker = new ServiceTracker(DiscoveryActivator
				.getContext(), filter, null) {

			public Object addingService(ServiceReference reference) {
				getWather().publish(reference);
				return super.addingService(reference);
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
				getWather().update(reference);
				super.modifiedService(reference, service);
			}

			public void removedService(ServiceReference reference,
					Object service) {
				getWather().unpublish(
						reference.getProperty(Constants.SERVICE_ID).toString());
				super.removedService(reference, service);
			}
		};
		this.publicationTracker.open(true);
	}

	public void close() {
		if (this.publicationTracker != null) {
			this.publicationTracker.close();
		}
	}

	public WatchManager getWather() {
		return this.watcher;
	}

}
