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

import org.apache.axis.AxisFault;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.remoteservice.soap.host.Activator;
import org.eclipse.ecf.internal.remoteservice.soap.host.SOAPServiceTracker;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Create a soap container adapter which is responsible to publish remote service as WS
 * 
 * @since 3.4
 * 
 */
public class SoapServerContainerAdapter implements ISoapServerContainerAdapter{

	SOAPServiceTracker serviceTracker;

	public SoapServerContainerAdapter() {
		serviceTracker = Activator.getDefault().getServiceTracker();

	}

	/**
	 * Get the remote service and publish a web service access to it
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service name. Must not be <code>null</code>.
	 * @param rosgiServiceHost
	 *            . Must not be <code>null</code>.
	 * @param allowedMethod
	 *            . The exposed methods ex.: *. Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void deployRemoteServiceAsWebService(String clazz, String allowedMethod, IRemoteServiceContainerAdapter remoteServiceContainerAdapter, ID targetID) throws ECFException {
		try {
			Assert.isNotNull(clazz);
			Assert.isNotNull(allowedMethod);
			Assert.isNotNull(remoteServiceContainerAdapter);
			Assert.isNotNull(targetID);
			
			// Lookup IRemoteServiceReference
			IRemoteServiceReference[] serviceReferences = remoteServiceContainerAdapter.getRemoteServiceReferences(targetID, clazz, null);

			// Get remote service for reference
			if(serviceReferences == null ||serviceReferences.length == 0)
				throw new ECFException("The remote reference is not available : "+clazz);
			
			IRemoteServiceReference reference = serviceReferences[0];			
			IRemoteService remoteService = remoteServiceContainerAdapter.getRemoteService(reference);

			// Create the soap service on axis
			SoapRemoteService soapService = new SoapRemoteService(
					serviceTracker.getSoapServlet().getEngine(), clazz,
					remoteService.getProxy(), allowedMethod);

			//deploy the service
			soapService.deployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("",e); //$NON-NLS-1$
		} catch (InvalidSyntaxException e) {
			throw new ECFException("",e); //$NON-NLS-1$
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.soap.host.ISoapServerContainerAdapter#deployRemoteServiceAsWebService(java.lang.String, java.lang.String, org.eclipse.ecf.remoteservice.IRemoteService)
	 */
	public void deployRemoteServiceAsWebService(String serviceName, String allowedMethod, IRemoteService remoteService) throws ECFException {
		try {
			Assert.isNotNull(serviceName);
			Assert.isNotNull(allowedMethod);
			Assert.isNotNull(remoteService);

			// Create the soap service on axis
			SoapRemoteService soapService = new SoapRemoteService(
					serviceTracker.getSoapServlet().getEngine(), serviceName,
					remoteService.getProxy(), allowedMethod);

			//deploy the service
			soapService.deployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("",e); //$NON-NLS-1$
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.soap.host.ISoapServerContainerAdapter#deployRemoteServiceAsWebService(org.eclipse.ecf.remoteservice.soap.host.IServiceDescription, org.eclipse.ecf.remoteservice.IRemoteService)
	 */
	public void deployRemoteServiceAsWebService(IServiceDescription description, IRemoteService remoteService)throws ECFException {
		try {
			Assert.isNotNull(description);
			Assert.isNotNull(remoteService);

			// Create the soap service on axis
			SoapRemoteService soapService = new SoapRemoteService(serviceTracker.getSoapServlet().getEngine(), description,	remoteService.getProxy());

			//deploy the service
			soapService.deployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("",e); //$NON-NLS-1$
		}
		
	}


	/**
	 * Get the remote service and remove the web service access to it
	 * @param clazz
	 *            . the fully qualified name of the interface class that
	 *            describes the desired service. It will be the web service name. Must not be <code>null</code>.
	 * @param rosgiServiceHost
	 *            . Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public void undeployRemoteServiceAsWebService(String clazz, IRemoteServiceContainer remoteServiceContainer, ID targetID) throws ECFException {
		try {

			// Lookup IRemoteServiceReference
			IRemoteServiceReference[] serviceReferences = remoteServiceContainer.getContainerAdapter()
					.getRemoteServiceReferences(targetID, clazz, null);

			// Get remote service for reference
			IRemoteService remoteService = remoteServiceContainer.getContainerAdapter()
					.getRemoteService(serviceReferences[0]);

			// Create the soap service on axis
			SoapRemoteService soapService = new SoapRemoteService(
					serviceTracker.getSoapServlet().getEngine(), clazz,
					remoteService.getProxy());
			soapService.undeployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (InvalidSyntaxException e) {
			throw new ECFException("", e); //$NON-NLS-1$
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.soap.host.ISoapServerContainerAdapter#undeployRemoteServiceAsWebService(java.lang.String, org.eclipse.ecf.remoteservice.IRemoteService)
	 */
	public void undeployRemoteServiceAsWebService(String serviceName, IRemoteService remoteService) throws ECFException {
		try {

			// Create the soap service on axis
			SoapRemoteService soapService = new SoapRemoteService(
					serviceTracker.getSoapServlet().getEngine(), serviceName,
					remoteService.getProxy());
			soapService.undeployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("", e); //$NON-NLS-1$
		}

	}

	public Object getAdapter(Class adapter) {
		if (adapter == null)
			return null;
		final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.soap.host.ISoapServerContainerAdapter#undeployRemoteServiceAsWebService(org.eclipse.ecf.remoteservice.soap.host.IServiceDescription, org.eclipse.ecf.remoteservice.IRemoteService)
	 */
	public void undeployRemoteServiceAsWebService(
			IServiceDescription description, IRemoteService remoteService)
			throws ECFException {
		
		String serviceName = (String) description.getProperty("service.name");
		
		undeployRemoteServiceAsWebService(serviceName, remoteService);
		
	}

}
