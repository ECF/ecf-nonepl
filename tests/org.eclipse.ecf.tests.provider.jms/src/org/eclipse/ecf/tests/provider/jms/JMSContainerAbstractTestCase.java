package org.eclipse.ecf.tests.provider.jms;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public abstract class JMSContainerAbstractTestCase extends ContainerAbstractTestCase {

	@Override
	protected abstract String getClientContainerName();
	
	protected abstract String getServerIdentity();
	
	protected abstract String getServerContainerName();

	protected String getJMSNamespace() {
		return "ecf.namespace.jmsid";
	}
	
	@Override
	protected void setUp() throws Exception {
		setClientCount(1);
		createServerAndClients();
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		cleanUpServerAndClients();
		super.tearDown();
	}
	
	public void testConnectClient() throws Exception {
		IContainer client = getClients()[0];
		ID targetID = IDFactory.getDefault().createID(client.getConnectNamespace(),new Object [] {  getServerIdentity() });
		client.connect(targetID, null);
		Thread.sleep(3000);
	}
}
