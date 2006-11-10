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
package org.eclipse.ecf.provider.jms.identity;

import java.net.URI;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class JMSNamespace extends Namespace {
	private static final long serialVersionUID = 3761689000414884151L;

	private static final String SCHEME = "jms";

	public static final String NAME = "jms.activemq";

	public JMSNamespace() {
		super(NAME, null);
	}

	public ID createInstance(Object[] args) throws IDCreateException {
		try {
			if (args.length == 1) {
				if (args[0] instanceof String) {
					return new JMSID(this, (String) args[0]);
				} else if (args[0] instanceof URI) {
					return new JMSID(this, (URI) args[0]);
				}
			}
			throw new IllegalArgumentException(
					"XMPP ID constructor arguments invalid");
		} catch (Exception e) {
			throw new IDCreateException("XMPP ID creation exception", e);
		}
	}

	public String getScheme() {
		return SCHEME;
	}
}
