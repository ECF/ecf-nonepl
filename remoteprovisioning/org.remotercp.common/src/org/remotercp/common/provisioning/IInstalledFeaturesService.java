package org.remotercp.common.provisioning;

import java.util.Collection;

import org.eclipse.ecf.core.identity.ID;

public interface IInstalledFeaturesService {

	/**
	 * Returns an array with installed bundles in the users rcp application.
	 * 
	 * @return
	 */
	public Collection<SerializedBundleWrapper> getInstalledBundles();

	public String getUserInfo();

	/**
	 * Returns the remote user {@link ID}
	 * 
	 * @return
	 */
	public ID getUserID();

}
