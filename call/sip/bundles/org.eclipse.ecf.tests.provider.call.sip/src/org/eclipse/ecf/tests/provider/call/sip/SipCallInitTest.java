/**
 * Aug 22, 2009
 * 9:10:49 PM
 * Administrator
 */
package org.eclipse.ecf.tests.provider.call.sip;

import junit.framework.TestCase;

import org.eclipse.ecf.internal.tests.provider.call.sip.UserSettings;
import org.eclipse.ecf.provider.call.sip.SipCall;
import org.eclipse.ecf.provider.call.sip.identity.SipLocalParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipRemoteParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipUriID;
import org.eclipse.ecf.provider.call.sip.identity.SipUriNamespace;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Administrator
 * 
 */
public class SipCallInitTest extends TestCase {
	static SipCall call;
	static SipLocalParticipant localParty;
	static SipRemoteParticipant remoteParty;
	int timeToKeepAlive = 15000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	protected void setUp() throws Exception {

		localParty = new SipLocalParticipant(
				(SipUriID) new SipUriNamespace()
						.createInstance(new Object[] { UserSettings.LOCAL_ECF_TESTER_USER_NAME }),
				UserSettings.LOCAL_ECF_TESTER_NAME,
				UserSettings.LOCAL_ECF_TESTER_PASSWORD,
				UserSettings.LOCAL_ECF_TESTER_OUTBOUND_PROXY_NAME);

		remoteParty = new SipRemoteParticipant(
				(SipUriID) new SipUriNamespace()
						.createInstance(new Object[] { UserSettings.REMOTE_ECF_TESTER_USER_NAME }),
				UserSettings.REMOTE_ECF_SIP_TESTER_NAME);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.provider.call.sip.SipCall#initiateCall(org.eclipse.ecf.provider.call.sip.identity.SipRemoteParticipant)}
	 * .
	 */
	@Test
	public void testInitiateCallSipRemoteParticipant() {
		boolean result = false;
		try {

			call = new SipCall(localParty);
			result = call.initiateCall(remoteParty);

			while (!call.isActiveCall()) {
				// Just waiting till call initiated

			}

			// Give 15 secs to verify it's working
			Thread.currentThread().sleep(timeToKeepAlive);

			call.terminateOutGoingCall(0);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(result);
	}

}
