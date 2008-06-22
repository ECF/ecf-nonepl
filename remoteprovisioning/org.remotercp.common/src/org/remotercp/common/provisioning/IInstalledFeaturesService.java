package org.remotercp.common.provisioning;

import java.util.Collection;

import org.eclipse.ecf.core.identity.ID;

public interface IInstalledFeaturesService {

	// public Collection<IFeature> getInstalledFeatures();

	/**
	 * Returns an array with installed bundles in the users rcp application.
	 * 
	 * @return
	 */
	public Collection<SerializedBundleWrapper> getInstalledBundles();

	public String getUserInfo();

	public ID getUserID();

	// public String getInstalledBundlesAsXML();

}
