/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.jms;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 *
 */
public class ConnectionFactoryCreateException extends ECFException {

	private static final long serialVersionUID = -6959717405710622377L;

	public ConnectionFactoryCreateException() {
		super();
	}

	/**
	 * @param status
	 */
	public ConnectionFactoryCreateException(IStatus status) {
		super(status);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnectionFactoryCreateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ConnectionFactoryCreateException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConnectionFactoryCreateException(Throwable cause) {
		super(cause);
	}

}
