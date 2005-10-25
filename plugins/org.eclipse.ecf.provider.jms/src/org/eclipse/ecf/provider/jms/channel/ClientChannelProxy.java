/**
 * 
 */
package org.eclipse.ecf.provider.jms.channel;

import java.io.IOException;
import java.util.Map;

import org.activemq.transport.TransportChannel;
import org.eclipse.ecf.core.comm.IConnectionEventHandler;
import org.eclipse.ecf.core.comm.ISynchAsynchConnection;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.jms.container.JMSServerSOContainer;

/**
 * @author slewis
 *
 */
public class ClientChannelProxy implements ISynchAsynchConnection {

	JMSServerSOContainer container;
	ISynchAsynchConnection aconn;
	ID remoteID;
	TransportChannel channel;
	boolean started;
	
	public ClientChannelProxy(JMSServerSOContainer container, ISynchAsynchConnection aconn, ID remote) {
		super();
		this.container = container;
		this.aconn = aconn;
		this.remoteID = remote;
	}

	public void start() {
		started = true;
	}

	public void sendAsynch(ID receiver, byte[] data) throws IOException {
		if (!isConnected() || !isStarted()) {
			throw new IOException("connection not started");
		}
		aconn.sendAsynch(receiver,data);
	}

	public Object connect(ID remote, Object data, int timeout) throws IOException {
		throw new IOException("cannot connect via this proxy");
	}

	public void disconnect() throws IOException {
		stop();
		aconn = null;
	}

	public boolean isConnected() {
		return aconn.isConnected();
	}

	public ID getLocalID() {
		return aconn.getLocalID();
	}

	public void stop() {
		started = false;
	}

	public boolean isStarted() {
		return started;
	}

	public Map getProperties() {
		return null;
	}

	public void addCommEventListener(IConnectionEventHandler listener) {
	}

	public void removeCommEventListener(IConnectionEventHandler listener) {
	}

	public Object getAdapter(Class clazz) {
		return null;
	}

	public Object sendSynch(ID receiver, byte[] data) throws IOException {
		if (!isConnected() || !isStarted()) {
			throw new IOException("proxy connection not started");
		}
		return aconn.sendSynch(receiver,data);
	}
}
