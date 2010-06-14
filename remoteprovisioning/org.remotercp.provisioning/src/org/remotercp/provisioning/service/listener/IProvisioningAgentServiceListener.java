package org.remotercp.provisioning.service.listener;

import org.eclipse.equinox.p2.core.IProvisioningAgent;

public interface IProvisioningAgentServiceListener {

	public void bindProvisioningAgent(IProvisioningAgent service);

	public void unbindProvisioningAgent(IProvisioningAgent service);
}
