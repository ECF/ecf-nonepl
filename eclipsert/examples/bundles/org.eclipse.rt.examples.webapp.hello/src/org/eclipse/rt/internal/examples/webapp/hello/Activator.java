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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi BundleActivator class.  This class provides access to this bundle's
 * BundleContext, which allows information about the bundles' symbolic id and it's
 * runtime id to be introspected by the HelloServlet.
 * 
 */
public class Activator implements BundleActivator {

	private BundleContext context;
	private static Activator instance;
	
	public static Activator getDefault() {
		return instance;
	}
	
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
	}

	public void stop(BundleContext context) throws Exception {
		this.context = null;
		instance = null;
	}

	/**
	 * Get the bundle's symbolic name (i.e. org.eclipse.equinox.server.examples.webapp.hello)
	 * @return
	 */
	public String getBundleSymbolicName() {
		return this.context.getBundle().getSymbolicName();
	}
	
	/**
	 * Get the bundle's framework id (determined at runtime by the framework).
	 * @return
	 */
	public String getBundleId() {
		return new Long(this.context.getBundle().getBundleId()).toString();
	}
}
