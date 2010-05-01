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
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.remoteservice.soap.host.Activator;
import org.eclipse.ecf.internal.remoteservice.soap.host.SOAPRemoteService;
import org.eclipse.ecf.internal.remoteservice.soap.host.SOAPServiceTracker;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.soap.host.identity.SoapID;
import org.eclipse.ecf.remoteservice.soap.host.identity.SoapNamespace;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Create a soap container which is responsible to locate
 * 
 * @since 3.4
 * 
 */
public abstract class AbstractSoapContainer extends AbstractContainer {

	private SOAPServiceTracker serviceTracker;

	protected SoapID containerID;
	protected String containerType = "ecf.r_osgi.peer"; //$NON-NLS-1$
	protected ServiceTracker containerFactoryServiceTracker;
	protected BundleContext bundleContext;
	protected IContainer container;
	protected IRemoteServiceContainerAdapter containerAdapter;
	protected Object connectLock = new Object();
	protected ID connectedID;
	protected ID targetID = null;
	protected IConnectContext connectContext;

	/**
	 * 
	 * @param containerID
	 *            . Must not be <code>null</code>.
	 * @throws ECFException
	 */
	public AbstractSoapContainer(SoapID containerID) throws ECFException {
		this.containerID = containerID;
		Assert.isNotNull(this.containerID);
		bundleContext = Activator.getDefault().getContext();
		serviceTracker = Activator.getDefault().getServiceTracker();
		container = getContainerFactory().createContainer(containerType);

		// Get remote service container adapter
		containerAdapter = (IRemoteServiceContainerAdapter) container
				.getAdapter(IRemoteServiceContainerAdapter.class);

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
	public void deployRemoteServiceAsWebService(String clazz,
			String rosgiServiceHost, String allowedMethod) throws ECFException {
		try {
			Assert.isNotNull(clazz);
			Assert.isNotNull(rosgiServiceHost);
			Assert.isNotNull(allowedMethod);

			// Lookup IRemoteServiceReference
			IRemoteServiceReference[] serviceReferences = containerAdapter
					.getRemoteServiceReferences(IDFactory.getDefault()
							.createID(container.getConnectNamespace(),
									rosgiServiceHost), clazz, null);

			// Get remote service for reference

			if(serviceReferences == null ||serviceReferences.length == 0)
				throw new ECFException("The remote reference is not available : "+clazz);
			IRemoteServiceReference reference = serviceReferences[0];			
			IRemoteService remoteService = containerAdapter
					.getRemoteService(reference);

			// Create the soap service on axis
			SOAPRemoteService soapService = new SOAPRemoteService(
					serviceTracker.getSoapServlet().getEngine(), clazz,
					remoteService, allowedMethod);
			soapService.deployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("",e); //$NON-NLS-1$
		} catch (InvalidSyntaxException e) {
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
	public void undeployRemoteServiceAsWebService(String clazz,
			String rosgiServiceHost) throws ECFException {
		try {

			// Lookup IRemoteServiceReference
			IRemoteServiceReference[] serviceReferences = containerAdapter
					.getRemoteServiceReferences(IDFactory.getDefault()
							.createID(container.getConnectNamespace(),
									rosgiServiceHost), clazz, null);

			// Get remote service for reference
			IRemoteService remoteService = containerAdapter
					.getRemoteService(serviceReferences[0]);

			// Create the soap service on axis
			SOAPRemoteService soapService = new SOAPRemoteService(
					serviceTracker.getSoapServlet().getEngine(), clazz,
					remoteService);
			soapService.undeployService();

		} catch (AxisFault e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (IDCreateException e) {
			throw new ECFException("", e); //$NON-NLS-1$
		} catch (InvalidSyntaxException e) {
			throw new ECFException("", e); //$NON-NLS-1$
		}

	}

	private IContainerFactory getContainerFactory() {
		if (containerFactoryServiceTracker == null) {
			containerFactoryServiceTracker = new ServiceTracker(bundleContext,
					IContainerFactory.class.getName(), null);
			containerFactoryServiceTracker.open();
		}
		return (IContainerFactory) containerFactoryServiceTracker.getService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID,
	 * org.eclipse.ecf.core.security.IConnectContext)
	 */
	public void connect(ID targetID, IConnectContext connectContext1)
			throws ContainerConnectException {
		if (targetID == null)
			throw new ContainerConnectException("targetID cannot be null"); //$NON-NLS-1$
		Namespace targetNamespace = targetID.getNamespace();
		Namespace connectNamespace = getConnectNamespace();
		if (connectNamespace == null)
			throw new ContainerConnectException(
					"targetID namespace cannot be null"); //$NON-NLS-1$
		if (!(targetNamespace.getName().equals(connectNamespace.getName())))
			throw new ContainerConnectException("targetID of incorrect type"); //$NON-NLS-1$
		fireContainerEvent(new ContainerConnectingEvent(containerID, targetID));
		synchronized (connectLock) {
			if (connectedID == null) {
				connectedID = targetID;
				this.connectContext = connectContext1;
			} else if (!connectedID.equals(targetID))
				throw new ContainerConnectException(
						"Already connected to " + connectedID.getName()); //$NON-NLS-1$
		}
		fireContainerEvent(new ContainerConnectedEvent(containerID, targetID));

		if (!targetID.getNamespace().getName().equals(
				getConnectNamespace().getName()))
			throw new ContainerConnectException("targetID not of appropriate Namespace"); //$NON-NLS-1$

		fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));

		this.targetID = targetID;
		fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));

	}

	public void disconnect() {
		ID oldId = connectedID;
		fireContainerEvent(new ContainerDisconnectingEvent(containerID, oldId));
		synchronized (connectLock) {
			connectedID = null;
			connectContext = null;
		}
		fireContainerEvent(new ContainerDisconnectedEvent(containerID, oldId));
	}

	public ID getConnectedID() {
		synchronized (connectLock) {
			return connectedID;
		}
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(SoapNamespace.NAME);
	}

	public ID getID() {
		return containerID;
	}

	public Object getAdapter(Class serviceType) {

		return super.getAdapter(serviceType);
	}

}
