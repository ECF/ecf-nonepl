/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


/**
 * Shared object manager event
 * 
 */
public interface ISharedObjectManagerEvent extends IContainerEvent {

	/**
	 * Get shared object ID for shared object in question
	 * 
	 * @return ID of shared object in question. Will not return null.
	 */
	public ID getSharedObjectID();
}
