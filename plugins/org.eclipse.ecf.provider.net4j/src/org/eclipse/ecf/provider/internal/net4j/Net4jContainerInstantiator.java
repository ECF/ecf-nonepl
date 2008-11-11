/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper (Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.ecf.provider.internal.net4j;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.net4j.INet4jConnectorProvider;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ManagedContainer;

/**
 * @author Eike Stepper
 */
public class Net4jContainerInstantiator extends BaseContainerInstantiator {
	@Override
	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			Net4jContainer container = null;

			if (parameters != null && parameters.length > 0) {
				if (parameters[0] instanceof ID) {
					container = new Net4jContainer((ID) parameters[0]);
				}

				if (parameters[0] instanceof String) {
					container = new Net4jContainer(IDFactory.getDefault()
							.createStringID((String) parameters[0]));
				}
			}

			if (container == null) {
				container = new Net4jContainer();
			}
			// Now setup
			container.setConnectorProvider(new INet4jConnectorProvider() {
				public IConnector getConnector(ID targetID,
						IConnectContext connectContext) {
					ManagedContainer container = new ManagedContainer();
					Net4jUtil.prepareContainer(container);
					TCPUtil.prepareContainer(container);
					return TCPUtil.getConnector(container, "localhost");
				}
			});

			return container;

		} catch (IDCreateException ex) {
			throw new ContainerCreateException(
					"Exception creating ID for net4j container", ex);
		}
	}

	@Override
	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return super.getSupportedAdapterTypes(description);
	}
}
