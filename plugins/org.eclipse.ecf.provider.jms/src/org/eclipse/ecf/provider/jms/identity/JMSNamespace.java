package org.eclipse.ecf.provider.jms.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.jms.JmsPlugin;

public class JMSNamespace extends Namespace {
	private static final long serialVersionUID = 3761689000414884151L;
	private static final String JMS_SCHEME = "jms";
	
	public static final String JMS_NAMESPACE_NAME = JmsPlugin.NAMESPACE_NAME;
	
	public JMSNamespace() {
		super(JMS_NAMESPACE_NAME,null);
	}
	public ID createInstance(Object[] args)
			throws IDCreateException {
		try {
			if (args.length == 1) {
				if (args[0] instanceof String) {
					return new JMSID(this, (String) args[0]);
				} else if (args[0] instanceof URI) {
					return new JMSID(this, (URI) args[0]);
				}
			}
			throw new IllegalArgumentException(
					"XMPP ID constructor arguments invalid");
		} catch (Exception e) {
			throw new IDCreateException("XMPP ID creation exception", e);
		}
	}

	public String getScheme() {
		return JMS_SCHEME;
	}
}
