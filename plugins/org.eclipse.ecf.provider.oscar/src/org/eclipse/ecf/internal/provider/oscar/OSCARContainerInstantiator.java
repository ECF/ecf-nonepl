/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar;

import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;
import org.eclipse.ecf.provider.oscar.OSCARContainer;

public class OSCARContainerInstantiator extends GenericContainerInstantiator {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(ContainerDescription, java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description, Object[] args) throws ContainerCreateException {
		try {
			String name = null;
			String host = null;
			Integer port = null;
			if (args != null) {
				if (args.length > 0) {
					name = (String) args[0];
					if (args.length > 1) {
						host = (String) args[1];
						if (args.length > 2)
							port = (Integer) args[2];
					}
				}
			}

			if (name == null) {
				return new OSCARContainer();
			} else {
				if (host == null)
					return new OSCARContainer(name);
				else if (port == null)
					return new OSCARContainer(name, host);
				else
					return new OSCARContainer(name, host, port.intValue());
			}
		} catch (Exception e) {
			throw new ContainerCreateException("Exception creating OSCAR container", e);
		}
	}

	private static final String OSCAR_CONFIG = "ecf.oscar.icqlib"; //$NON-NLS-1$

	public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
		if (exporterSupportedConfigs == null)
			return null;

		List results = new ArrayList();
		List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
		if (OSCAR_CONFIG.equals(description.getName())) {
			if (supportedConfigs.contains(OSCAR_CONFIG))
				results.add(OSCAR_CONFIG);
		}

		if (supportedConfigs.size() == 0)
			return null;

		return (String[]) results.toArray(new String[] {});
	}

	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return new String[] {OSCAR_CONFIG};
	}
}
