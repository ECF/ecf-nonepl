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
package org.eclipse.ecf.android;


/**
 * Connection event super class.
 * 
 */
public class ConnectionEvent implements Event { // 
	private static final long serialVersionUID = 3257290214476362291L;

	private final Object data;

	private final IConnection connection;

	public ConnectionEvent(IConnection source, Object data) {
		this.connection = source;
		this.data = data;
	}

	public IConnection getConnection() {
		return connection;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer("ConnectionEvent["); //$NON-NLS-1$
		buf.append("conn=").append(getConnection()).append(";").append("data=") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append(getData());
		buf.append("]"); //$NON-NLS-1$
		return buf.toString();
	}

}