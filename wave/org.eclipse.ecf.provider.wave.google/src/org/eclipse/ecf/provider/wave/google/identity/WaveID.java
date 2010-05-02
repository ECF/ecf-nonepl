/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.identity;

import org.eclipse.ecf.core.identity.BaseID;
import org.waveprotocol.wave.model.id.WaveId;

public class WaveID extends BaseID {

	private static final long serialVersionUID = -4591927740497040435L;

	private WaveId waveId;

	private String waveDomain;

	private String waveIdName;

	protected WaveID(WaveNamespace ns, String waveDomain, String waveIdName) {
		super(ns);
		this.waveId = new WaveId(waveDomain, waveIdName);
		this.waveDomain = waveDomain;
		this.waveIdName = waveIdName;
	}

	protected int namespaceCompareTo(BaseID obj) {
		return getName().compareTo(obj.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (o == this)
			return true;
		if (!(o instanceof WaveID))
			return false;
		WaveID wo = (WaveID) o;
		return wo.waveId.equals(this.waveId);
	}

	protected String namespaceGetName() {
		return waveIdName;
	}

	protected int namespaceHashCode() {
		return hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((waveDomain == null) ? 0 : waveDomain.hashCode());
		result = prime * result
				+ ((waveIdName == null) ? 0 : waveIdName.hashCode());
		return result;
	}

	public WaveId getWaveId() {
		return waveId;
	}

	public String getWaveDomain() {
		return waveDomain;
	}

	public String getWaveIdName() {
		return waveIdName;
	}

}
