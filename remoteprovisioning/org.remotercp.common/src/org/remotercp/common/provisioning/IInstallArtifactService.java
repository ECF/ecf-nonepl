package org.remotercp.common.provisioning;

import java.net.URL;

public interface IInstallArtifactService {

	public void installArtifact(String artifactID, URL installSite);

	public void updateArtifact(String artifactID);

	public void uninstallArtifact(String artifactID);
}
