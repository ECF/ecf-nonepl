/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class WaveNamespace extends Namespace {

	public static final String NAME = "ecf.namespace.googlewave.wave";
	public static final String SCHEME = "wave";
	
	private static final long serialVersionUID = 2615028840514406159L;

	public WaveNamespace() {
		super(NAME,null);
	}
	
	private String getInitFromExternalForm(Object[] args) {
		if (args == null || args.length < 1 || args[0] == null)
			return null;
		if (args[0] instanceof String) {
			String arg = (String) args[0];
			if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
				int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
				if (index >= arg.length())
					return null;
				return arg.substring(index + 1);
			}
		}
		return null;
	}


	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			@SuppressWarnings("unused")
			String init = getInitFromExternalForm(parameters);
			
			throw new IllegalArgumentException("Invalid WaveId creation arguments");
		} catch (Exception e) {
			throw new IDCreateException("WaveID creation failed", e);
		}
	}

	public String getScheme() {
		return SCHEME;
	}

}