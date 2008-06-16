package org.remotercp.ecf.old;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.IContainerInstantiator;

public class XMPPContainerInstantiator implements IContainerInstantiator {

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {

		// create container
		IContainer container = ContainerFactory.getDefault().createContainer(
				"ecf.generic.client");

		// create target ID for connection
		try {
			ID myID = IDFactory.getDefault().createID(
					container.getConnectNamespace(),
					"http://localhost:9090");

			container.connect(myID, null);
		} catch (IDCreateException e) {
			e.printStackTrace();
		} catch (ContainerConnectException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		// TODO Auto-generated method stub
		return null;
	}

}
