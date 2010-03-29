/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.internal.provider.xmpp.XMPPContainerInstantiator;
import org.eclipse.ecf.provider.google.GoogleContainer;

public class GoogleContainerInstantiator extends XMPPContainerInstantiator {

	public GoogleContainerInstantiator() {
		super();
	}

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] args) throws ContainerCreateException {
		try {
			Integer ka = new Integer(GoogleContainer.DEFAULT_KEEPALIVE);
			String name = null;
			if (args != null) {
				if (args.length > 0) {
					name = (String) args[0];
					if (args.length > 1) {
						ka = getIntegerFromArg(args[1]);
					}
				}
			}
			if (name == null) {
				if (ka == null) {
					return new GoogleContainer();
				} else {
					return new GoogleContainer(ka.intValue());
				}
			} else {
				if (ka == null) {
					ka = new Integer(GoogleContainer.DEFAULT_KEEPALIVE);
				}
				return new GoogleContainer(name, ka.intValue());
			}
		} catch (Exception e) {
			throw new ContainerCreateException(
					"Exception creating generic container", e);
		}
	}
}
