package org.eclipse.ecf.provider.jms.channel;

import java.io.Serializable;

import org.eclipse.ecf.core.identity.ID;

public class DisconnectResponse implements Serializable, ECFMessage, SynchResponse {

	private static final long serialVersionUID = 6207505046491179233L;
	String jmsTopicClientID;
	ID targetID;
	ID clientID;
	Serializable data;

	public DisconnectResponse(String jmsTopicClientID, ID clientID, ID targetID, Serializable data) {
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
		StringBuffer buf = new StringBuffer("DisconnectResponse[");
		buf.append(clientID).append(";").append(targetID).append(";");
		buf.append(jmsTopicClientID).append(";").append(data).append("]");
		return buf.toString();
	}

}
