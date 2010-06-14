package org.remotercp.provisioning.domain.service;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.identity.ID;
import org.remotercp.provisioning.domain.exception.RemoteOperationException;
import org.remotercp.provisioning.domain.version.IVersionedId;

public interface IInstallFeaturesService {

	public IStatus restartApplication(ID adminId);

	public IVersionedId[] getInstalledFeatures(ID adminId) throws RemoteOperationException;

	public IStatus installFeature(IVersionedId featureId, URI[] repoLocations,
			ID adminId);

	public IStatus updateFeature(IVersionedId[] versionIds, URI[] repoLocations,
			ID adminId);

	public IStatus uninstallFeature(IVersionedId featureId, ID adminId);
}
