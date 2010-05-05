/*******************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.internal.wave.google.CommonConstants;
import org.eclipse.ecf.provider.wave.google.identity.WaveID;
import org.eclipse.ecf.provider.wave.google.identity.WaveletID;
import org.eclipse.ecf.wave.IWave;
import org.waveprotocol.wave.federation.Proto.ProtocolHashedVersion;

public class Wave implements IWave {

	private final WaveID waveId;

	private final Map<WaveletID, Wavelet> wavelets = new HashMap<WaveletID, Wavelet>();

	public Wave(WaveID waveId) {
		this.waveId = waveId;
	}

	public Wavelet[] getWavelets() {
		Wavelet[] result = new Wavelet[wavelets.size()];
		Iterator<Entry<WaveletID, Wavelet>> it = wavelets.entrySet().iterator();

		int i = 0;
		while (it.hasNext()) {
			Map.Entry<WaveletID, Wavelet> wavelet = it.next();
			result[i] = wavelet.getValue();
			i++;
		}

		return result;
	}

	@Override
	public ID getID() {
		return waveId;
	}

	public Wavelet createWavelet(WaveletID waveletId) {
		Wavelet wavelet = new Wavelet(waveletId);
		wavelets.put(waveletId, wavelet);

		return wavelet;
	}

	public Wavelet getWavelet(WaveletID waveletId) {
		return wavelets.get(waveletId);
	}

	public void setWaveletVersion(WaveletID waveletId, ProtocolHashedVersion protocolHashedVersion) {
		Wavelet wavelet = getWavelet(waveletId);
		if (wavelet == null) {
			throw new IllegalArgumentException("");
		}

		// TODO: implement
	}

	public void removeWavelet(WaveletID waveletId) {
		wavelets.remove(waveletId);
	}

	public boolean isIndexWave() {
		return waveId.equals(CommonConstants.INDEX_WAVE_ID);
	}
}
