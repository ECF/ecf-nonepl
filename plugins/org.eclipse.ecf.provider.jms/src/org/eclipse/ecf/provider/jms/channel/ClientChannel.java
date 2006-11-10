/**
 * 
 */
package org.eclipse.ecf.provider.jms.channel;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.net.ConnectException;

import javax.jms.ObjectMessage;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;
import org.eclipse.ecf.provider.comm.SynchEvent;
import org.eclipse.ecf.provider.jms.identity.JMSID;

public class ClientChannel extends Channel implements ISynchAsynchConnection {
	private static final long serialVersionUID = -1381571376210849678L;

	public ClientChannel(ISynchAsynchEventHandler handler,
			int keepAlive) {
		super(handler, keepAlive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.comm.IConnection#connect(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object, int)
	 */
	public synchronized Object connect(ID remote, Object data, int timeout)
			throws IOException {
		if (connected)
			throw new ConnectException("already connected");
		if (remote == null)
			throw new ConnectException("remote target cannot be null");
		if (!(remote instanceof JMSID))
			throw new ConnectException("remote " + remote.getName()
					+ " not JMS ID");
		managerID = (JMSID) remote;
		url = managerID.getName();
		topicName = removeLeadingSlashes(url);

		if (!(data instanceof Serializable)) {
			throw new NotSerializableException("data are not serializable");
		}
		Serializable connectData = (Serializable) data;
		trace("connect(" + remote + "," + data + "," + timeout);
		setup();
		Serializable res = getConnectResult(managerID, connectData);
		if (res != null && (!(res instanceof ConnectResponse))) {
			throw new ConnectException("Invalid response");
		}
		if (res == null)
			throw new ConnectException("server refused connection");
		ConnectResponse cr = (ConnectResponse) res;
		return cr.getData();
	}

	protected Serializable getConnectResult(ID managerID, Serializable data)
			throws IOException {
		return sendAndWait(new ConnectRequest(getConnectionID(), getLocalID(),
				managerID, data));
	}

	protected void respondToRequest(ObjectMessage omsg, ECFMessage o) {
		trace("respondToRequest(" + o + ")");
		try {
			ObjectMessage first = session
					.createObjectMessage(new DisconnectResponse(
							getConnectionID(), o.getTargetID(),
							o.getSenderID(), null));
			first.setJMSCorrelationID(omsg.getJMSCorrelationID());
			trace("CLIENT.respondToConnectRequest:sending:" + first);
			topicProducer.send(first);
			handler
					.handleSynchEvent(new SynchEvent(this, o
							.getData()));
		} catch (Exception e) {
			// disconnect
			dumpStack("Exception in respondToRequest", e);
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
