/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator plugin;

	public Activator() {
	}

	public static Activator getDefault() {
		if (plugin == null)
			plugin = new Activator();
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {

		// Debug.addClass(iServerStoreFactory.instance().getServerStoreFacade().getStore().getClass());

		super.start(context);
		plugin = this;

	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		Enumeration<?> findEntries = getBundle().findEntries("icons", "*.gif",
				true);
		while (findEntries.hasMoreElements()) {
			File file;
			file = new File(((URL) findEntries.nextElement()).getFile());
			reg.put(file.getName(), imageDescriptorFromPlugin(this.getBundle()
					.getSymbolicName(), "icons/" + file.getName()));
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		// TODO Auto-generated method stub
		super.stop(context);
	}

}
