package org.eclipse.ecf.provider.call.sip_new;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.call.sip_new.container.Credential;

public class SipManager {
	
	public static SipStack sipStack; 
	public static SipProvider sipProvider;
	public static SipListener sipListner;
	public static SipFactory sipFactory;
	public static String pathName="gov.nist";
	
	public SipManager() {
		sipFactory=SipFactory.getInstance();
		sipFactory.setPathName(pathName);		
	}
	
	public boolean connect(ID targetId,Credential credential){
		
		try {
			sipStack=sipFactory.createSipStack(generateProperties(credential.getProxyServer()));
			
			ListeningPoint udpListeningPoint=sipStack.createListeningPoint(InetAddress.getLocalHost().getHostAddress(), 5060, "udp");
			sipProvider = sipStack.createSipProvider(udpListeningPoint);
			sipListner=SipListenerImpl.getInstance();
			((SipListenerImpl)sipListner).setTargetToConnect(sipProvider,targetId, credential);
			sipProvider.addSipListener(sipListner);
			
			//Now send a REGISTER via UAC of listenerImpl 
			
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	Properties generateProperties(final String sipProxy) throws ContainerConnectException{
		
		Properties properties = new Properties();

		properties.setProperty("javax.sip.OUTBOUND_PROXY", sipProxy + "/" + "UDP".toLowerCase());
		properties.setProperty("javax.sip.STACK_NAME", "Eclipse ECF Sip Stack");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "Sip Stack Debug.txt");
		try {
			properties.setProperty("javax.sip.IP_ADDRESS", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			throw new ContainerConnectException("Can not connect because local ip retrival failed",e);
		}
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "Sip Client Server.txt");
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS", "false");
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "ERROR");
		
		
		return properties;
	}

}
