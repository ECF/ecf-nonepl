package org.eclipse.ecf.provider.call.sip_new;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;

public class SipUserAgentClient {

	private ClientTransaction registerCT;
	private Dialog responseDialog;
	
//	public boolean sendRegister()
	
	
	
	public ClientTransaction getRegisterCT(){
		return registerCT;
	}
	
	public Dialog getResponseDialog(){
		return responseDialog;
	}
}
