/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.examples.tests.remoteservice.soap.host.hello;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.RemoteServiceContainer;
import org.eclipse.ecf.remoteservice.soap.host.ISoapServerContainerAdapter;
import org.eclipse.ecf.tests.httpservice.AbstractHttpServiceTest;



public class HelloTest extends AbstractHttpServiceTest {

	IContainer container;
	String containerType = "ecf.r_osgi.peer";
	String HOST_CONTAINER_ENDPOINT_ID = "r-osgi://localhost:9278";
	
 	protected void setUp() throws Exception {
		super.setUp();
 		container = getContainerFactory().createContainer(containerType);
	}
 	
 	protected void tearDown() throws Exception {
 		super.tearDown();
 		container.dispose();
 		container = null;
 		getContainerManager().removeAllContainers();
 	}
 	
 	protected IRemoteServiceContainer createRemoteServiceContainer(
			IContainer container) {
		return new RemoteServiceContainer(container,
				(IRemoteServiceContainerAdapter) container
						.getAdapter(IRemoteServiceContainerAdapter.class));
	}
 	
 	public void testWSServiceUsingContainerAdapter(){
 		try {
 	 		startHttpService();
 	 	
			ISoapServerContainerAdapter soapContainerAdapter =  (ISoapServerContainerAdapter) ContainerFactory.getDefault().createContainer().getAdapter(ISoapServerContainerAdapter.class);

 	 		IRemoteServiceContainer rsContainer = createRemoteServiceContainer(container);
 	 		ID targetID = IDFactory.getDefault().createID(container.getConnectNamespace(),	HOST_CONTAINER_ENDPOINT_ID);
 	 		soapContainerAdapter.deployRemoteServiceAsWebService(IHello.class.getName(), "*", rsContainer.getContainerAdapter(), targetID);
 	 		
			String endpoint = "http://localhost:8089/services/org.eclipse.ecf.examples.remoteservices.hello.IHello";

			Service service = new Service();
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("hello"));

			call.invoke(new Object[] { "A hello from the Axis client test using container adapter" });
			
			soapContainerAdapter.undeployRemoteServiceAsWebService(IHello.class.getName(), rsContainer, targetID);
			
			stopHttpService();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
 	}

 	public void testWSService(){
 		try {
 	 		startHttpService();
 	 	
			ISoapServerContainerAdapter soapContainerAdapter =  (ISoapServerContainerAdapter) ContainerFactory.getDefault().createContainer().getAdapter(ISoapServerContainerAdapter.class);

 	 		IRemoteServiceContainer rsContainer = createRemoteServiceContainer(container);
 	 		ID targetID = IDFactory.getDefault().createID(container.getConnectNamespace(),	HOST_CONTAINER_ENDPOINT_ID);
 	 		
			// Lookup IRemoteServiceReference
			IRemoteServiceReference[] serviceReferences = rsContainer.getContainerAdapter().getRemoteServiceReferences(targetID, IHello.class.getName(), null);

			// Get remote service for reference
			if(serviceReferences == null ||serviceReferences.length == 0)
				throw new ECFException("The remote reference is not available : "+IHello.class.getName());
			
			IRemoteServiceReference reference = serviceReferences[0];			
			IRemoteService remoteService = rsContainer.getContainerAdapter().getRemoteService(reference);

 	 		
 	 		soapContainerAdapter.deployRemoteServiceAsWebService("hello", "*", remoteService);
 	 		
			String endpoint = "http://localhost:8089/services/hello";

			Service service = new Service();
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("hello"));

			call.invoke(new Object[] { "A hello from the Axis client test IRemoteService" });
			
			soapContainerAdapter.undeployRemoteServiceAsWebService("hello", remoteService);
			
			stopHttpService();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
 	}

}
