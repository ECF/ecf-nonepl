package org.remotercp.ecf;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;

public class ECFConnector implements IContainer {

	// private final Logger logger = Logger
	// .getLogger(ECFConnector.class.getName());

	private IContainer container;

	public void connect(ID targetID, IConnectContext connectContext,
			String containerType) throws IDCreateException,
			ContainerCreateException, ContainerConnectException,
			URISyntaxException {
		this.container = ContainerFactory.getDefault().createContainer(
				containerType);

		/*
		 * XXX: is this the right way to create an ID??
		 */
		ID newID = IDFactory.getDefault().createID(
				container.getConnectNamespace(),
				((ECFUserID) targetID).getConnectionData());

		container.connect(newID, connectContext);
	}

	public void addListener(IContainerListener listener) {
		this.container.addListener(listener);

	}

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		// not supported yet
	}

	public void disconnect() {
		this.container.disconnect();
	}

	public void dispose() {
		this.container.dispose();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class serviceType) {
		return this.container.getAdapter(serviceType);
	}

	public Namespace getConnectNamespace() {
		return this.container.getConnectNamespace();
	}

	public ID getConnectedID() {
		return this.container.getConnectedID();
	}

	public void removeListener(IContainerListener listener) {
		this.container.removeListener(listener);

	}

	public ID getID() {
		return this.container.getID();
	}
}
