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
package org.eclipse.ecf.provider.zookeeper.core;

import java.net.URI;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.osgi.util.NLS;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public class DiscoveryNamespace extends Namespace {

	private static final long serialVersionUID = 3925693055869405334L;
	public static final String NAME = "ecf.namespace.zookeeperdiscovery"; //$NON-NLS-1$	

	public DiscoveryNamespace() {
		super(NAME, "ZooKeeper Based Discovery Namespace"); //$NON-NLS-1$
	}

	public ID createInstance(Object[] parameters) throws IDCreateException {
		Assert.isTrue(parameters != null && parameters.length > 0);
		try {
			if (parameters[0] instanceof String) {
				return new ZooTargetID(this, (String[]) parameters);
			} else if (parameters.length == 1
					&& parameters[0] instanceof IServiceTypeID) {
				return new ZooServiceTypeID(this, (IServiceTypeID) parameters[0]);
			} else if (parameters.length == 2
					&& parameters[0] instanceof IServiceTypeID
					&& parameters[1] instanceof URI) {
				return new ZooServiceID(this, ((IServiceTypeID) parameters[0]),
						((URI) parameters[1]));
			}

		} catch (Exception e) {
			throw new IDCreateException(NLS.bind(
					"{0} createInstance()", getName()), e); //$NON-NLS-1$
		}
		return null;

	}

	public String getScheme() {
		return this.getClass().toString();
	}

	public Class[][] getSupportedParameterTypes() {
		return new Class[][] { { String.class }, { IServiceTypeID.class },
				{ IServiceTypeID.class, URI.class } };
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
