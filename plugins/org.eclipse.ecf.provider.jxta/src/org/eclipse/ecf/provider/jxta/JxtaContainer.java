package org.eclipse.ecf.provider.jxta;

import java.util.Map;

import net.jxta.ext.example.presence.MyNetwork;
import net.jxta.ext.example.presence.MyServices;
import net.jxta.ext.network.GroupEvent;
import net.jxta.ext.network.NetworkEvent;
import net.jxta.ext.network.NetworkListener;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.IChatMessage.Type;

public class JxtaContainer implements IContainer, IChatMessageSender {

	private MyNetwork network;
	private MyServices service;
	private ID ecfID ;
    String id= System.getProperty("user.name") + "-" + System.currentTimeMillis();

    
	public JxtaContainer(ID id) {
		super();
		this.ecfID = id;
		initNetwork();
		initServices();
	}

	private void initNetwork() {
	    network = new MyNetwork(new NetworkListener() {
	        public void notify(NetworkEvent ne) {
	            System.out.println("NetworkEvent: "+ne.getCause()+" groupe: "+ne.getPeerGroup().getPeerGroupName()+" peer: "+ne.getPeerGroup().getPeerName());
	            
	            if (ne.getCause() instanceof GroupEvent) {
	                System.out.println("GroupEvent");
	            }
	        }
	    });
	
	    network.start();
	}

	private void initServices() {
	    service = new MyServices(network);
	}

	public void addListener(IContainerListener listener) {
		// TODO Auto-generated method stub

	}


	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {

	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class serviceType) {
		if (serviceType != null && serviceType.isInstance(this)) {
			return this;
		}
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

	public void sendChatMessage(ID toID, ID threadID, Type type,
			String subject, String body, Map properties) throws ECFException {
		// TODO Auto-generated method stub
		
	}

	public void sendChatMessage(ID toID, String body) throws ECFException {
		// TODO Auto-generated method stub
		
	}


}
