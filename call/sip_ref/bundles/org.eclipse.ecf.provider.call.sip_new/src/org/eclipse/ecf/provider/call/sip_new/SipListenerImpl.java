package org.eclipse.ecf.provider.call.sip_new;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.call.sip_new.container.Credential;

public class SipListenerImpl implements SipListener {
	private final SipUserAgentServer UAS;
	private final SipUserAgentClient UAC;
	private Credential credential;
	private ID targetId;
	private SipProvider sipProvider;

	private SipListenerImpl() {
		super();
		UAS = new SipUserAgentServer();
		UAC = new SipUserAgentClient();
	}

	private static class SipListenerImplHolder {
		private static final SipListenerImpl INSTANCE = new SipListenerImpl();
	}

	public static SipListenerImpl getInstance() {
		return SipListenerImplHolder.INSTANCE;
	}
	
	public void setTargetToConnect(final SipProvider sipProvider, final ID targetId,final Credential credential){
		this.sipProvider=sipProvider;
		this.targetId=targetId;
		this.credential=credential;
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {

	}

	public void processIOException(IOExceptionEvent arg0) {

	}

	public void processRequest(RequestEvent arg0) {

	}

	public void processResponse(ResponseEvent arg0) {

	}

	public void processTimeout(TimeoutEvent arg0) {

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {

	}

	public SipUserAgentServer getUAS() {
		return UAS;
	}

	public SipUserAgentClient getUAC() {
		return UAC;
	}

	public Credential getCredential() {
		return credential;
	}

	public ID getTargetId() {
		return targetId;
	}
	
	public SipProvider getSipProvider(){
		return sipProvider;
	}

}
