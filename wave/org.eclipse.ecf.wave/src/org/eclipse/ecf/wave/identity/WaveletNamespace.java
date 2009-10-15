package org.eclipse.ecf.wave.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.waveprotocol.wave.model.id.LongIdSerialiser;

public class WaveletNamespace extends Namespace {

	public static final String NAME = "ecf.googlewave.wavelet.ns";
	public static final String SCHEME = "wavelet";
	
	private static final long serialVersionUID = 2615028840514406159L;

	public WaveletNamespace() {
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
			String init = getInitFromExternalForm(parameters);
			if (init != null)
				return new WaveletID(this, LongIdSerialiser.INSTANCE.deserialiseWaveletId(init));
			if (parameters.length == 1) {
				if (parameters[0] instanceof String) {
					return new WaveletID(this, LongIdSerialiser.INSTANCE.deserialiseWaveletId((String) parameters[0]));
				}
			}
			throw new IllegalArgumentException("Invalid WaveletID creation arguments");
		} catch (Exception e) {
			throw new IDCreateException("WaveletID creation failed", e);
		}
	}

	public String getScheme() {
		return SCHEME;
	}

}
