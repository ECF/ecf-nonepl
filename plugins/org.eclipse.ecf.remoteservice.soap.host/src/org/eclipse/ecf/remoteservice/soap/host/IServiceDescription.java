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

public interface IServiceDescription {

	/**
	 * Returns the required service properties. It must be not null.
	 * @return
	 */
	public Map getProperties();

	/**
	 * Returns the property value to which the specified property key is mapped in the properties .
	 * @param key
	 * @return
	 */
	public Object getProperty(String key);
}
