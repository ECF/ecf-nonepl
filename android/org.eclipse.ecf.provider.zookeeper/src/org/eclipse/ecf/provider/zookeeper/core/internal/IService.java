/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.util.Properties;

import org.eclipse.ecf.discovery.IServiceInfo;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public interface IService extends IServiceInfo {

	String LOCATION = "discovery.service.location"; //$NON-NLS-1$	
	String WEIGHT = "discovery.service.weight"; //$NON-NLS-1$
	String PRIORITY = "discovery.service.priority"; //$NON-NLS-1$
	String PROTOCOLS = "discovery.service.protocol"; //$NON-NLS-1$

	Properties getProperties();

	/**
	 * Stored as value of a zookeeper node
	 * 
	 * @return byte value of string
	 */
	byte[] getPropertiesAsBytes();

	String getPropertiesAsString();
}
