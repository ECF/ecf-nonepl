package org.remotercp.ecf.old;

import java.net.URI;

import org.eclipse.ecf.core.identity.BaseID;

public class XMPPID extends BaseID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1676665134929338709L;
	private String userName;
	private URI uri;

	@Override
	protected int namespaceCompareTo(BaseID o) {
		return getName().compareTo(o.getName());
	}

	@Override
	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof XMPPID)) {
			return false;
		}
		return uri.equals(((XMPPID) o).uri);
	}

	@Override
	protected String namespaceGetName() {
		return userName;
	}

	@Override
	protected int namespaceHashCode() {
		return uri.hashCode();
	}

}
