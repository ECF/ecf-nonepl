/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.soap.host;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.ecf.internal.remoteservice.soap.host.servlet.AbstractSoapServlet;
import org.eclipse.ecf.internal.remoteservice.soap.host.servlet.SoapServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @since 3.4
 * 
 */
public class SOAPServiceTracker extends ServiceTracker {

	private AbstractSoapServlet soapServlet;
	String services = "/services";
	
	public SOAPServiceTracker(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	public Object addingService(ServiceReference reference) {
		HttpService httpService = (HttpService) super.addingService(reference);

		if (httpService == null)
			return null;

		try {
						
			soapServlet = new SoapServlet(context);
			final Dictionary initParamsAxis = new Hashtable();
			initParamsAxis.put("servlet-name", "AxisServlet");
			
			httpService.registerServlet(services, soapServlet,initParamsAxis, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return httpService;
	}

	public void removedService(ServiceReference reference, Object service) {
		HttpService httpService = (HttpService) service;
		httpService.unregister(services);
		super.removedService(reference, service);
	}


	public AbstractSoapServlet getSoapServlet() {
		return soapServlet;
	}

}
