package org.remotercp.provisioning.features;

import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.junit.After;
import org.remotercp.ecf.ECFConnector;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.ecf.ECFNamespace;

public class TestRemoteMethodInvocation {
	private IRemoteServiceContainerAdapter remoteAdapter1;
	private IRemoteServiceContainerAdapter remoteAdapter2;

	private ECFConnector connection1;
	private ECFConnector connection2;

	// @Before
	public void setupRemoteConnection() {
		// Bundle bundle =
		// Platform.getBundle("org.remotercp.provisioning.update");
		// try {
		// bundle.start();
		// } catch (BundleException e) {
		// e.printStackTrace();
		// fail();
		// }

		String server = "jabber-server.de";
		connection1 = createUserConnection(server, "eugenpeugen", "scheisse");

		connection2 = createUserConnection(server, "partyalarm", "scheisse");

		// assertNotNull(connection1);
		// assertNotNull(connection2);

		IPresenceContainerAdapter adapter1 = (IPresenceContainerAdapter) connection1
				.getAdapter(IPresenceContainerAdapter.class);

		IPresenceContainerAdapter adapter2 = (IPresenceContainerAdapter) connection2
				.getAdapter(IPresenceContainerAdapter.class);

		// assertNotNull(adapter1);
		// assertNotNull(adapter2);

		remoteAdapter1 = (IRemoteServiceContainerAdapter) connection1
				.getAdapter(IRemoteServiceContainerAdapter.class);

		remoteAdapter2 = (IRemoteServiceContainerAdapter) connection2
				.getAdapter(IRemoteServiceContainerAdapter.class);

		// assertNotNull(remoteAdapter1);
		// assertNotNull(remoteAdapter2);

		IRoster roster1 = adapter1.getRosterManager().getRoster();
		IRoster roster2 = adapter2.getRosterManager().getRoster();

		// assertNotNull(roster1);
		// assertNotNull(roster2);

		ID[] targetIDs = new ID[2];
		targetIDs[0] = roster1.getUser().getID();
		targetIDs[1] = roster2.getUser().getID();

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, targetIDs);

		// register the remote service
		remoteAdapter1.registerRemoteService(
				new String[] { IRemoteServiceTest.class.getName() },
				new RemoteServiceTestImpl(), props);

		remoteAdapter2.registerRemoteService(
				new String[] { IRemoteServiceTest.class.getName() },
				new RemoteServiceTestImpl(), props);

	}

	@After
	public void shutDownServices() {
		this.connection1.disconnect();
		this.connection2.disconnect();
	}

	private ECFConnector createUserConnection(String server, String user,
			String password) {
		ECFConnector connector = new ECFConnector();
		String[] parameter = new String[] { user, server };

		ECFNamespace namespace = new ECFNamespace();

		try {
			ID targetID = namespace.createInstance(parameter);
			IConnectContext connectContext = ConnectContextFactory
					.createUsernamePasswordConnectContext(user, password);

			connector.connect(targetID, connectContext, ECFConstants.XMPP);
		} catch (IDCreateException e) {
			e.printStackTrace();
			fail();
		} catch (ContainerCreateException e) {
			e.printStackTrace();
			fail("Unable to create container");
		} catch (ContainerConnectException e) {
			e.printStackTrace();
			fail("Unable to establish connection");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("Wrong URI Syntax");
		}

		return connector;
	}

}
