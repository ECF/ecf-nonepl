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
package org.eclipse.ecf.provider.google.identity;

import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.internal.provider.xmpp.Messages;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPNamespace;

public class GoogleNamespace extends XMPPNamespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9004188879157998163L;
	/**
	 * 
	 */
	// private static final long serialVersionUID = 497951988695693846L;
	private static final String GTALK_SCHEME = "google";

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

	public ID createInstance(Object[] args) throws IDCreateException {
		try {
			final String init = getInitFromExternalForm(args);
			if (init != null)
				return new XMPPID(this, init);
			return new XMPPID(this, (String) args[0]);
		} catch (final Exception e) {
			throw new IDCreateException(
					Messages.XMPPNamespace_EXCEPTION_ID_CREATE, e);
		}
	}

	/*
	 * public ID createInstance(Object[] parameters) throws IDCreateException {
	 * return super.createInstance(parameters); }
	 */
	public String getScheme() {
		return GTALK_SCHEME;

	}

}
