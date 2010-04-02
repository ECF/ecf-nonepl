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

import javax.sip.address.SipURI;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;



public class SipUriID extends StringID{

	
	protected SipUriID(Namespace n, String s) {
		super(n, s);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4812062237023116543L;
	
	public SipUriID(SipURI sipUri) {
		super(IDFactory.getDefault().getNamespaceByName(
				SipUriNamespace.NAME), sipUri.toString());
	}

	public SipUriID(String s) {
		this(IDFactory.getDefault().getNamespaceByName(
				SipUriNamespace.NAME), s);
	}

	public String getSIPUrl() {
		return  getUser();
	}

	public String getUser() {
		return getName();
	}

}
