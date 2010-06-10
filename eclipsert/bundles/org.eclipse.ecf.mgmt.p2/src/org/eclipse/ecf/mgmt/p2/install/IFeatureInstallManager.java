package org.eclipse.ecf.mgmt.p2.install;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IVersionedId;

public interface IFeatureInstallManager {

	public IStatus applyConfiguration();

	public IVersionedId[] getInstalledFeatures(String profileId);
	
	public IVersionedId[] getInstalledFeatures();

	public IVersionedId[] getInstallableFeatures(URI location);
	
	public IVersionedId[] getInstallableFeatures();
	
	public IStatus installFeature(IVersionedId featureId, URL[] repoLocations, String profileId);
	public IStatus installFeature(IVersionedId featureId, URL[] repoLocations);
	public IStatus installFeature(IVersionedId featureId, String profileId);
	public IStatus installFeature(IVersionedId featureId);

	public IStatus updateFeature(IVersionedId featureId, URL[] repoLocations, String profileId);
	public IStatus updateFeature(IVersionedId featureId, URL[] repoLocations);
	public IStatus updateFeature(IVersionedId featureId, String profileId);
	public IStatus updateFeature(IVersionedId featureId);

	public IStatus uninstallFeature(IVersionedId featureId, URL[] repoLocations, String profileId);
	public IStatus uninstallFeature(IVersionedId featureId, URL[] repoLocations);
	public IStatus uninstallFeature(IVersionedId featureId, String profileId);
	public IStatus uninstallFeature(IVersionedId featureId);
}
