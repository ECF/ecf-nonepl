/****************************************************************************
 * Copyright (c) 2009 Composent, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Scott Lewis <slewis@composent.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.wave.identity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.waveprotocol.wave.model.id.WaveId;

public class WaveID extends BaseID {

	private static final long serialVersionUID = -4591927740497040435L;

	private WaveId waveId;
	private int hashCode;
	
	protected WaveID(WaveNamespace ns, WaveId waveId) {
		super(ns);
		Assert.isNotNull(waveId);
		this.waveId = waveId;
		hashCode = 7;
		hashCode = 31 * hashCode + waveId.hashCode();
	}
	
	protected WaveID(WaveNamespace ns, String waveDomain, String waveId) {
		super(ns);
		this.waveId = new WaveId(waveDomain,waveId);
	}
	
	public WaveId getWaveId() {
		return waveId;
	}
	
	protected int namespaceCompareTo(BaseID obj) {
		return getName().compareTo(obj.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (o == this) return true;
		if (!(o instanceof WaveID))
			return false;
		WaveID wo = (WaveID) o;
		return wo.waveId.equals(this.waveId);
	}

	protected String namespaceGetName() {
		return waveId.getId();
	}

	protected int namespaceHashCode() {
		return hashCode;
	}

}
