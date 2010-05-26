package org.eclipse.ecf.remoteservice.soap.host;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;

public interface ISoapServerContainerAdapter extends IAdaptable{

	/**
	 * Get the remote service and publish a web service access to it
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service name. Must not be <code>null</code>.
	 * @param allowedMethod
	 *            . The exposed methods ex.: *. Must not be <code>null</code>.
	 * @param remoteServiceContainer Must not be <code>null</code>.
	 * @param targetID Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void deployRemoteServiceAsWebService(String clazz,String allowedMethod, IRemoteServiceContainer remoteServiceContainer, ID targetID) throws ECFException;

	/**
	 * Get the remote service and remove the web service access to it
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service name. Must not be <code>null</code>.
	 * @param remoteServiceContainer Must not be <code>null</code>.
	 * @param targetID Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void undeployRemoteServiceAsWebService(String clazz, IRemoteServiceContainer remoteServiceContainer, ID targetID)
			throws ECFException;

}
