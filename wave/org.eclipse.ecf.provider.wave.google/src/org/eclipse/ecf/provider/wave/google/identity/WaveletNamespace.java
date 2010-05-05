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
import org.eclipse.ecf.provider.internal.wave.google.CommonConstants;

public class WaveletNamespace extends Namespace {

	public static final String NAME = "ecf.namespace.googlewave.wavelet";
	public static final String SCHEME = "wavelet";
	
	private static final long serialVersionUID = 2615028840514406159L;

	public ID createInstance(Object[] objects) throws IDCreateException {
		try {
			if (objects == null || objects.length != 1 || objects[0] == null) {
				throw new IllegalArgumentException("Invalid WaveId creation arguments");
			}

			if(!(objects[0] instanceof String)) {
				throw new IllegalArgumentException("Invalid WaveId creation arguments");
			}
			
			String[] parameters = (String[]) objects;
			stripWaveProtocolPrefix(parameters);
			String waveletId = getWaveletId(parameters[0]);
			String waveletDomain = getWaveletDomain(parameters[0]);
			
			return new WaveletID(this, waveletDomain, waveletId);
		} catch (Exception e) {
			throw new IDCreateException("WaveletID creation failed", e);
		}
	}

	private String getWaveletId(String parameter) {
		String[] parts = parameter.split("/");
		if(parts.length == 1) {
			// test-wave.de!test
			String[] idParts = parts[0].split("!");
			return idParts[1];
		}
		
		return parts[parts.length - 1];
	}

	private String getWaveletDomain(String parameter) {
		String[] parts = parameter.split("/");
		if(parts.length == 1) {
			// test-wave.de!test
			String[] idParts = parts[0].split("!");
			return idParts[0];
		}

		return parts[0];
	}

	private void stripWaveProtocolPrefix(String[] parameters) {
		if(parameters[0].startsWith(CommonConstants.WAVE_PROTOCOL_PREFIX)) {
			parameters[0] = parameters[0].substring(CommonConstants.WAVE_PROTOCOL_PREFIX.length());
		}
	}

	public String getScheme() {
		return SCHEME;
	}

}
