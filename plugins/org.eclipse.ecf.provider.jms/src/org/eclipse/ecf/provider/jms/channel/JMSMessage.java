package org.eclipse.ecf.provider.jms.channel;

import java.io.Serializable;

import org.eclipse.ecf.core.identity.ID;

public class JMSMessage implements Serializable, ECFMessage {

	private static final long serialVersionUID = 3256722900785707057L;

	ID target;
	ID sender;
	String jmsClientID;
	Serializable data;

	protected JMSMessage(String clientID, ID sender, ID target, Serializable data) {
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
