/*******************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.rt.internal.examples.webapp.hello;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * This is an example OSGi Declarative Services component. It is referenced by
 * OSGI-INF/hello.xml, which is read by DS upon starting of this bundle
 * (OSGI-INF/hello.xml is specified by the Service-Component property value in
 * META-INF/MANIFEST.MF).
 * 
 * The result is that as per the declaration in hello.xml, the HttpService is
 * bound to this instance (via {@link #bind(HttpService)} method), and then this
 * component's {@link #activate()} method is called.
 * 
 * For this example, in response to the {@link #activate()} method call a
 * resource alias is established, and then a HelloServlet instance is created
 * and registered with the HttpService with the alias '/hello'.
 * 
 * Conversely, when {@link #deactivate()} is called, it unregisters both the
 * resource and the servlet registrations, and then the unbind methods null out
 * the HttpService and HttpContext references.
 * 
 */
public class HelloComponent {

	/**
	 * The name of our servlet alias.  This name will be added to the
	 * name of the EclipseRT within servlet container...e.g. servlet's resulting
	 * external URL will be:  http://<host>:<port>/eclipsert100/hello
	 */
	public static final String HELLO_SERVLET_ALIAS = "/hello";
	/**
	 * The resource path name (in URL) to resources exposed by this servlet
	 */
	public static final String HELLO_SERVLET_RESOURCE_PATH = "resources";
	/**
	 * The alias for the resource path is just the resource path prepended with "/"
	 */
	public static final String HELLO_SERVLET_RESOURCE_ALIAS = "/"
			+ HELLO_SERVLET_RESOURCE_PATH;

	/**
	 * The directory within this bundle that contains the resources.  This is aliased
	 * to {@link #HELLO_SERVLET_ALIAS} by the httpService.registerResources call in
	 * {@link #activate()}.
	 */
	public static final String HELLO_SERVLET_RESOURCE_DIR = "/webresources";

	/**
	 * The http service to use, injected via bind method (by DS)
	 */
	private HttpService httpService;
	/**
	 * The http context to use.  Created in bind method (once httpService is received).
	 */
	private HttpContext httpContext;

	/**
	 * Our own HttpContext implementation.  For our HttpContext, the 
	 * resource name passed into getResource will be used to get the resource
	 * URL corresponding to the path within the bundle...i.e. in /webresources
	 * directory inside our bundle.
	 */
	class HelloHttpContext implements HttpContext {
		/**
		 * When resource is requested, simply return the resource, as the
		 * name is the absolute path.
		 * 
		 * @return URL the url of the resource with the given name
		 */
		public URL getResource(String name) {
			return getClass().getResource(name);
		}

		public boolean handleSecurity(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
			return true;
		}

		public String getMimeType(String name) {
			return null;
		}
	}
	
	/**
	 * Bind the http service to this component.  This method will be called by
	 * DS (declarative services) prior to this component being activated.
	 * 
	 * @param httpService the httpService we will be bound to. 
	 */
	void bind(HttpService httpService) {
		this.httpService = httpService;
		this.httpContext = new HelloHttpContext();
	}

	/**
	 * Unbinds the http service from this component.  This method will be called by
	 * DS (declarative services) after this component is deactivated.
	 * 
	 * @param httpService the httpService we will be unbound from.
	 */
	void unbind(HttpService httpService) {
		this.httpService = null;
		this.httpContext = null;
	}

	/**
	 * Activate this component.  After the HttpService instance is injected, DS will 
	 * call this method to activate the component.  We respond to this by registering
	 * the resource alias {@link #HELLO_SERVLET_RESOURCE_ALIAS} and by registering
	 * a new {@link HelloServlet} with alias {@link #HELLO_SERVLET_ALIAS}.
	 * 
	 * @throws Exception if the resources or the servlet cannot be registered, the 
	 * component activation will throw an exception (and component activation will fail).
	 */
	void activate() throws Exception {
		// Register resources
		this.httpService.registerResources(HELLO_SERVLET_RESOURCE_ALIAS,
				HELLO_SERVLET_RESOURCE_DIR, httpContext);
		// Register new HelloServlet
		this.httpService.registerServlet(HELLO_SERVLET_ALIAS, new HelloServlet(
				HELLO_SERVLET_RESOURCE_PATH), null, httpContext);
	}

	/**
	 * Deactivate this component.  We respond to this by unregistering the resource
	 * alias and the servlet alias.
	 */
	void deactivate() {
		// Unregister HELLO_SERVLET_ALIAS
		this.httpService.unregister(HELLO_SERVLET_ALIAS);
		// Unregister HELLO_SERVLET_RESOURCE_ALIAS
		this.httpService.unregister(HELLO_SERVLET_RESOURCE_ALIAS);
	}
}
