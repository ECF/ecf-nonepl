/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.security.PermissionCollection;

/**
 * Connect policy typically implemented by servers
 * 
 */
public interface IConnectHandlerPolicy extends IContainerPolicy {
	/**
	 * Check connect request
	 * 
	 * @param address
	 *            the address for the remote client
	 * @param fromID
	 *            the ID of the container making the connect request
	 * @param targetID
	 *            the ID of the container responding to that connect request
	 * @param targetGroup
	 *            the target name of the group that is being connected to
	 * @param connectData
	 *            arbitrary data associated with the join request
	 * @return PermissionCollection a collection of permissions associated with
	 *         a successful acceptance of join request
	 * @throws Exception thrown if connect should not be allowed
	 */
	public PermissionCollection checkConnect(Object address, ID fromID, ID targetID, String targetGroup, Object connectData) throws Exception;
}
