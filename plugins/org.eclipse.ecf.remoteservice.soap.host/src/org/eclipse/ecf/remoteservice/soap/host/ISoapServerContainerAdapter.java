/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.host;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

/**
 * Create a soap container adapter which is responsible to publish remote service as WS
 * @since 3.4
 *
 */
public interface ISoapServerContainerAdapter extends IAdaptable {

	/**
	 * Get the remote service and publish a web service access to it
	 * 
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service
	 *            name. Must not be <code>null</code>.
	 * @param allowedMethod
	 *            . The exposed methods ex.: *. Must not be <code>null</code>.
	 * @param remoteServiceContainerAdapter
	 *            Must not be <code>null</code>.
	 * @param targetID
	 *            the ID of the target. Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void deployRemoteServiceAsWebService(String clazz,
			String allowedMethod,
			IRemoteServiceContainerAdapter remoteServiceContainerAdapter,
			ID targetID) throws ECFException;

	/**
	 * Get the remote service and remove the web service access to it
	 * 
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service
	 *            name. Must not be <code>null</code>.
	 * @param remoteServiceContainer
	 *            . Must not be <code>null</code>.
	 * @param targetID
	 *            the ID of the target. Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void undeployRemoteServiceAsWebService(String clazz,
			IRemoteServiceContainer remoteServiceContainer, ID targetID)
			throws ECFException;

	/**
	 * Get the remote service and publish a web service access to it
	 * 
	 * @param serviceName
	 *            . It will be the web service name. Must not be
	 *            <code>null</code>.
	 * @param allowedMethod
	 *            . The exposed methods ex.: *. Must not be <code>null</code>.
	 * @param remoteService
	 *            . Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void deployRemoteServiceAsWebService(String serviceName,
			String allowedMethod, IRemoteService remoteService)
			throws ECFException;

	/**
	 * Get the remote service and remove the web service access to it
	 * 
	 * @param serviceName
	 *            . describes the desired service. It will be the web service
	 *            name. Must not be <code>null</code>.
	 * @param remoteService
	 *            . Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void undeployRemoteServiceAsWebService(String serviceName,
			IRemoteService remoteService) throws ECFException;

}
