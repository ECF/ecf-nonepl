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
 * Defines implementing classes as being identifiable with an ECF ID.
 * 
 */
public interface IIdentifiable {
	/**
	 * Return the ID for this 'identifiable' object. The returned ID should be
	 * unique within its namespace.  May return <code>null</code>.
	 * 
	 * @return the ID for this identifiable object.  May return <code>null</code>.
	 */
	public ID getID();
}
