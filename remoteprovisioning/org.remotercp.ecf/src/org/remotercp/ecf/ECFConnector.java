package org.remotercp.ecf;

import java.util.logging.Logger;

import org.eclipse.ecf.core.IContainer;
import org.osgi.framework.BundleContext;
import org.remotercp.ecf.session.ConnectionDetails;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.ecf.session.impl.SessionServiceImpl;

public class ECFConnector {

	private final static Logger logger = Logger.getLogger(ECFConnector.class
			.getName());

	public static void createConnection(
			ConnectionDetails connectionDetails, IContainer container) {

		ISessionService sessionService = new SessionServiceImpl(
				connectionDetails, container);

		BundleContext bundleContext = ECFActivator.getBundleContext();

		bundleContext.registerService(ISessionService.class.getName(),
				sessionService, null);

		logger.info(">>> SessionService registred");
	}
}
