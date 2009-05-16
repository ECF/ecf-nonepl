package org.remotercp.ecf.session;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.authorization.IOperationAuthorization;
import org.remotercp.util.authorization.AuthorizationUtil;

public class SessionServiceImpl implements ISessionService {

	private ConnectionDetails connectionDetails;

	private IContainer containter;

	private Map<String, Object> remoteServices = new HashMap<String, Object>();

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());

	private boolean servicesInitialized = false;

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}

	public void setContainer(IContainer container) {
		this.containter = container;

	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this.containter
				.getAdapter(IPresenceContainerAdapter.class);
		Assert.isNotNull(adapter);
		return adapter;
	}

	protected IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) this.containter
				.getAdapter(IRemoteServiceContainerAdapter.class);
		Assert.isNotNull(adapter);

		adapter.addRemoteServiceListener(new IRemoteServiceListener() {

			public void handleServiceEvent(IRemoteServiceEvent event) {
				// logger.info("Remote service event occured: " + event);
			}

		});
		return adapter;
	}

	/**
	 * Returns a list of remote service proxies for a given service. The given
	 * service might me provided by several users though there might be more
	 * than one service available. Use filterIDs and filter to delimit the
	 * amount of services.
	 * 
	 * @param <T>
	 *            The service type
	 * @param service
	 *            The needed remote service name. (Use yourinterface.class)
	 * @param filterIDs
	 *            User IDs work as a filter though remote services will be
	 *            limited to the given user. May be null if the service should
	 *            be get for all users.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filter. May be null if all services should be
	 *            found
	 * @return A list of remote service proxies
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public <T> List<T> getRemoteService(Class<T> service, ID[] filterIDs,
			String filter) throws ECFException, InvalidSyntaxException {
		List<T> remoteServices = new ArrayList<T>();

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		/* 1. get all available services */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);

		/* 2. filter services for the given rosterIDs */
		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			Assert.isNotNull(remoteService);

			T castedService = service.cast(remoteService.getProxy());
			Assert.isNotNull(castedService);
			remoteServices.add(castedService);
		}

		return remoteServices;
	}

	/**
	 * The above method getRemoteService(...) is easy to use as methods can be
	 * performed directly on the returned proxy. However in some scenarios e.g.
	 * update, install operations it might take a long time to perform a remote
	 * operation (features have to be downloaded first etc). Methods performed
	 * on a proxy have a default time out of 30 sec. which is not customizable.
	 * Therefore the above method can't be used in some scenarios and we have to
	 * use this method to get a service reference and perform an
	 * {@link IRemoteCall} with a user defined time out.
	 * 
	 * XXX: this method contains same workarounds as the above mentioned method.
	 * 
	 * @param service
	 *            The service name to get a remote service of
	 * @param filterIDs
	 *            User Ids to get a remote service for
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found
	 * @return An array of remote services for given user and filter
	 * @throws InvalidSyntaxException
	 */
	public IRemoteService[] getRemoteServiceReference(Class service,
			ID[] filterIDs, String filter) throws InvalidSyntaxException {
		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);

		IRemoteService serviceReferences[] = new IRemoteService[refs.length];

		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			Assert.isNotNull(remoteService);

			serviceReferences[serviceNumber] = remoteService;

		}

		return serviceReferences;
	}

	public IRosterManager getRosterManager() {
		IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		Assert.isNotNull(rosterManager);
		return rosterManager;
	}

	public IRoster getRoster() {
		IRoster roster = getRosterManager().getRoster();
		Assert.isNotNull(roster);
		return roster;
	}

	public IChatManager getChatManager() {
		IChatManager chatManager = this.getPresenceContainerAdapter()
				.getChatManager();
		Assert.isNotNull(chatManager);
		return chatManager;
	}

	public IContainer getContainer() {
		Assert.isNotNull(containter);
		return this.containter;
	}

	/**
	 * Registers a service as remote service for OSGi over ECF
	 * 
	 * @param classType
	 *            The service name
	 * @param impl
	 *            The service implementation
	 * @param targetIDs
	 *            Buddies, who are to receive service registration. If
	 *            <code>null</code> all buddies in roster will be taken
	 */
	public void registerRemoteService(String serviceName, Object impl,
			ID[] targetIDs) {

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, targetIDs);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		logger.info("Service Registered: " + serviceName);
	}

	/**
	 * Unget a remote service. This operation should actually be called if a
	 * client disconnects. Ask ECF devs if this happens. If yes, delete this
	 * method.
	 * 
	 * @param idFilter
	 *            The user id array for which the service should be unget
	 * @param service
	 *            The service interface class
	 * @param filter
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public void ungetRemoteService(ID[] idFilter, String serviceName,
			String filter) throws ECFException, InvalidSyntaxException {

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = (IRemoteServiceContainerAdapter) this.containter
				.getAdapter(IRemoteServiceContainerAdapter.class);

		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(idFilter, serviceName, filter);

		if (refs != null) {
			// unget the remote service
			for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {
				remoteServiceContainerAdapter
						.ungetRemoteService(refs[serviceNumber]);
				logger.info("Unget service: " + serviceName);
			}
		}
	}

	/*
	 * This method checks whether the provided user has the authorization to
	 * receive remote method registrations. This check will only be performed if
	 * an authorization extension has been provided.
	 * 
	 * @param fromId The user ID to check the authorization for
	 * 
	 * @return True if user is authorized to receive remote service
	 * registrations, otherwise false
	 */
	private boolean checkAuthorization(ID fromId) {
		boolean authorized = false;
		try {
			List<Object> executablesForExtensionPoint = AuthorizationUtil
					.getExecutablesForExtensionPoint("org.remotercp.authorization");
			if (executablesForExtensionPoint.isEmpty()) {
				// no extensions for extension point provided, ignore
				// authorization
				authorized = true;
			} else {
				for (Object executable : executablesForExtensionPoint) {
					if (executable instanceof IOperationAuthorization) {
						IOperationAuthorization operation = (IOperationAuthorization) executable;
						authorized = operation.canExecute(fromId,
								"registerRemoteServices");
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return authorized;
	}
}
