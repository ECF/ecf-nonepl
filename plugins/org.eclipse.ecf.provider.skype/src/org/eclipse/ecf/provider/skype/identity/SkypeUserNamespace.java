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

package org.eclipse.ecf.provider.skype.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class SkypeUserNamespace extends Namespace {

	public static final String SCHEME = "ecf.namespace.skype.user"; //$NON-NLS-1$
	public static final String NAMESPACE_NAME = SCHEME;

	private static final long serialVersionUID = -7848190137834330244L;

	private String getInitFromExternalForm(Object[] args) {
		if (args == null || args.length < 1 || args[0] == null)
			return null;
		if (args[0] instanceof String) {
			final String arg = (String) args[0];
			if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
				final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
				if (index >= arg.length())
					return null;
				return arg.substring(index + 1);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			final String init = getInitFromExternalForm(parameters);
			if (init != null)
				return new SkypeUserID(this, init);
			return new SkypeUserID(this, (String) parameters[0]);
		} catch (final Exception e) {
			throw new IDCreateException(NLS.bind("{0} createInstance()", getName()), e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return SCHEME;
	}

	public String getCallParty() {
		return getName();
	}
}
