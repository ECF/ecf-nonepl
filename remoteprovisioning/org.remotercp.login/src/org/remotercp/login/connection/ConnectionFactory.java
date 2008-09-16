package org.remotercp.login.connection;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.remotercp.ecf.ECFConnector;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.ecf.ECFNamespace;
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
public class ConnectionFactory {

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
		ECFConnector connector = new ECFConnector();
		String[] parameter = new String[] { userName, server };
		ECFNamespace namespace = new ECFNamespace();

		ID targetID = namespace.createInstance(parameter);
		IConnectContext connectContext = ConnectContextFactory
				.createUsernamePasswordConnectContext(userName, password);

		connector.connect(targetID, connectContext, protocol);
		ConnectionDetails connectionDetails = new ConnectionDetails(userName,
				server);

		ISessionService session = OsgiServiceLocatorUtil.getOSGiService(
				LoginActivator.getBundleContext(), ISessionService.class);
		session.setConnectionDetails(connectionDetails);
		session.setContainer(connector);
	}

}
