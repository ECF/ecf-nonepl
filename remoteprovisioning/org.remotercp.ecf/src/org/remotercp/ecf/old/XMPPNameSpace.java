package org.remotercp.ecf.old;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPNamespace;

public class XMPPNameSpace extends Namespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1074344186092269164L;

	private static final Logger logger = Logger.getLogger(XMPPNamespace.class
			.getName());

	private static final String XMPP_PROTOCOL = "xmpp://";

	public XMPPNameSpace() {
		
		
	}

	public XMPPNameSpace(String name, String desc) {
		super(name, desc);
	}

	@Override
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			return new XMPPID(this, (String) parameters[0]);
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, "Unable to create XMPP ID", e);
		}
		return null;
	}

	@Override
	public String getScheme() {
		return XMPP_PROTOCOL;
	}

}
