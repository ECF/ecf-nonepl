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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	public static final String PLUGIN_ID = "org.eclipse.ecf.examples.tests.provider.soap.hello"; //$NON-NLS-1$

	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
	}

	public void stop(BundleContext context) throws Exception {
		context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

}
