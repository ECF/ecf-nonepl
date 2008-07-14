package org.remotercp.common.provisioning;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.update.core.IFeature;

public interface IInstallFeaturesService {

	// public IStatus installFeature(String featureId, String version,
	// URL installSite);
	//
	// public IStatus updateFeature(String featureId, String version);
	//
	// public IStatus uninstallFeature(String featureId, String version);

	public List<IStatus>  installFeatures(IFeature[] features);

	public List<IStatus>  updateFeautures(IFeature[] features);

	public List<IStatus>  uninstallFeatures(IFeature[] features);

	public List<IStatus>  uninstallFeatures(String[] featuresIds);

	public void restartApplication();
}
