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
		super(NAME, null);
	}
	
	public ID createInstance(Object[] objects) throws IDCreateException {
		try {
			if (objects == null || objects.length < 1 || objects[0] == null) {
				throw new IllegalArgumentException("Invalid WaveId creation arguments");
			}

			if(!(objects[0] instanceof String) || (objects.length == 2 && !(objects[1] instanceof String))) {
				throw new IllegalArgumentException("Invalid WaveId creation arguments");
			}
			
			// TODO: parameter checks
			String[] parameters = (String[]) objects;
			String waveIdName = getWaveIdName(parameters);
			String waveDomain = getWaveDomain(parameters);
			
			return new WaveID(this, waveDomain, waveIdName);
		} catch (Exception e) {
			throw new IDCreateException("WaveID creation failed", e);
		}
	}

	private String getWaveIdName(String[] parameters) {
		if(parameters.length == 2) {
			return parameters[1];
		} else {
			return parameters[0].substring(parameters[0].indexOf("!") + 1);
		}

	}

	private String getWaveDomain(String[] parameters) {
		if(parameters.length == 2) {
			return parameters[0];
		} else {
			return parameters[0].substring(0, parameters[0].indexOf("!"));
		}
	}

	public String getScheme() {
		return SCHEME;
	}

}
