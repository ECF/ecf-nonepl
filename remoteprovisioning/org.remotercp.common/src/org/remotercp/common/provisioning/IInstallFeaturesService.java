package org.remotercp.common.provisioning;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.identity.ID;

/**
 * @deprecated Functionality moved to bundle org.remotercp.provisioning.service
 * @author ereiswich
 *
 */
@Deprecated
public interface IInstallFeaturesService  {

	public List<IStatus> installFeatures(SerializedFeatureWrapper[] features,
			ID fromId);

	public List<IStatus> updateFeautures(SerializedFeatureWrapper[] features,
			ID fromId);

	public List<IStatus> uninstallFeatures(String[] featureIds, ID fromId);

	public List<IStatus> restartApplication(ID fromId);

	public IStatus acceptUpdate(ID fromId);
}
