package org.eclipse.ecf.tests.provider.wave.google.namespaces;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.wave.google.container.WaveClientContainer;
import org.eclipse.ecf.provider.wave.google.identity.WaveID;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class WaveNamespaceTest extends ContainerAbstractTestCase {

	WaveClientContainer container;

	public void setUp() throws Exception {
		container = (WaveClientContainer) ContainerFactory.getDefault().createContainer(
				"ecf.googlewave.client");
		assertNotNull(container);
	}

	public void testCreateInstance() {
		Namespace ns = container.getWaveNamespace();
		assertNotNull(ns);

		WaveID id = (WaveID) ns.createInstance(new String[] { "test-domain.de!wave1" });
		assertEquals("test-domain.de", id.getWaveDomain());
		assertEquals("wave1", id.getWaveIdName());
		
		id = (WaveID) ns.createInstance(new String[] { "test-domain.de", "wave2" });
		assertEquals("test-domain.de", id.getWaveDomain());
		assertEquals("wave2", id.getWaveIdName());
	}
}
