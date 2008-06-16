package org.remotercp.ecf;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * This class is responsible for creating ECFUserIDs
 * 
 * @author eugrei
 * 
 */
public class ECFNamespace extends Namespace {

	/**
	 * generated Serial
	 */
	private static final long serialVersionUID = 78223162798660492L;

	private static final Logger logger = Logger.getLogger(ECFNamespace.class
			.getName());

	public ECFNamespace() {

	}

	public ECFNamespace(String name, String desc) {
		super(name, desc);
	}

	@Override
	public ID createInstance(Object[] parameters) throws IDCreateException {
		String username = (String) parameters[0];
		String host = (String) parameters[1];

		Assert.isNotNull(username);
		Assert.isNotNull(host);

		try {
			return new ECFUserID(this, username, host);
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, "Unable to create target ID", e);
		}
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

}
