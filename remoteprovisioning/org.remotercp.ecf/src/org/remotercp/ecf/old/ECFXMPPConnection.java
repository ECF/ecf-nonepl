package org.remotercp.ecf.old;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;

public class ECFXMPPConnection {

	public static void main(String[] args) {
		try {
			IContainer container = ContainerFactory.getDefault()
					.createContainer("ecf.xmpp.smack");
			ID targetID = IDFactory.getDefault().createID(
					container.getConnectNamespace(), "127.0.0.1:9090");
			container.connect(targetID, null);
			IPresenceContainerAdapter pca = (IPresenceContainerAdapter) container
					.getAdapter(IPresenceContainerAdapter.class);
			
			IRosterManager rosterManager = pca.getRosterManager();
			IRoster roster = rosterManager.getRoster();
			
		} catch (ContainerCreateException e) {
			e.printStackTrace();
		} catch (IDCreateException e) {
			e.printStackTrace();
		} catch (ContainerConnectException e) {
			e.printStackTrace();
		}
	}
}
