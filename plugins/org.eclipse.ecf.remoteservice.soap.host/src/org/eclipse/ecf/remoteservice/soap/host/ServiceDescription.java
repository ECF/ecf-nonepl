/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.host;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

public class ServiceDescription implements IServiceDescription {

	private Map properties;

	public ServiceDescription(Map properties){
		Assert.isNotNull(properties);
		this.properties = properties;
	}
	
	public Map getProperties() {
		return this.properties;
	}

	public Object getProperty(String key) {
		return this.properties.get(key);
	}

}
