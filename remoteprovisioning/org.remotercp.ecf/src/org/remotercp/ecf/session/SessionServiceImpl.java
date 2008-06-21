package org.remotercp.ecf.session;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.ecf.ECFConnector;
import org.remotercp.util.roster.RosterUtil;

public class SessionServiceImpl implements IAdaptable, ISessionService {

	private ConnectionDetails connectionDetails;

	private ECFConnector containter;

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}

	public void setContainer(ECFConnector container) {
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
		return adapter;
	}

	/**
	 * Returns a list of remote service references for a given service. The
	 * given service might me provided by several user though there might be
	 * more than one service available
	 * 
	 * @param <T>
	 *            The service type
	 * @param service
	 *            The needed remote service name
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

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = (IRemoteServiceContainerAdapter) this.containter
				.getAdapter(IRemoteServiceContainerAdapter.class);

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
		 * given rosterIDs in order to filter only those service which are
		 * requested (filterIDs).
		 */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(null, service.getName(), filter);

		// cast the remote service references to proxies
		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);
			Assert.isNotNull(remoteService);

			IRemoteServiceReference remoteServiceReference = refs[serviceNumber];
			ID containerID = remoteServiceReference.getContainerID();
			String containerIDName = containerID.getName();

			for (ID userID : filterIDs) {
				String userIDName = userID.getName();
				if (userIDName.equals(containerIDName)) {

					// get proxy for remote service and add service to the
					// service list
					T castedService = service.cast(remoteService.getProxy());
					// T castedService = (T) remoteService.getProxy();
					Assert.isNotNull(castedService);
					remoteServices.add(castedService);
					break;
				}
			}

		}

		return remoteServices;
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
	 * @param targetIDs
	 *            Buddies, who are to receive service registration. If
	 *            <code>null</code> all buddies in roster will be taken
	 */
	public void registerRemoteService(String serviceName, Object impl,
			ID[] targetIDs) {

		if (targetIDs == null) {
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
}
