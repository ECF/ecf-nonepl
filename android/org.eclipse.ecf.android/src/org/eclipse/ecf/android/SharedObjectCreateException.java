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
 * Exception thrown upon shared object create by {@link ISharedObjectManager}
 * 
 * @see ISharedObjectManager#createSharedObject(SharedObjectDescription)
 */
public class SharedObjectCreateException extends ECFException {
	private static final long serialVersionUID = 3546919195137815606L;

	public SharedObjectCreateException() {
		super();
	}

	public SharedObjectCreateException(IStatus status) {
		super(status);
	}
	public SharedObjectCreateException(String arg0) {
		super(arg0);
	}

	public SharedObjectCreateException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SharedObjectCreateException(Throwable cause) {
		super(cause);
	}
}