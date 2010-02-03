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

import java.io.Serializable;

public class AsynchMessage implements Serializable {
	private static final long serialVersionUID = 3258689905679873075L;
	Serializable data;

	protected AsynchMessage() {
		//
	}

	protected AsynchMessage(Serializable data) {
		this.data = data;
	}

	Serializable getData() {
		return data;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("AsynchMessage["); //$NON-NLS-1$
		buf.append(data).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}