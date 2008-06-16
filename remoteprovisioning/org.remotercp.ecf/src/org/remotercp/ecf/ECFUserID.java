package org.remotercp.ecf;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;

public class ECFUserID extends BaseID {

	/**
	 * generated Serial
	 */
	private static final long serialVersionUID = 2765421689094484794L;

	private String username;

	private String connectionData;

	public ECFUserID(Namespace namespace, String username, String server)
			throws URISyntaxException {
		super(namespace);
		this.username = username;
		// this.connectionData = username + "@" + server + ":5222";
		this.connectionData = username + "@" + server;
	}

	@Override
	protected int namespaceCompareTo(BaseID o) {
		return getName().compareTo(o.getName());
	}

	@Override
	protected boolean namespaceEquals(BaseID o) {

		if (o instanceof ECFUserID) {
			return true;
		}
		// return uri.equals(((ECFUserID) o).uri);
		return false;
	}

	@Override
	protected String namespaceGetName() {
		return username;
	}

	@Override
	protected int namespaceHashCode() {
		return this.connectionData.hashCode();
	}

	public String getConnectionData() {
		return connectionData;
	}
}
