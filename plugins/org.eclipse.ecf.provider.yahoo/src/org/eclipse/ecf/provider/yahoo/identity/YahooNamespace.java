/**
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	- Initial API and implementation
 *  	- Chris Aniszczyk <zx@us.ibm.com>
 *   	- Borna Safabakhsh <borna@us.ibm.com> 
 *   
 * $Id$
 */
package org.eclipse.ecf.provider.yahoo.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class YahooNamespace extends Namespace {
	
	private static final long serialVersionUID = 6211049515723995382L;
	private static final String YMSG_PROTOCOL = "ymsg://";
	
	public String getScheme() {
		return YMSG_PROTOCOL;
	}
	
	/**
	 * Creates an instance of an ID within this namespace given
	 * the arguments provided. In this case, the args is expected
	 * to include a single string argument representing the username
	 */
	public ID createInstance(Object[] args) throws IDCreateException {
		try {
			return new YahooID(this, (String) args[0]);
		} catch (Exception e) {
			throw new IDCreateException("Yahoo ID creation exception", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
	 */
	public Class[][] getSupportedParameterTypesForCreateInstance() {
		return new Class[][] { { String.class } };
	}
	
}
