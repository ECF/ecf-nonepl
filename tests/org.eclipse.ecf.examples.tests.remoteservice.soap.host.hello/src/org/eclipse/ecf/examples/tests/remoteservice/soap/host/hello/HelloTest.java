/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.tests.remoteservice.soap.host.hello;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.soap.host.hello.HelloSoapContainer;
import org.eclipse.ecf.tests.httpservice.AbstractHttpServiceTest;



public class HelloTest extends AbstractHttpServiceTest {

	IContainer container;
	IRemoteServiceContainerAdapter containerAdapter;
	
 	protected void setUp() throws Exception {
		super.setUp();
	}
 	
 	protected void tearDown() throws Exception {
 		super.tearDown();
 		containerAdapter = null;
 		container.dispose();
 		container = null;
 		getContainerManager().removeAllContainers();
 	}
 	
 	
 	public void testWSService(){
 		try {
 	 		startHttpService();
 	 		container = getContainerFactory().createContainer("org.eclipse.ecf.remoteservice.soap.host.hello");
 	 		
 	 		HelloSoapContainer helloContainer = (HelloSoapContainer)container;

 	 		Assert.assertNotNull(helloContainer);

 	 		helloContainer.deployRemoteServiceAsWebService();
 	 		
			String endpoint = "http://localhost:8089/services/org.eclipse.ecf.examples.remoteservices.hello.IHello";

			Service service = new Service();
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("hello"));

			call.invoke(new Object[] { "A hello from the Axis client test" });
			
			helloContainer.undeployRemoteServiceAsWebService();
			
			stopHttpService();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
 	}

}
