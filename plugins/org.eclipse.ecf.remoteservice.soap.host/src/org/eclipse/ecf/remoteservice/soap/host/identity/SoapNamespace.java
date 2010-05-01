/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.remoteservice.soap.host.identity;

import java.net.URI;
import java.net.URL;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

/**
 *@since 3.4
 */
public class SoapNamespace extends Namespace {

	private static final long serialVersionUID = 1235788855435011811L;
	public static final String SCHEME = "soap";
	public static final String NAME = "ecf.soap.ws.namespace";

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(Object[] parameters) throws IDCreateException {
		if (parameters == null || parameters.length < 1)
			throw new IDCreateException("parameters not of correct size");
		if (!(parameters[0] instanceof String))
			throw new IDCreateException("parameter not of String type");
		return new SoapID(this, (String) parameters[0]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return SCHEME;
	}

	public Class[][] getSupportedParameterTypes() {
		return new Class[][] { {ID.class}, {URI.class}, {String.class}, {URL.class}};
	}
}
