package org.eclipse.ecf.tests.provider.skype;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class SkypeContainerTest extends ContainerAbstractTestCase {

	protected String getClientContainerName() {
		return Skype.CLIENT_CONTAINER_NAME;
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testCreateSkypeContainer() throws Exception {
		IContainer container = ContainerFactory.getDefault().createContainer(getClientContainerName());
		assertNotNull(container);
	}
}
