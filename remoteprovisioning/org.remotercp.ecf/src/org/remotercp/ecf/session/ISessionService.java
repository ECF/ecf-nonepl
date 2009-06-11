package org.remotercp.ecf.session;

import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.osgi.framework.InvalidSyntaxException;

public interface ISessionService {

	public ConnectionDetails getConnectionDetails();

	public void setConnectionDetails(ConnectionDetails connectionDetails);

	public void setContainer(IContainer container);

	public IRosterManager getRosterManager();

	public IRoster getRoster();

	public IChatMessageSender getChatMessageSender();

	public void addMessageListener(IIMMessageListener listener);

	public ID getContainerID();

	public ID getConnectedID();

	public <T> T getAdapter(Class<T> adapter);

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
			ID[] targetIDs);

	/**
	 * 
	 * @param idFilter
	 *            The user id array for which the service should be unget
	 * @param service
	 *            The service interface class
	 * @param filter
	 * @throws ECFException
	 */
	public void ungetRemoteService(ID[] idFilter, String serviceName,
			String filter) throws ECFException, InvalidSyntaxException;

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
			String filter) throws ECFException, InvalidSyntaxException;

	/**
	 * The above method getRemoteService(...) is easy to use as methods can be
	 * performed directly on the returned Interface. However in some scenarios
	 * e.g. update, install operations it might take a long time to perform a
	 * remote operation (features have to be downloaded first etc). Methods
	 * performed on a proxy have a default time out of 30 sec. which is not
	 * customizable. Therefore the above method can't be used in some scenarios
	 * and we have to use this method to get a service reference and perform an
	 * {@link IRemoteCall} with a user defined time out.
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
	public IRemoteService[] getRemoteServiceReference(Class<?> service,
			ID[] filterIDs, String filter) throws InvalidSyntaxException;
}
