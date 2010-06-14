package org.remotercp.connection.connection;

import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.osgi.framework.BundleContext;
import org.remotercp.connection.ECFActivator;
import org.remotercp.connection.session.ISessionService;
import org.remotercp.connection.session.impl.SessionServiceImpl;

/**
 * This class can be used in headless application to log-in to a server.
 * 
 * @author Eugen Reiswich
 * @date 16.09.2008
 * 
 */
public class HeadlessConnection {

	private final static Logger logger = Logger
			.getLogger(HeadlessConnection.class.getName());

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
	public static IContainer connect(String userName, String password,
			String server, String protocol) throws IDCreateException,
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

		container.connect(xmppid, connectContext);

		ConnectionDetails connectionDetails = new ConnectionDetails(userName,
				server);

		createSessionService(container, connectionDetails);
		
		

		return container;

	}

	/*
	 * Create and publish an OSGi-sessionService. This service will be used to
	 * register and retrieve remoe services
	 */
	private static void createSessionService(IContainer container,
			ConnectionDetails connectionDetails) {
		ISessionService sessionService = new SessionServiceImpl(
				connectionDetails, container);

		BundleContext bundleContext = ECFActivator.getBundleContext();

		bundleContext.registerService(ISessionService.class.getName(),
				sessionService, null);
		
		Properties props = new Properties();
		props.put("username", connectionDetails.getUserName());
		props.put("server", connectionDetails.getServer());
		
		bundleContext.registerService(IContainer.class.getName(), container, props);
		logger.info(">>>  ContainerService registered");

		logger.info(">>> SessionService registred");
	}

}
