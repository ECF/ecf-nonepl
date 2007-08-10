/****************************************************************************
 * Copyright (c) 2004 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jms.identity;

import java.net.URI;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.jms.Messages;

public class JMSNamespace extends Namespace {
	private static final long serialVersionUID = 3761689000414884151L;

	private static final String SCHEME = "jms"; //$NON-NLS-1$

	public static final String NAME = "ecf.namespace.jmsid"; //$NON-NLS-1$

	public JMSNamespace() {
		super(NAME, null);
	}

	public ID createInstance(Object[] args) throws IDCreateException {
		try {
			if (args.length == 1) {
				if (args[0] instanceof String) {
					return new JMSID(this, (String) args[0]);
				} else if (args[0] instanceof URI) {
					return new JMSID(this, ((URI) args[0]).toString());
				}
			}
			throw new IllegalArgumentException(
					Messages.JMSNamespace_EXCEPTION_XMPP_ARGS_INVALID);
		} catch (Exception e) {
			throw new IDCreateException(
					Messages.JMSNamespace_EXCEPTION_IDCREATION, e);
		}
	}

	public String getScheme() {
		return SCHEME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
	 */
	public Class[][] getSupportedParameterTypes() {
		return new Class[][] { { String.class }, { URI.class } };
	}
}
