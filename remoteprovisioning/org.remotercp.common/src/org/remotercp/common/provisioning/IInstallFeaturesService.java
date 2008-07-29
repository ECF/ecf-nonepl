package org.remotercp.common.provisioning;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.update.core.IFeature;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;

public interface IInstallFeaturesService extends IRemoteServiceLauncher {

	public List<IStatus> installFeatures(IFeature[] features);

	public List<IStatus> updateFeautures(IFeature[] features);

	// public List<IStatus> uninstallFeatures(IFeature[] features);

	public List<IStatus> uninstallFeatures(String[] featureIds);

	public void restartApplication();

	public IStatus acceptUpdate();
}
