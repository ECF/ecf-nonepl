package org.eclipse.ecf.mgmt.p2.install;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IVersionedId;

public interface IFeatureInstallManager {

	public IStatus applyConfiguration();

	public IVersionedId[] getInstalledFeatures();

	public IStatus installFeature(IVersionedId featureId, String profileId);

	public IStatus installFeature(IVersionedId featureId);

	public IStatus uninstallFeature(IVersionedId featureId, String profileId);

	public IStatus uninstallFeature(IVersionedId featureId);
}
