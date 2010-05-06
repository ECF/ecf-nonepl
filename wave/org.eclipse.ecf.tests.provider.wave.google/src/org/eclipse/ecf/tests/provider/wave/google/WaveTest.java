package org.eclipse.ecf.tests.provider.wave.google;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.wave.google.container.Wave;
import org.eclipse.ecf.provider.wave.google.container.WaveClientContainer;
import org.eclipse.ecf.provider.wave.google.identity.WaveID;
import org.eclipse.ecf.provider.wave.google.identity.WaveletID;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public class WaveTest extends ContainerAbstractTestCase {
	WaveClientContainer container;
	WaveID id;

	public void setUp() throws Exception {
		container = (WaveClientContainer) ContainerFactory.getDefault().createContainer("ecf.googlewave.client");
		assertNotNull(container);
		Namespace ns = container.getWaveNamespace();
		assertNotNull(ns);

		id = (WaveID) ns.createInstance(new String[] { "test-domain.de!wave1" });
	}

	public void testAddAndRemoveWavelets() {
		Wave client = new Wave(id);
		WaveletID wavelet = (WaveletID) container.getWaveletNamespace().createInstance(new String[] { "wave://test-domain.de/adsdasd" });
		
		assertNull(client.getWavelet(wavelet));
		assertNotNull(client.createWavelet(wavelet));
		assertEquals(wavelet, client.getWavelet(wavelet).getWaveletId());
		assertEquals(1, client.getWavelets().length);
	}

}
