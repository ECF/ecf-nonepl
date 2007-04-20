/**
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	- Initial API and implementation
 *  	- Chris Aniszczyk <zx@us.ibm.com>
 *   	- Borna Safabakhsh <borna@us.ibm.com> 
 *   
 * $Id: Activator.java,v 1.3 2007/04/20 22:28:25 slewis Exp $
 */

package org.eclipse.ecf.internal.provider.yahoo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator implements BundleActivator {

	/**
	 * Namespace identifier
	 */
	public static final String NAMESPACE_IDENTIFIER = "ecf.yahoo";
	
	//The shared instance.
	private static Activator plugin;
	
	/**
	 * The constructor.
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public synchronized static Activator getDefault() {
		if (plugin == null) {
			plugin = new Activator();
		}
		return plugin;
	}

}
