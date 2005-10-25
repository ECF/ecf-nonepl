package org.eclipse.ecf.provider.jms.identity;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;

public class JMSID extends BaseID {

	private static final long serialVersionUID = 3979266962767753264L;
    
	URI uri;
	
	protected JMSID(Namespace namespace, String url) throws URISyntaxException {
		super(namespace);
		this.uri = new URI(namespace.getName()+":"+url);
	}
	protected JMSID(Namespace namespace, URI uri) throws URISyntaxException {
		super(namespace);
		this.uri = new URI(namespace.getName()+":"+uri.toString());
	}
	protected int namespaceCompareTo(BaseID o) {
        return getName().compareTo(o.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof JMSID)) {
			return false;
		}
		JMSID other = (JMSID) o;
		return uri.equals(other.uri);
	}

	protected String namespaceGetName() {
		return uri.getSchemeSpecificPart();
	}

	protected int namespaceHashCode() {
		return uri.hashCode();
	}

	protected URI namespaceToURI() throws URISyntaxException {
		return uri;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("JMSID[");
		sb.append(uri.toString()).append("]");
		return sb.toString();
	}

	public URI getNameURI() {
		try {
			URI uri = toURI();
			return new URI(uri.getSchemeSpecificPart());
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
