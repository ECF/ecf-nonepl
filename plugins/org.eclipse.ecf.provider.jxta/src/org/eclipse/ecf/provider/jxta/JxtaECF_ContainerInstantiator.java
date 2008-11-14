package org.eclipse.ecf.provider.jxta;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;

/**
 * @author pierre
 *
 */
public class JxtaECF_ContainerInstantiator implements IContainerInstantiator {

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			
			return new JxtaContainer(IDFactory.getDefault().createGUID());
		} catch (IDCreateException e) {
			throw new ContainerCreateException("erreur à la création du container:", e);
		}
	}

	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return new String[] { 
				IPresenceContainerAdapter.class.getName(),
				 };
	}

	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		return null;
	}

}
