/**
 * 
 */
package org.eclipse.ecf.provider.jms.channel;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.jms.ObjectMessage;
import org.eclipse.ecf.core.comm.ISynchAsynchConnection;
import org.eclipse.ecf.core.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.core.comm.SynchEvent;
import org.eclipse.ecf.core.identity.ID;

/**
 * @author slewis
 * 
 */
public class ServerChannel extends Channel implements ISynchAsynchConnection {
	private static final long serialVersionUID = -4762123821387039176L;

	public ServerChannel(ISynchAsynchEventHandler handler,
			int keepAlive) throws IOException, URISyntaxException {
		super(handler, keepAlive);
		URI aURI = containerID.toURI();
		url = aURI.getSchemeSpecificPart();
		topicName = removeLeadingSlashes(new URI(url));
		setup();
	}

	public synchronized Object connect(ID remote, Object data, int timeout)
			throws IOException {
		throw new ConnectException("server cannot connect");
	}

	protected void respondToRequest(ObjectMessage omsg, ECFMessage o) {
		trace("respondToRequest(" + o + ")");
		try {
			Serializable[] resp = (Serializable[]) handler
					.handleSynchEvent(new SynchEvent(this, o));
			// this resp is an Serializable[] with two messages, one for the
			// connect response and the other for everyone else
			if (o instanceof ConnectRequest) {
				ObjectMessage first = session
						.createObjectMessage(new ConnectResponse(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), resp[0]));
				first.setJMSCorrelationID(omsg.getJMSCorrelationID());
				trace("SERVER.respondToConnectRequest:sending:" + first);
				topicProducer.send(first);
				ObjectMessage second = session
						.createObjectMessage(new JMSMessage(getConnectionID(),
								getLocalID(), null, resp[1]));
				trace("SERVER.respondToConnectRequest:sending:" + second);
				topicProducer.send(second);
			} else if (o instanceof DisconnectRequest) {
				ObjectMessage msg = session
						.createObjectMessage(new DisconnectResponse(
								getConnectionID(), o.getTargetID(), o
										.getSenderID(), null));
				msg.setJMSCorrelationID(omsg.getJMSCorrelationID());
				trace("SERVER.respondToDisconnectRequest:sending:" + msg);
				topicProducer.send(msg);
			}
		} catch (Exception e) {
			// disconnect
			dumpStack("Exception in respondToRequest", e);
			hardDisconnect();
		}
	}

	public Object sendSynch(ID target, byte[] data) throws IOException {
		if (isConnected() && isStarted()) {
			return sendAndWait(new DisconnectRequest(getConnectionID(),
					getLocalID(), target, data), keepAlive);
		}
		return null;
	}
}
