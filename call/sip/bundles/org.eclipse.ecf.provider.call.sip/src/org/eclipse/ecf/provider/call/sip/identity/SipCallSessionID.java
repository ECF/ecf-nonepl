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

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class SipCallSessionID extends StringID {

	protected SipCallSessionID(Namespace n, String s) {
		super(n, s);
	}
	

	public SipCallSessionID(String s) {
		this(IDFactory.getDefault().getNamespaceByName(
				SipCallSessionNamespace.NAME), s);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1198185266927833611L;

}
