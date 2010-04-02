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
package org.eclipse.ecf.provider.call.sip;

import java.text.ParseException;
import javax.sip.*;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class SipClient implements SipListener {

	private SipCall call;

	// Save the created ACK request, to respond to retransmitted 2xx
	private Request ackRequest;

	/**
	 * 
	 */
	public SipClient(SipCall call) {
		this.call = call;
	}

	public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
		System.out.println("Dialog Terminated Event Received");

	}

	public void processIOException(IOExceptionEvent exceptionEvent) {
		System.out.println("IOException happened for " + exceptionEvent.getHost() + " port = " + exceptionEvent.getPort());
	}

	public void processRequest(RequestEvent requestReceivedEvent) {
		Request request = requestReceivedEvent.getRequest();
		ServerTransaction serverTransactionId = requestReceivedEvent.getServerTransaction();

		System.out.println("\n\nRequest " + request.getMethod() + " received at " + call.getSipStack().getStackName() + " with server transaction id " + serverTransactionId);

		// We are the UAC so the only request we get is the BYE.
		if (request.getMethod().equals(Request.BYE)) {
			// For any BYE just send OK cz it asks to terminate the call
			call.processBye(request, serverTransactionId);
		}// / But this is a common class hence all the other requests will
		// receive as well=> has to integrate them
		else if (request.getMethod().equals(Request.INVITE)) {
			call.processInvite(requestReceivedEvent, serverTransactionId);
		} else if (request.getMethod().equals(Request.CANCEL)) {
			call.processCancel(requestReceivedEvent, serverTransactionId);
		} else if (request.getMethod().equals(Request.ACK)) {
			call.processAck(requestReceivedEvent, serverTransactionId);
		} else {
			try {
				serverTransactionId.sendResponse(SipCall.getMessageFactory().createResponse(202, request));
			} catch (SipException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}

	public void processResponse(ResponseEvent responseReceivedEvent) {
		System.out.println("Got a response");
		Response response = (Response) responseReceivedEvent.getResponse();
		ClientTransaction tid = responseReceivedEvent.getClientTransaction();
		CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);

		System.out.println("Response received : Status Code = " + response.getStatusCode() + " " + cseq);

		if (tid == null) {

			// RFC3261: MUST respond to every 2xx
			if (ackRequest != null && call.getResponseDialog() != null) {
				System.out.println("re-sending ACK");
				try {
					call.getResponseDialog().sendAck(ackRequest);
				} catch (SipException se) {
					se.printStackTrace();
				}
			}
			return;
		}

		System.out.println("transaction state is " + tid.getState());
		System.out.println("Dialog = " + tid.getDialog());

		try {
			if (response.getStatusCode() == Response.OK) {
				if (cseq.getMethod().equals(Request.INVITE)) {
					System.out.println("Dialog after 200 OK  " + call.getResponseDialog());
					System.out.println("Dialog State after 200 OK  " + call.getResponseDialog().getState());
					ackRequest = call.getResponseDialog().createAck(((CSeqHeader) response.getHeader(CSeqHeader.NAME)).getSeqNumber());
					System.out.println("Sending ACK");
					call.getResponseDialog().sendAck(ackRequest);

					String okSdpData = new String((byte[]) response.getContent());
					System.out.println("OK RECIVED with SDP " + okSdpData);
					SipCall.sdpImpl = new SessionDescriptionImpl();
					SipCall.sdpImpl.resolveOkSDP(okSdpData);

					//TODO JUnit Test
					//Now the Call is an active call since we have initiated
					call.setActiveCall(true);

					//					new SessionDescriptionImpl().resolveOkSDP(okSdpData);

					// JvB: test REFER, reported bug in tag handling
					call.getResponseDialog().sendRequest(call.getSipProvider().getNewClientTransaction(call.getResponseDialog().createRequest("REFER")));

				} else if (cseq.getMethod().equals(Request.CANCEL)) {
					if (call.getResponseDialog().getState() == DialogState.CONFIRMED) {

						System.out.println("Sending BYE -- cancel went in too late !!"); //$NON-NLS-1$
						Request byeRequest = call.getResponseDialog().createRequest(Request.BYE);
						ClientTransaction ct = call.getSipProvider().getNewClientTransaction(byeRequest);
						call.getResponseDialog().sendRequest(ct);

					}

				} else if (cseq.getMethod().equals(Request.REGISTER)) {
					// Do nothing

					System.out.println("User Registration Successful. OK received for REGISTER."); //$NON-NLS-1$
				} else if (cseq.getMethod().equals(Request.BYE)) {
					// Do nothing=> incoming call terminated from this side.

					System.out.println("Ongoing call for Incoming invite request, terminated by INVITE receiver."); //$NON-NLS-1$
				}
			} else if (response.getStatusCode() == Response.UNAUTHORIZED) {
				// Used by REGISTER
				System.out.println("Unauthorized recived " + response.getStatusCode());
				URI uriReq = tid.getRequest().getRequestURI();
				call.processResponseAuthorization(response, uriReq);

			} else if (response.getStatusCode() == Response.PROXY_AUTHENTICATION_REQUIRED) {
				// Used by INVITE
				System.out.println("Proxy Authentication Required recived " + response.getStatusCode());
				URI uriReq = tid.getRequest().getRequestURI();
				call.processResponseProxyAuthorization(response, uriReq);
			} else if (response.getStatusCode() == Response.RINGING) {
				call.setRingingReceived(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public void processTimeout(TimeoutEvent timeoutEvent) {
		System.out.println("Transaction Time out event recieved");
	}

	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
		System.out.println("Transaction terminated event recieved");
	}

}
