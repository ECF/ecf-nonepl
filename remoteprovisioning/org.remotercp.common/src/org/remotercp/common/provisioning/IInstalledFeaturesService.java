package org.remotercp.common.provisioning;

import java.util.Collection;

import org.eclipse.update.core.IFeature;

public interface IInstalledFeaturesService {

	public Collection<IFeature> getInstalledFeatures();

	/**
	 * Returns an array with installed bundles in the users rcp application.
	 * 
	 * @return
	 */
	public Collection<SerializedBundleWrapper> getInstalledBundles();

	public String getUserInfo();

	public String getInstalledBundlesAsXML();

}
