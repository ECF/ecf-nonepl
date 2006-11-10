/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.channel;

import java.io.Serializable;

import org.eclipse.ecf.core.identity.ID;

public class ConnectRequest implements Serializable, ECFMessage, SynchRequest {

	private static final long serialVersionUID = -1660845225936582555L;

	String jmsTopicClientID;

	ID targetID;

	ID clientID;

	Serializable data;

	public ConnectRequest(String jmsTopicClientID, ID clientID, ID targetID,
			Serializable data) {
		this.clientID = clientID;
		this.targetID = targetID;
		this.jmsTopicClientID = jmsTopicClientID;
		this.data = data;
	}

	public ID getTargetID() {
		return targetID;
	}

	public ID getSenderID() {
		return clientID;
	}

	public String getSenderJMSID() {
		return jmsTopicClientID;
	}

	public Serializable getData() {
		return data;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("ConnectRequest[");
		buf.append(clientID).append(";").append(targetID).append(";");
		buf.append(jmsTopicClientID).append(";").append(data).append("]");
		return buf.toString();
	}

}
