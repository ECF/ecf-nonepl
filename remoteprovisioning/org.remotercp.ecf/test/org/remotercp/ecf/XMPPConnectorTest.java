package org.remotercp.ecf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.remotercp.ecf.old.XMPPConnector;

public class XMPPConnectorTest {
	@Test
	public void testXMPPConnector() {
		XMPPConnector connector = new XMPPConnector();
		boolean connectionEstablished = connector.connect("eugen", "eugen",
				"127.0.0.1");

		assertEquals(true, connectionEstablished);

		connectionEstablished = connector.connect("eugen", "wrongpassword",
				"127.0.0.1");
		assertEquals(false, connectionEstablished);
	}
}
