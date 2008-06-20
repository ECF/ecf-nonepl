package org.remotercp.provisioning.bundleexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.remotercp.provisioning.ProvisioningActivator;

@Deprecated
public class BundleExplorerServiceImpl implements IBundleExplorerService {

	public Bundle[] getApplicationBundles() {
		BundleContext bundleContext = ProvisioningActivator.getDefault()
				.getBundle().getBundleContext();

		return bundleContext.getBundles();
	}

	public List<IFeature> getInstalledFeatures() {
		List<IFeature> installedFeatureList = new ArrayList<IFeature>();
		try {
			// this set will contain all installed features
			ILocalSite localSite = SiteManager.getLocalSite();

			IConfiguredSite[] sites = localSite.getCurrentConfiguration()
					.getConfiguredSites();

			for (IConfiguredSite site : sites) {
				for (IFeatureReference featureRef : site.getFeatureReferences()) {
					IFeature feature = featureRef.getFeature(null);
					installedFeatureList.add(feature);
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
		return installedFeatureList;
	}
}
