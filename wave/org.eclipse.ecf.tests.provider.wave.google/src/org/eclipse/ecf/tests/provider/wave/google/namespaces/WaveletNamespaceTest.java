package org.eclipse.ecf.tests.provider.wave.google.namespaces;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.wave.google.container.WaveClientContainer;
import org.eclipse.ecf.provider.wave.google.identity.WaveletID;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class WaveletNamespaceTest extends ContainerAbstractTestCase {

	WaveClientContainer container;

	public void setUp() throws Exception {
		container = (WaveClientContainer) ContainerFactory.getDefault().createContainer(
				"ecf.googlewave.client");
		assertNotNull(container);
	}

	public void testCreateInstance() {
		Namespace ns = container.getWaveletNamespace();
		assertNotNull(ns);

		WaveletID id = (WaveletID) ns.createInstance(new String[] { "wave://test-domain.de/test-domain2.de!asdsada/asdasdsad" });
		assertEquals("test-domain.de", id.getWaveletDomain());
		assertEquals("asdasdsad", id.getWaveletId());
		
		id = (WaveletID) ns.createInstance(new String[] { "wave://test-domain.de/asdsada/asdasdsad" });
		assertEquals("test-domain.de", id.getWaveletDomain());
		assertEquals("asdasdsad", id.getWaveletId());
	}

}
