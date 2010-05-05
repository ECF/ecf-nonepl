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

public class WaveletID extends BaseID {

	private static final long serialVersionUID = -6043054539024233450L;
	private int hashCode;
	private String waveletId;
	private String waveletDomain;

	protected WaveletID(WaveletNamespace ns, String waveletDomain, String waveletId) {
		super(ns);
		this.waveletDomain = waveletDomain;
		this.waveletId = waveletId;
		this.hashCode = hashCode();
	}

	protected int namespaceCompareTo(BaseID obj) {
		return getName().compareTo(obj.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (o == this) return true;
		if (!(o instanceof WaveletID))
			return false;
		WaveletID wo = (WaveletID) o;
		return wo.waveletId.equals(this.waveletId) && wo.waveletDomain.equals(this.waveletDomain);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((waveletDomain == null) ? 0 : waveletDomain.hashCode());
		result = prime * result	+ ((waveletId == null) ? 0 : waveletId.hashCode());
		return result;
	}

	protected String namespaceGetName() {
		return waveletDomain  + "!" + waveletId;
	}

	protected int namespaceHashCode() {
		return hashCode;
	}

	public String getWaveletId() {
		return waveletId;
	}

	public String getWaveletDomain() {
		return waveletDomain;
	}
}
