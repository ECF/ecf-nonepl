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
 * Exception thrown upon
 * {@link ISharedObjectManager#disconnectSharedObjects(ISharedObjectConnector)}
 * 
 * @see ISharedObjectManager#disconnectSharedObjects(ISharedObjectConnector)
 */
public class SharedObjectDisconnectException extends ECFException {
	private static final long serialVersionUID = 3258689922876586289L;

	public SharedObjectDisconnectException() {
		super();
	}

	public SharedObjectDisconnectException(IStatus status) {
		super(status);
	}
	public SharedObjectDisconnectException(String arg0) {
		super(arg0);
	}

	public SharedObjectDisconnectException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SharedObjectDisconnectException(Throwable cause) {
		super(cause);
	}
}