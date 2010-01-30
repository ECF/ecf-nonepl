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
package org.eclipse.ecf.provider.wave.google.identity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.waveprotocol.wave.model.id.WaveletId;

public class WaveletID extends BaseID {

	private static final long serialVersionUID = -6043054539024233450L;
	private WaveletId waveletId;
	private int hashCode;
	
	protected WaveletID(WaveletNamespace ns, WaveletId waveletId) {
		super(ns);
		Assert.isNotNull(waveletId);
		this.waveletId = waveletId;
		hashCode = 7;
		hashCode = 31 * hashCode + waveletId.hashCode();
	}
	
	protected WaveletID(WaveletNamespace ns, String waveletDomain, String waveletId) {
		super(ns);
		this.waveletId = new WaveletId(waveletDomain,waveletId);
	}
	
	public WaveletId getWaveletId() {
		return waveletId;
	}
	
	protected int namespaceCompareTo(BaseID obj) {
		return getName().compareTo(obj.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (o == this) return true;
		if (!(o instanceof WaveletID))
			return false;
		WaveletID wo = (WaveletID) o;
		return wo.waveletId.equals(this.waveletId);
	}

	protected String namespaceGetName() {
		return waveletId.getId();
	}

	protected int namespaceHashCode() {
		return hashCode;
	}

}
