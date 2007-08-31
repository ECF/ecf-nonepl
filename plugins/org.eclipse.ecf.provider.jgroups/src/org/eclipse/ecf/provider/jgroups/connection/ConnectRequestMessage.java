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
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.jgroups.Address;

public class ConnectRequestMessage implements Serializable {

	private static final long serialVersionUID = 6802941271892242707L;

	Address clientAddress;

	ID clientID;

	JGroupsID targetID;

	Serializable data;

	public ConnectRequestMessage(Address clientAddress, ID clientID, JGroupsID targetID, Serializable data) {
		this.clientAddress = clientAddress;
		this.clientID = clientID;
		this.targetID = targetID;
		this.data = data;
	}

	public JGroupsID getTargetID() {
		return targetID;
	}

	public ID getSenderID() {
		return clientID;
	}

	public Address getClientAddress() {
		return clientAddress;
	}

	public Serializable getData() {
		return data;
	}

	public String toString() {
		final StringBuffer buf = new StringBuffer("ConnectRequestMessage["); //$NON-NLS-1$
		buf.append(clientID).append(";").append(targetID).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append(clientAddress).append(";").append(data).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return buf.toString();
	}

}
