/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.soap.host.servlet;

import org.osgi.framework.BundleContext;

/**
 * This is the entry point to all SOAP requests for remote ECF services.
 * @since 3.4
 */
public class SOAPServlet extends AbstractSOAPServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2696423844208922993L;
	
	private BundleContext bundleContext;

	public SOAPServlet(BundleContext context) {
		this.bundleContext = context;
	}

	/**
	 * create a new servlet instance
	 */
	public SOAPServlet() {

	}
	
	public BundleContext getBundle() {
		return bundleContext;
	}

}
