package org.remotercp.login.connection;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.ecf.session.ConnectionDetails;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.login.LoginActivator;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * This class can be used in headless application to log-in to a server.
 * 
 * @author Eugen Reiswich
 * @date 16.09.2008
 * 
 */
public class HeadlessConnection {

	/**
	 * Creates a connection to an server.
	 * 
	 * @param userName
	 *            The user name
	 * @param password
	 *            The user password
	 * @param server
	 *            The server url as string (e.g. myserver.com)
	 * @param protocol
	 *            The type of protocol
	 * @see {@link ECFConstants}
	 * @throws IDCreateException
	 * @throws ContainerCreateException
	 * @throws ContainerConnectException
	 * @throws URISyntaxException
	 */
	public static void connect(String userName, String password, String server,
			String protocol) throws IDCreateException,
			ContainerCreateException, ContainerConnectException,
			URISyntaxException {
		/*
		 * Establish the server connection
		 */
		IContainer container = ContainerFactory.getDefault().createContainer(
				protocol);

		XMPPID xmppid = new XMPPID(container.getConnectNamespace(), userName
				+ "@" + server);
		xmppid.setResourceName("" + System.currentTimeMillis());

		IConnectContext connectContext = ConnectContextFactory
				.createUsernamePasswordConnectContext(userName, password);

		ConnectionDetails connectionDetails = new ConnectionDetails(userName,
				server);

		container.connect(xmppid, connectContext);

		ISessionService session = OsgiServiceLocatorUtil.getOSGiService(
				LoginActivator.getBundleContext(), ISessionService.class);
		session.setConnectionDetails(connectionDetails);
		session.setContainer(container);

	}

}
