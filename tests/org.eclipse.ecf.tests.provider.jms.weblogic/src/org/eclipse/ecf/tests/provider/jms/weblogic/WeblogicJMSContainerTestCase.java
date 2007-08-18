package org.eclipse.ecf.tests.provider.jms.weblogic;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.tests.provider.jms.JMSContainerAbstractTestCase;

public class WeblogicJMSContainerTestCase extends JMSContainerAbstractTestCase {

	@Override
	protected String getClientContainerName() {
		return Weblogic.CLIENT_CONTAINER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.provider.jms.JMSContainerAbstractTestCase#getServerContainerName()
	 */
	@Override
	protected String getServerContainerName() {
		return Weblogic.SERVER_CONTAINER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.provider.jms.JMSContainerAbstractTestCase#getServerIdentity()
	 */
	@Override
	protected String getServerIdentity() {
		return Weblogic.TARGET_NAME;
	}

	public void testConnectClient() throws Exception {
		IContainer client = getClients()[0];
		ID targetID = IDFactory.getDefault().createID(client.getConnectNamespace(),new Object [] { getServerIdentity() });
		client.connect(targetID, null);
		Thread.sleep(3000);
	}

}
