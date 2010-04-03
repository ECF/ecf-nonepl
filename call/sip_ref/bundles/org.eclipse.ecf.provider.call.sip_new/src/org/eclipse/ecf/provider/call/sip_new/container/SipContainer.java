package org.eclipse.ecf.provider.call.sip_new.container;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.NameCallback;
import org.eclipse.ecf.core.security.ObjectCallback;

public class SipContainer extends AbstractContainer {
	
	/**
	 * This is the ID of the remote process we are connected. 
	 * Returned via getConnectedID().
	 */
	private ID targetId;
	
	/**
	 * This is the containerId and returned via getID().
	 */
	private ID containerId;
	
	/**
	 * To get a fine grained lock on the class for synchronization.
	 */
	protected Lock connectLock;
	
	protected byte connectionState;
	
	public static final byte DISCONNECTED=0;
	
	public static final byte CONNECTING=1;
	
	public static final byte CONNECTED=1;
	
	
	
	
	public SipContainer()throws IDCreateException {
		super();
		this.containerId = IDFactory.getDefault().createGUID();
		connectLock=new Lock();
	}
	
	public SipContainer(ID id) {
		super();
		Assert.isNotNull(id);
		this.containerId = id;
		connectLock=new Lock();
	}

/**
 * This is where we use to connect to the sip services.
 * In this case target is generated from the initiator Id. Hence TargetId is initiatorId.
 * Initiator Name, Authorization Password, sip proxy server is need to connect. 
 */
	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		if (!targetID.getNamespace().getName().equals(getConnectNamespace().getName()))
			throw new ContainerConnectException("targetID not of appropriate Namespace");

		fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));

		// XXX connect to remote service here
		@SuppressWarnings("unused")
		Credential credential=getSipCredentials(connectContext);
		
	//Pass these to the manager and connect to the service.
		
//wait for it
		this.targetId = targetID;
		fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));

		
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	public Namespace getConnectNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public ID getConnectedID() {
		
		return targetId;
	}

	public ID getID() {
		
		return containerId;
	}

	
	protected Credential getSipCredentials(IConnectContext connectContext) throws ContainerConnectException{
		if (connectContext == null)
			return null;
		final CallbackHandler callbackHandler = connectContext.getCallbackHandler();
		if (callbackHandler == null)
			return null;
		final NameCallback usernameCallback = new NameCallback(SipConnectContextIdPrefix.INITIATOR_NAME_PREFIX);
		final ObjectCallback passwordCallback = new ObjectCallback();
		final ProxyServerCallBack proxyCallback=new ProxyServerCallBack(SipConnectContextIdPrefix.PROXY_SERVER_PREFIX);
		
		try {
			callbackHandler.handle(new Callback[] {usernameCallback, passwordCallback,proxyCallback});
		} catch (Exception e) {
			throw new ContainerConnectException("Non compatible authentication data provided", e);
		}
		final String username = usernameCallback.getName();
		final String password = (String) passwordCallback.getObject();
		final String proxy=proxyCallback.getProxy();
		
		return new Credential(username, password,proxy);
	}
	
	
	static final class Lock{
		//To get the fine grainer lock for synchronization 
	}


	public Lock getConnectLock() {
		return connectLock;
	}

	public void setConnectLock(Lock connectLock) {
		this.connectLock = connectLock;
	}

	public byte getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(byte connectionState) {
		this.connectionState = connectionState;
	}
	
	

}
