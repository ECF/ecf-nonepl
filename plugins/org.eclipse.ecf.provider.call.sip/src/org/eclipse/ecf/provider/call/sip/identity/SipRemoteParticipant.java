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


public class SipRemoteParticipant {
	private SipUriID receiverID;
	private String receiverName;
	
	/**
	 * 
	 */
	public SipRemoteParticipant(SipUriID receiverID, String receiverName) {
		this.receiverID = receiverID;
		this.receiverName = receiverName;
	}

	public SipUriID getReceiverID() {
		return receiverID;
	}

	public void setReceiverID(SipUriID receiverId) {
		receiverID = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	
}
