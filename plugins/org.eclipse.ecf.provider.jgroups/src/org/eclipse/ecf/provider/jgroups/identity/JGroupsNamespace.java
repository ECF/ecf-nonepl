/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.identity;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;

public class JGroupsNamespace extends Namespace {

	private static final long serialVersionUID = 1235788855435011811L;
	public static final String SCHEME = "jgroups";
	public static final String NAME = "ecf.namespace.jgroupsid";

	public JGroupsNamespace() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(Object[] parameters) throws IDCreateException {
		if (parameters != null) {
			if (parameters.length > 0) {
				if (parameters[0] instanceof String) {
					try {
						return new JGroupsID(this, new URI((String) parameters[0]));
					} catch (final URISyntaxException e) {
						throw new IDCreateException("invalid uri for creating JGroupsID", e);
					}
				}
			} else {
				return new JGroupsID(this, IDFactory.getDefault().createGUID().getName());
			}
		}
		throw new IDCreateException("invalid parameters for creating JGroupsID");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return SCHEME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypes()
	 */
	public Class[][] getSupportedParameterTypes() {
		return new Class[][] { {String.class}, {}};
	}

}
