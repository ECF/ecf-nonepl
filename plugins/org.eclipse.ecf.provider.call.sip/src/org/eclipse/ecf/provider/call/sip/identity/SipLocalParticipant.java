/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.call.sip.identity;


public class SipLocalParticipant {
	private SipUriID initiatorID;
	private String initiatorName;
	private String initiatorPassword;
	private String sipProxyServer;
	
	/**
	 * 
	 */
	public SipLocalParticipant(SipUriID initiatorID, String initiatorName,String initiatorPassword, String sipProxyServer) {
		this.initiatorID = initiatorID;
		this.initiatorName = initiatorName;
		this.initiatorPassword=initiatorPassword;
		this.sipProxyServer=sipProxyServer;
	}

	public SipUriID getInitiatorID() {
		return initiatorID;
	}

	public void setInitiatorID(SipUriID initiatorId) {
		initiatorID = initiatorId;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public String getInitiatorPassword() {
		return initiatorPassword;
	}

	public void setInitiatorPassword(String initiatorPassword) {
		this.initiatorPassword = initiatorPassword;
	}

	public String getSipProxyServer() {
		return sipProxyServer;
	}

	public void setSipProxyServer(String sipProxyServer) {
		this.sipProxyServer = sipProxyServer;
	}

	
	
}
