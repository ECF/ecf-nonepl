package org.remotercp.common.provisioning;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;

public interface IInstallFeaturesService extends IRemoteServiceLauncher {

	public List<IStatus> installFeatures(SerializedFeatureWrapper[] features);

	public List<IStatus> updateFeautures(SerializedFeatureWrapper[] features);

	// public List<IStatus> uninstallFeatures(IFeature[] features);

	public List<IStatus> uninstallFeatures(String[] featureIds);

	public void restartApplication();

	public IStatus acceptUpdate();
}
