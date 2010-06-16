package org.remotercp.provisioning.domain.service;

import org.eclipse.core.runtime.IStatus;

public interface IAdministrationService {

	public void restartApplication();

	public boolean acceptUpdate();
}
