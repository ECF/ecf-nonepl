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
 * Exception thrown during calls to
 * {@link ISharedObject#init(ISharedObjectConfig)}
 * 
 * @see ISharedObject#init(ISharedObjectConfig)
 */
public class SharedObjectInitException extends ECFException {
	private static final long serialVersionUID = 3617579318620862771L;

	public SharedObjectInitException() {
		super();
	}

	public SharedObjectInitException(IStatus status) {
		super(status);
	}
	public SharedObjectInitException(String arg0) {
		super(arg0);
	}

	public SharedObjectInitException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SharedObjectInitException(Throwable cause) {
		super(cause);
	}
}