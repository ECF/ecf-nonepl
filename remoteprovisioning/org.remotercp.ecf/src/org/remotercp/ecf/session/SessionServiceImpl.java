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
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.IPresenceListener;
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
import org.remotercp.ecf.ECFConnector;
import org.remotercp.util.authorization.ExtensionRegistryHelper;
import org.remotercp.util.roster.RosterUtil;

public class SessionServiceImpl implements ISessionService {

	private ConnectionDetails connectionDetails;

	private ECFConnector containter;

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

	public void setContainer(ECFConnector container) {
		this.containter = container;

		// register local presence listener
		this.getRosterManager().addPresenceListener(this.getPresenceListener());
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
	 *            match the given filer. May be null if all services should be
	 *            found
	 * @return A list of remote service proxies
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public <T> List<T> getRemoteService(Class<T> service, ID[] filterIDs,
			String filter) throws ECFException, InvalidSyntaxException {
		List<T> remoteServices = new ArrayList<T>();

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		logger.info("Container status: " + this.containter.toString());

		/*
		 * XXX: according to Scott Lewis the filterIDs have to be containerIDs
		 * and not rosterIDs, therefore the IRemoteServiceReferences are not
		 * properly filtered. Solution needed for this problem! According to
		 * Scott this might become API in the next release
		 */
		// IRemoteServiceReference[] refs = remoteServiceContainerAdapter
		// .getRemoteServiceReferences(filterIDs, service.getName(),
		// filter);
		/*
		 * XXX this is a workaround for the above mentioned problem. The idea is
		 * to get all remote services and ask each serviceReference for the
		 * containerID. Afterwards the containerIDName will be matched with the
		 * given rosterIDName in order to filter only those services which are
		 * requested (filterIDs).
		 */

		/* 1. get all available services */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(null, service.getName(), filter);

		Map<String, T> filteredServices = new HashMap<String, T>();

		/* 2. filter services for the given rosterIDs */
		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			Assert.isNotNull(remoteService);

			String containerIDName = refs[serviceNumber].getContainerID()
					.getName();
			int indexOfContainer = containerIDName.indexOf("@");
			String containerUserName = containerIDName.substring(0,
					indexOfContainer);

			for (ID userID : filterIDs) {
				String userIDName = userID.getName();
				/*
				 * XXX workaround for container and roster IDs. Split user names
				 * and compare only names. This is dangerous as the same user
				 * can be connected to different XMPP servers. Solution needed!
				 */
				int indexOfUser = userIDName.indexOf("@");
				String userName = userIDName.substring(0, indexOfUser);

				if (containerUserName.equals(userName)) {

					// get proxy for remote service and add service to the
					// service list
					T castedService = service.cast(remoteService.getProxy());
					Assert.isNotNull(castedService);

					/*
					 * XXX: next workaround. If a user connects and disconnects
					 * several times user services will also be registered
					 * several times. Asking for a specific service for a user
					 * in this method may result in multiple services. A map
					 * will avoid multiple services and return only one service
					 * per user.
					 */
					filteredServices.put(userIDName, castedService);
					break;
				}
			}

		}

		remoteServices.addAll(filteredServices.values());
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
		IRemoteService serviceReferences[] = null;
		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(null, service.getName(), filter);

		Map<String, IRemoteService> filteredServices = new HashMap<String, IRemoteService>();

		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			Assert.isNotNull(remoteService);

			String containerIDName = refs[serviceNumber].getContainerID()
					.getName();
			int indexOfContainer = containerIDName.indexOf("@");
			String containerUserName = containerIDName.substring(0,
					indexOfContainer);

			for (ID userID : filterIDs) {
				String userIDName = userID.getName();

				int indexOfUser = userIDName.indexOf("@");
				String userName = userIDName.substring(0, indexOfUser);

				if (containerUserName.equals(userName)) {

					filteredServices.put(userIDName, remoteService);
					break;
				}
			}

		}
		serviceReferences = filteredServices.values().toArray(
				new IRemoteService[filteredServices.size()]);
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

		// store the service locally in order to provide this service to new
		// connected user
		if (!this.remoteServices.containsKey(serviceName)) {
			this.remoteServices.put(serviceName, impl);
		}

		if (targetIDs == null) {
			// register service to all user
			targetIDs = RosterUtil.getUserIDs(getRoster());
			Assert.isNotNull(targetIDs);
		}

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
	 * Register services to clients that just logged in.
	 * 
	 * TODO: this is a workaround!!! As the admin will usually login when other
	 * users are already connected he won't be able to use the remote services
	 * as the registration of these services happens at the login time.
	 * Therefore we push here services to just logged in users.
	 * 
	 * Try to figure out if the ECF DiscoveryService will do here better work.
	 */
	protected IPresenceListener getPresenceListener() {
		return new IPresenceListener() {

			/*
			 * Register local services to new connected user
			 * 
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ecf.presence.IPresenceListener#handlePresence(org
			 * .eclipse.ecf.core.identity.ID,
			 * org.eclipse.ecf.presence.IPresence)
			 */
			public void handlePresence(ID fromID, IPresence presence) {

				if (presence.getType() == IPresence.Type.AVAILABLE) {

					if (!servicesInitialized) {
						/*
						 * wait 3 sec so that all local services are initialized
						 * before registering them to remote clients
						 */
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						servicesInitialized = true;
					}

					boolean authorized = checkAuthorization(fromID);
					if (authorized) {
						// perform remote method registration
						for (String service : SessionServiceImpl.this.remoteServices
								.keySet()) {

							Object serviceImpl = SessionServiceImpl.this.remoteServices
									.get(service);

							SessionServiceImpl.this.registerRemoteService(
									service, serviceImpl, new ID[] { fromID });

							logger.info("Service " + service
									+ " registered to user: "
									+ fromID.getName());
						}
					}

				}
			}

		};
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
			List<Object> executablesForExtensionPoint = ExtensionRegistryHelper
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
