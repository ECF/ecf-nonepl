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

package org.eclipse.ecf.provider.skype.identity;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

import com.skype.User;

public class SkypeUserID extends StringID {

	private static final long serialVersionUID = 5023965255941333692L;

	public SkypeUserID(User skypeUser) {
		super(IDFactory.getDefault().getNamespaceByName(
				SkypeUserNamespace.NAMESPACE_NAME), skypeUser.getId());
	}

	public SkypeUserID(String s) {
		this(IDFactory.getDefault().getNamespaceByName(
				SkypeUserNamespace.NAMESPACE_NAME), s);
	}

	/**
	 * @param n
	 * @param s
	 */
	public SkypeUserID(Namespace n, String s) {
		super(n, s);
	}

	public String getUser() {
		return getName();
	}
}
