/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.oscar.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.oscar.Messages;

/**
 * The specific for OSCAR Namespace  
 *
 */
public class OSCARNamespace extends Namespace {

	private static final long serialVersionUID = 2294914977584738551L;
	
	private static final String OSCAR_PROTOCOL = "oscar";  //$NON-NLS-1$
	
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			final String init = getInitFromExternalForm(parameters);
			if (init != null)
				return new OSCARID(this, init);
			return new OSCARID(this, (String) parameters[0]);
		} catch (final Exception e) {
			throw new IDCreateException(Messages.OSCAR_NAMESPACE_EXCEPTION_ID_CREATE, e);
		}
	}

	public String getScheme() {	
		return OSCAR_PROTOCOL;
	}
		
	public Class[][] getSupportedParameterTypes() {
		return new Class[][] {{String.class}};
	}
	
	private String getInitFromExternalForm(Object[] parameters) {
		if (parameters == null || parameters.length < 1 || parameters[0] == null)
			return null;
		
		if (parameters[0] instanceof String) {
			final String arg = (String) parameters[0];
			if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
				final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
				if (index >= arg.length())
					return null;
				return arg.substring(index + 1);
			}
		}
		
		return null;
	}
}
