package org.eclipse.ecf.tests.provider.wave.google.namespaces;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.wave.google.identity.WaveBackendID;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class WaveBackendNamespaceTest extends ContainerAbstractTestCase {

	IContainer container;

	public void setUp() throws Exception {
		container = ContainerFactory.getDefault().createContainer(
				"ecf.googlewave.client");
		assertNotNull(container);
	}

	public void testCreateInstance() {
		Namespace ns = container.getConnectNamespace();
		assertNotNull(ns);

		WaveBackendID id = (WaveBackendID) ns.createInstance(new String[] { "test@test.de" });
		assertEquals("test@test.de", id.getUserAtDomain());
		assertEquals(9876, id.getPort());
		assertEquals("test.de", id.getHost());

		id = (WaveBackendID) ns.createInstance(new String[] { "test@test.de", "foo.de:9000" });
		assertEquals("test@test.de", id.getUserAtDomain());
		assertEquals(9000, id.getPort());
		assertEquals("foo.de", id.getHost());

		try {
			id = (WaveBackendID) ns.createInstance(new String[] { "", "" });
			fail("Expected IDCreateException");
		} catch (IDCreateException e) {
		}

		try {
			id = (WaveBackendID) ns.createInstance(new String[] { "12312321313", "!$$%%%" });
			fail("Expected IDCreateException");
		} catch (IDCreateException e) {
		}
	}
}
