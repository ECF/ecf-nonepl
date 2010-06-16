package org.remotercp.provisioning.domain.service;


public interface IAdministrationService {

	public void restartApplication();

	public boolean acceptUpdate(boolean forceAsking);
}
