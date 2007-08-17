package org.eclipse.ecf.tests.provider.jms.weblogic;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class JMSContainerTestCase extends ContainerAbstractTestCase {

	@Override
	protected String getClientContainerName() {
		return "ecf.jms.weblogic.client";
	}
	
	@Override
	protected void setUp() throws Exception {
		clients = createClients();
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		cleanUpClients();
		super.tearDown();
	}
	
	public void testConnectClient() throws Exception {
		IContainer client = getClients()[0];
		ID targetID = IDFactory.getDefault().createID(client.getConnectNamespace(),new Object [] { "t3://localhost:7001/exampleTopic"});
		client.connect(targetID, null);
		Thread.sleep(3000);
	}
}
