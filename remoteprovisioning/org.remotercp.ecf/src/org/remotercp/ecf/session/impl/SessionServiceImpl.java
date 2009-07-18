package org.remotercp.ecf.session.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessageSender;
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
import org.remotercp.ecf.session.ConnectionDetails;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.roster.RosterUtil;


public class SessionServiceImpl implements ISessionService {

	private ConnectionDetails connectionDetails;

	private IContainer containter;

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());

	public SessionServiceImpl(ConnectionDetails connectionDetails,
			IContainer container) {
		assert connectionDetails != null : "connectionDetails != null";
		assert container != null : "container != null";
		this.connectionDetails = connectionDetails;
		this.containter = container;
	}

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this.containter
				.getAdapter(IPresenceContainerAdapter.class);
		assert adapter != null : "adapter != null";
		return adapter;
	}

	protected synchronized IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) this.containter
				.getAdapter(IRemoteServiceContainerAdapter.class);
		assert adapter != null : "adapter != null";

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
	public synchronized <T> List<T> getRemoteService(Class<T> service, ID[] filterIDs,
			String filter) throws ECFException, InvalidSyntaxException {
		List<T> remoteServices = new ArrayList<T>();

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		/* 1. get available services */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);
		assert refs != null : "Remote service references != null";

		/* 2. get the proxies for found service references */
		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);

			T castedService = service.cast(remoteService.getProxy());
			assert castedService != null : "castedService != null";
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
	public synchronized IRemoteService[] getRemoteServiceReference(Class<?> service,
			ID[] filterIDs, String filter) throws InvalidSyntaxException {
		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);

		IRemoteService serviceReferences[] = new IRemoteService[refs.length];

		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			assert remoteService != null : "remoteService != null";

			serviceReferences[serviceNumber] = remoteService;

		}

		return serviceReferences;
	}

	public IRosterManager getRosterManager() {
		IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		assert rosterManager != null : "rosterManager != null";
		return rosterManager;
	}

	public IRoster getRoster() {
		IRoster roster = getRosterManager().getRoster();
		assert roster != null : "roster != null";
		return roster;
	}

	public IChatManager getChatManager() {
		IChatManager chatManager = this.getPresenceContainerAdapter()
				.getChatManager();
		assert chatManager != null : "chatManager != null";
		return chatManager;
	}

	public IContainer getContainer() {
		assert containter != null : "container != null";
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
	public synchronized void registerRemoteService(String serviceName, Object impl,
			ID[] targetIDs) {

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		if (targetIDs == null) {
			targetIDs = RosterUtil.getUserIDs(getRoster());
		}
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, targetIDs);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		logger.info(">>>> Service Registered: " + serviceName);
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
	public synchronized void ungetRemoteService(ID[] idFilter, String serviceName,
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

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		assert adapter != null : "adapter != null";
		return (T) getContainer().getAdapter(adapter);

	}

	public ID getContainerID() {
		return containter.getID();
	}

	public ID getConnectedID() {
		assert containter.getConnectedID() != null : "containter.getConnectedID() != null";
		return containter.getConnectedID();
	}

	public void addMessageListener(IIMMessageListener listener) {
		assert listener != null : "listener != null";
		getChatManager().addMessageListener(listener);
	}

	public IChatMessageSender getChatMessageSender() {
		return getChatManager().getChatMessageSender();
	}

}
