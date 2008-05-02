/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.aol.acc.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

/**
 *
 */
public class AIMNamespace extends Namespace {

	private static final long serialVersionUID = 2972159433535047690L;

	public static final String SCHEME = "aim";
	public static final String NAME = "ecf.namespace.aim";

	/**
	 * 
	 */
	public AIMNamespace() {
	}

	/**
	 * @param name
	 * @param desc
	 */
	public AIMNamespace(String name, String desc) {
		super(name, desc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			return new AIMID(this, (String) parameters[0]);
		} catch (final Exception e) {
			throw new IDCreateException("ID cannot be created", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return SCHEME;
	}

}
