/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.android;

/**
 * @since 3.3
 */
public interface IRemoteServiceProxy {

	/**
	 * Get IRemoteService associated with this proxy
	 * @return IRemoteService for this proxy.  May be <code>null</code> 
	 * if no IRemoteService available for proxy.
	 */
	IRemoteService getRemoteService();

	/**
	 * Get IRemoteService reference associated with this proxy.
	 *
	 * @return IRemoteServiceReference for this proxy.  May be <code>null</code>
	 * if no IRemoteServiceReference associated with this proxy.
	 */
	IRemoteServiceReference getRemoteServiceReference();
}
