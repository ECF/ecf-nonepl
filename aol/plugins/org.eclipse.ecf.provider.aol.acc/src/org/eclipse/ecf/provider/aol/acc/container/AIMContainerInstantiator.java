package org.eclipse.ecf.provider.aol.acc.container;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;

public class AIMContainerInstantiator extends BaseContainerInstantiator {

	public AIMContainerInstantiator() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
		try {
			return new AIMContainer(IDFactory.getDefault().createGUID());
		} catch (final Exception e) {
			throw new ContainerCreateException("Exception creating AIM container", e);
		}
	}
}
