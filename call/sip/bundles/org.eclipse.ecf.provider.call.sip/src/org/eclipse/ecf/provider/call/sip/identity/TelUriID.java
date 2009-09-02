/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.call.sip.identity;

import gov.nist.javax.sip.address.TelURLImpl;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class TelUriID extends StringID{

	protected TelUriID(Namespace n, String s) {
		super(n, s);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5811566323629110604L;

	public TelUriID(TelURLImpl telUri) {
		super(IDFactory.getDefault().getNamespaceByName(
				TelUriNamespace.NAME), telUri.getPhoneNumber());
	}

	public TelUriID(String s) {
		this(IDFactory.getDefault().getNamespaceByName(
				TelUriNamespace.NAME), s);
	}

	public String getTelUrl() {
		return TelUriNamespace.SCHEME +TelUriNamespace.SCHEME_SEPARATOR + getUser();
	}

	public String getUser() {
		return getName();
	}
}
