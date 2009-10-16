package org.eclipse.ecf.wave.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class WaveBackendNamespace extends Namespace {

	private static final long serialVersionUID = 5628390764537972030L;
	public static final String SCHEME = "wavebackend";
	public static final String NAME = "ecf.googlewave.wavebackend.ns";
	
	public ID createInstance(Object[] parameters) throws IDCreateException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getScheme() {
		return SCHEME;
	}

}
