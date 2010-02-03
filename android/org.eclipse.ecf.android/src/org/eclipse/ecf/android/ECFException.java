/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import android.util.AndroidRuntimeException;


public class ECFException extends AndroidRuntimeException {
	private static final long serialVersionUID = 3256440309134406707L;

	public ECFException() {
		this(null, null);
	}

	/**
	 * @param message
	 *            message associated with exception
	 */
	public ECFException(String message) {
		this(message, null);
	}

	/**
	 * @param cause
	 *            the cause of the new exception
	 */
	public ECFException(Throwable cause) {
		this(null, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ECFException(String message, Throwable cause) {
//	TODO	this(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, ((message == null) ? "" : message), cause)); //$NON-NLS-1$
		new RuntimeException();
	}

	public ECFException(IStatus status) {
		// TODO Auto-generated constructor stub
	}

}