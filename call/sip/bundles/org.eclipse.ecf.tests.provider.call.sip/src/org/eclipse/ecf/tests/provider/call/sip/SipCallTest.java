/**
 * Aug 21, 2009
 * 6:27:40 PM
 * Administrator
 */
package org.eclipse.ecf.tests.provider.call.sip;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.ecf.provider.call.sip.SipCall;
import org.eclipse.ecf.provider.call.sip.identity.SipLocalParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipRemoteParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipUriID;
import org.eclipse.ecf.provider.call.sip.identity.SipUriNamespace;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class SipCallTest  extends TestCase{
	static SipCall call;
	static SipLocalParticipant localParty;
	static SipRemoteParticipant remoteParty;
	int timeToKeepAlive=1000;

	
	/**
	 * Test method for {@link org.eclipse.ecf.provider.call.sip.SipCall#SipCall()}.
	 */
	@Test
	public void testSipCall() {
		 call=new SipCall();
		assertNotNull(call);
	}

	/**
	 * Test method for {@link org.eclipse.ecf.provider.call.sip.SipCall#connect(org.eclipse.ecf.provider.call.sip.identity.SipLocalParticipant)}.
	 */
	@Test
	public void testConnect() {
		 call=new SipCall();
		 localParty = new SipLocalParticipant(
				(SipUriID) new SipUriNamespace()
						.createInstance(new Object[] { "sip:2233371083@sip2sip.info" }),
				"Harshana Eranga", "4j5yx83hs5","proxy.sipthor.net");
		boolean result=call.connect(localParty);
		assertTrue(result);
		
		try {
			Thread.currentThread().sleep(timeToKeepAlive);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}



}
