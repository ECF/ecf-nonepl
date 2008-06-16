package org.remotercp.ecf.old;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.ObjectCallback;
import org.jivesoftware.smack.packet.Session;

public class XMPPContainer implements IContainer {

	private XMPPID targetId;
	
	private Session session;
	
	private XMPPPresenceContainer presenceContainer;

	public void addListener(IContainerListener listener) {
		// TODO Auto-generated method stub

	}

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		this.targetId = (XMPPID) targetID;
		String password = getPassword(connectContext);
		
		this.session = new Session();
	

	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class serviceType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Namespace getConnectNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public ID getConnectedID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeListener(IContainerListener listener) {
		// TODO Auto-generated method stub

	}

	public ID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getPassword(IConnectContext connectContext) {
		// Get password via callback in connectContext
		String pw = null;
		try {
			Callback[] callbacks = new Callback[1];
			callbacks[0] = new ObjectCallback();
			if (connectContext != null) {
				CallbackHandler handler = connectContext.getCallbackHandler();
				if (handler != null) {
					handler.handle(callbacks);
				}
			}
			ObjectCallback cb = (ObjectCallback) callbacks[0];
			pw = (String) cb.getObject();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return pw;
	}

}
