package org.remotercp.provisioning.bundleexplorer;

import java.util.List;

import org.eclipse.update.core.IFeature;
import org.osgi.framework.Bundle;
import org.remotercp.provisioning.update.features.IInstalledFeaturesService;

/**
 * Use {@link IInstalledFeaturesService} instead
 * 
 * @author eugrei
 * 
 */
@Deprecated
public interface IBundleExplorerService {

	public Bundle[] getApplicationBundles();

	public List<IFeature> getInstalledFeatures();

}
