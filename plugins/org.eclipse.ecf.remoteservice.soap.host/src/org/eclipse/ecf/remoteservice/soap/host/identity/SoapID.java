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

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

/**
 *@since 3.4
 */
public class SoapID extends StringID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3656862045346223983L;

	/**
	 * @param n
	 * @param s
	 */
	protected SoapID(Namespace n, String s) {
		super(n, s);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("SoapID["); //$NON-NLS-1$
		sb.append(getName()).append("]"); //$NON-NLS-1$
		return sb.toString();
	}


}
