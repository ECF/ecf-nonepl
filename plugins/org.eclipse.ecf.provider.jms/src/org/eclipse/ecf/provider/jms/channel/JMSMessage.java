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

public class JMSMessage implements Serializable, ECFMessage {

	private static final long serialVersionUID = 3256722900785707057L;

	ID target;

	ID sender;

	String jmsClientID;

	Serializable data;

	protected JMSMessage(String clientID, ID sender, ID target,
			Serializable data) {
		this.jmsClientID = clientID;
		this.sender = sender;
		this.target = target;
		this.data = data;
	}

	public Serializable getData() {
		return data;
	}

	public ID getTargetID() {
		return target;
	}

	public ID getSenderID() {
		return sender;
	}

	public String getSenderJMSID() {
		return jmsClientID;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("JMSMessage[");
		buf.append(target).append(";").append(data).append("]");
		return buf.toString();
	}

}
