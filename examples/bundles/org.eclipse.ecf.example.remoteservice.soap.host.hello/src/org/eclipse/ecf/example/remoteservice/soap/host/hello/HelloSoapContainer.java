/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.example.remoteservice.soap.host.hello;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.remoteservice.soap.host.AbstractSoapContainer;
import org.eclipse.ecf.remoteservice.soap.host.identity.SoapID;

public class HelloSoapContainer extends AbstractSoapContainer {

	public static final String ROSGI_SERVICE_HOST = "r-osgi://localhost:9278";

	public HelloSoapContainer(SoapID containerID) throws ECFException {
		super(containerID);
		
	}
	
	public void deployRemoteServiceAsWebService() throws ECFException {
		super.deployRemoteServiceAsWebService(IHello.class.getName(),ROSGI_SERVICE_HOST, "hello");
	}

	public void undeployRemoteServiceAsWebService() throws ECFException {
		super.undeployRemoteServiceAsWebService(IHello.class.getName(),ROSGI_SERVICE_HOST);
	}

}
