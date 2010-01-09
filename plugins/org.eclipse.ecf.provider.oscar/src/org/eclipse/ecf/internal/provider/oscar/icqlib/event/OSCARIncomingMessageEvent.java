/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.icqlib.event;

import org.eclipse.ecf.provider.comm.AsynchEvent;
import org.eclipse.ecf.provider.comm.IAsynchConnection;

public class OSCARIncomingMessageEvent extends AsynchEvent {

	public OSCARIncomingMessageEvent(IAsynchConnection conn, String message) {
		super(conn, message);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("OSCARIncomingMessageEvent["); //$NON-NLS-1$
		sb.append(getData()).append(";"); //$NON-NLS-1$
		sb.append(getConnection()).append("]"); //$NON-NLS-1$
		return sb.toString();
	}
}
