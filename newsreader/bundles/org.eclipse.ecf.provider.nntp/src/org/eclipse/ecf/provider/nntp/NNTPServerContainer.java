package org.eclipse.ecf.provider.nntp;

import org.eclipse.ecf.core.BaseContainer;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;

public class NNTPServerContainer extends BaseContainer {

	protected NNTPServerContainer(ID id) {
		super(id);
	}

	public NNTPServerContainer(long idl) throws ContainerCreateException {
		super(idl);
	}	
}
