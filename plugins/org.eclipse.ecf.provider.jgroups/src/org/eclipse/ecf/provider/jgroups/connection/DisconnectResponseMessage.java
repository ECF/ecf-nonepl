/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.connection;

import java.io.Serializable;

import org.eclipse.ecf.core.identity.ID;

public class DisconnectResponseMessage implements Serializable {

	private static final long serialVersionUID = 6407659936123232952L;

	ID targetID;

	ID clientID;

	Serializable data;

	public DisconnectResponseMessage(ID clientID, ID targetID, Serializable data) {
		this.clientID = clientID;
		this.targetID = targetID;
		this.data = data;
	}

	public ID getTargetID() {
		return targetID;
	}

	public ID getSenderID() {
		return clientID;
	}

	public Serializable getData() {
		return data;
	}

	public String toString() {
		final StringBuffer buf = new StringBuffer("DisconnectResponseMessage["); //$NON-NLS-1$
		buf.append(clientID).append(";").append(targetID).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append(data).append("]"); //$NON-NLS-1$ 
		return buf.toString();
	}

}