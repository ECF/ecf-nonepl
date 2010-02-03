/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.util.Map;


/**
 * Configuration information associated with ISharedObjectContainer.
 * 
 */
public interface ISharedObjectContainerConfig extends IIdentifiable, IAdaptable {
	/**
	 * The properties associated with the owner ISharedObjectContainer
	 * 
	 * @return Map the properties associated with owner ISharedObjectContainer
	 */
	public Map getProperties();

}