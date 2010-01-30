package org.eclipse.ecf.provider.wave.google.identity;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;

public class WaveBackendID extends BaseID {

	private static final long serialVersionUID = 5842059732119395881L;

	private String userAtDomain;
	private String host;
	private int port;
	private int hashCode;
	
	public WaveBackendID(Namespace namespace, String userAtDomain, String host, int port) {
		super(namespace);
		this.userAtDomain = userAtDomain;
		this.host = host;
		this.port = port;
		hashCode = 7;
		hashCode = 31 * hashCode + userAtDomain.hashCode();
		hashCode = 31 * hashCode + host.hashCode();
		hashCode = 31 * hashCode + port;
	}

	public String getUserAtDomain() {
		return userAtDomain;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	protected int namespaceCompareTo(BaseID o) {
		if (!(o instanceof WaveBackendID)) return 0;
		return getName().compareTo(o.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (this == o) return true;
		if (this == null) return false;
		if (!(o instanceof WaveBackendID)) return false;
		WaveBackendID other = (WaveBackendID) o;
		return (userAtDomain.equals(other.getUserAtDomain()) && host.equals(other.getHost()) && port == other.getPort());
	}

	protected String namespaceGetName() {
		return userAtDomain + "@" + host + ":" + port;
	}

	protected int namespaceHashCode() {
		return hashCode;
	}

}
