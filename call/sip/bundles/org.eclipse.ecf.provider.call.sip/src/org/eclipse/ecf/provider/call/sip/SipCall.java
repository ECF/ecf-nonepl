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

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.ProxyAuthenticate;
import gov.nist.javax.sip.header.SIPHeaderNames;
import gov.nist.javax.sip.header.WWWAuthenticate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import javax.sdp.SdpException;
import javax.sdp.SdpParseException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransactionState;
import javax.sip.TransactionUnavailableException;
import javax.sip.TransportAlreadySupportedException;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.provider.call.sip.identity.SipLocalParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipRemoteParticipant;
import org.eclipse.ecf.provider.call.sip.identity.SipUriID;
import org.eclipse.ecf.provider.call.sip.identity.SipUriNamespace;

/**
 * This class represent a sip call. It is consists of all the methods to handle
 * user registration, incoming calls and outgoing calls.
 */
public class SipCall {

	private SipFactory sipFactory;

	private static SipProvider sipProvider;

	private static String sipProxyServer;

	private static AddressFactory addressFactory;

	private static MessageFactory messageFactory;

	private static HeaderFactory headerFactory;

	private static SipStack sipStack;

	private ContactHeader contactHeader;

	private ListeningPoint udpListeningPoint;

	private ListeningPoint tcpListeningPoint;

	private ClientTransaction inviteCTid;

	private ClientTransaction registerCTid;

	private ClientTransaction cancelCTid;

	private Dialog responseDialog;

	private Dialog requestDialog;

	private boolean byeTaskRunning;

	private SipUriID initiatorID;

	private String initiatorName;

	private SipUriID receiverID;

	private String receiverName;

	private SipClient sipClient;

	private String inviteSDP;

	private static final String transportTCP = "tcp";
	private static final String transportUDP = "udp";
	private static final String peerHostPortUDP = "192.168.1.5:5070";
	private static final String peerHostPortTCP = "230.0.0.1:5070";

	private String publicIpAddress;

	private String authUserName;

	private String authPassword;

	private String realm;

	private ServerTransaction inviteSTid;

	private Request inviteRequest;

	private boolean ringingReceived = false;

	public static final boolean callerSendsBye = true;// Decide which party ends

	public static SessionDescriptionImpl sdpImpl;

	private static Logger logger = Logger.getLogger(SipCall.class);

	private SipCallSessionContainerAdapter callAdapter;

	private boolean isActiveCall = false;

	private static SipCall self;

	// the call

	/*************************
	 * Finalized Constructor
	 *************************/
	public SipCall(SipLocalParticipant localUser) {

		connect(localUser);

	}

	public SipCall() {

	}

	public boolean connect(SipLocalParticipant localUser) {
		try {

			initiateLocalAuth(localUser); // Initiates local user's auth data

			initStack(); // Initialize the system

			// for registration
			createRegisterRequest(); // Register the local participant

			return true;

		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
			return false;
		} catch (TransportNotSupportedException e) {
			e.printStackTrace();
			return false;
		} catch (ObjectInUseException e) {
			e.printStackTrace();
			return false;
		} catch (TransportAlreadySupportedException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		} catch (SipException e) {
			e.printStackTrace();
			return false;
		}

	}

	private SipCall(SipCallSessionContainerAdapter callAdapter) {
		this.callAdapter = callAdapter;
	}

	public static SipCall getDefault(SipCallSessionContainerAdapter callAdapter) {
		if (self == null) {
			self = new SipCall(callAdapter);
		}
		return self;
	}

	/**********************************************************************************************************
	 * Start of SIP Call Method Section
	 **********************************************************************************************************/
	public void initiateLocalAuth(SipLocalParticipant localParty) {
		initiatorID = localParty.getInitiatorID();
		initiatorName = localParty.getInitiatorName();
		authPassword = localParty.getInitiatorPassword();
		authUserName = initiatorID.getUser().substring(
				initiatorID.getUser().indexOf(":") + 1,
				initiatorID.getUser().indexOf("@"));
		sipProxyServer = localParty.getSipProxyServer();
	}

	public void initiateRemoteUser(SipRemoteParticipant remoteParty) {
		receiverID = remoteParty.getReceiverID();
		receiverName = remoteParty.getReceiverName();
	}

	public boolean initiateCall(SipRemoteParticipant remoteUser) {

		initiateRemoteUser(remoteUser);

		try {
			createCallRequest();
			System.out.println("Call request suceesfully created and sent");
			return true;
		} catch (SdpParseException e) {
			e.printStackTrace();
			return false;
		} catch (SipException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		// while (!ringingReceived) {
		//					
		// }
		// createCallCancel();
		// terminateOutGoingCall(8000);

	}

	public void initiateCall(SipRemoteParticipant remoteUser,
			SipCallSession callSession) {
		initiateCall(remoteUser);

	}

	/**
	 * Create SIP stack to send and receive sip messages
	 * 
	 * @throws UnknownHostException
	 * @throws PeerUnavailableException
	 * @throws InvalidArgumentException
	 * @throws TransportNotSupportedException
	 * @throws ObjectInUseException
	 * @throws TransportAlreadySupportedException
	 * @throws TooManyListenersException
	 */
	public void initStack() throws UnknownHostException,
			PeerUnavailableException, TransportNotSupportedException,
			InvalidArgumentException, ObjectInUseException,
			TransportAlreadySupportedException, TooManyListenersException {

		sipFactory = null;
		sipStack = null;

		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");

		Properties properties = new Properties();

		properties.setProperty("javax.sip.OUTBOUND_PROXY", sipProxyServer + "/"
				+ transportUDP);

		properties.setProperty("javax.sip.STACK_NAME", "Eclipse Sip Stack");

		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"Sip Client Debug.txt");
		properties.setProperty("javax.sip.IP_ADDRESS", InetAddress
				.getLocalHost().getHostAddress());

		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"Sip Client Server.txt");

		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
				"false");
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "ERROR");

		// Create SipStack object
		sipStack = sipFactory.createSipStack(properties);
		// sipStack=new SipStackImpl(properties);
		System.out.println("createSipStack " + sipStack);
		logger.debug("Sip Stack created");

		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();

		udpListeningPoint = sipStack.createListeningPoint(InetAddress
				.getLocalHost().getHostAddress(), 5060, "udp");
		tcpListeningPoint = sipStack.createListeningPoint(InetAddress
				.getLocalHost().getHostAddress(), 5060, "tcp");
		sipProvider = sipStack.createSipProvider(udpListeningPoint);
		sipProvider.addListeningPoint(tcpListeningPoint);

		sipClient = new SipClient(this);
		sipProvider.addSipListener(sipClient);

	}

	/**
	 * Create New Invite request for a out going call
	 * 
	 * @throws SipException
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws IOException
	 * @throws SdpParseException
	 */
	public void createCallRequest() throws SipException, ParseException,
			InvalidArgumentException, SdpParseException, IOException {

		// create >From Header

		Address fromNameAddress = addressFactory.createAddress(initiatorID
				.getUser());
		fromNameAddress.setDisplayName(initiatorName);
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
				"12345");

		// create To Header
		Address toNameAddress = addressFactory.createAddress(receiverID
				.getUser());
		toNameAddress.setDisplayName(receiverName);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		// create Request URI
		SipURI requestURI = addressFactory.createSipURI(receiverID.getUser()
				.substring(receiverID.getUser().indexOf(":") + 1,
						receiverID.getUser().indexOf("@")), receiverID
				.getUser().substring(receiverID.getUser().indexOf("@") + 1));

		// Create ViaHeaders

		ArrayList viaHeaders = new ArrayList();
		String ipAddress = udpListeningPoint.getIPAddress();
		ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
				sipProvider.getListeningPoint(transportUDP).getPort(),
				transportUDP, null);

		// add via headers
		viaHeaders.add(viaHeader);

		// Create ContentTypeHeader
		ContentTypeHeader contentTypeHeader = headerFactory
				.createContentTypeHeader("application", "sdp");

		// Create a new CallId header
		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		// Create a new Cseq header
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
				Request.INVITE);

		// Create a new MaxForwardsHeader
		MaxForwardsHeader maxForwards = headerFactory
				.createMaxForwardsHeader(70);

		// Create the request.
		Request request = messageFactory.createRequest(requestURI,
				Request.INVITE, callIdHeader, cSeqHeader, fromHeader, toHeader,
				viaHeaders, maxForwards);

		// Create contact headers
		@SuppressWarnings("unused")
		String host = InetAddress.getLocalHost().getHostAddress();

		// SipURI contactUrl =

		SipURI contactUrl = addressFactory.createSipURI(initiatorID.getUser()
				.substring(initiatorID.getUser().indexOf(":") + 1,
						initiatorID.getUser().indexOf("@")), initiatorID
				.getUser().substring(initiatorID.getUser().indexOf("@") + 1));

		contactUrl.setPort(udpListeningPoint.getPort());
		contactUrl.setLrParam();

		// Create the contact name address.
		SipURI contactURI = null;

		contactURI = addressFactory.createSipURI(initiatorID.getUser()
				.substring(initiatorID.getUser().indexOf(":") + 1,
						initiatorID.getUser().indexOf("@")), initiatorID
				.getUser().substring(initiatorID.getUser().indexOf("@") + 1));

		contactURI.setPort(sipProvider.getListeningPoint(transportUDP)
				.getPort());

		Address contactAddress = addressFactory.createAddress(contactURI);

		// Add the contact address.
		contactAddress.setDisplayName(initiatorID.getUser().substring(
				initiatorID.getUser().indexOf(":") + 1,
				initiatorID.getUser().indexOf("@")));

		contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		// Add the extension header.
		Header extensionHeader = headerFactory.createHeader("My-Header",
				"my header value");
		request.addHeader(extensionHeader);

		String sdpData = new SessionDescriptionImpl().getInviteSDP().toString();

		byte[] contents = sdpData.getBytes();

		request.setContent(contents, contentTypeHeader);
		// You can add as many extension headers as you
		// want.

		extensionHeader = headerFactory.createHeader("My-Other-Header",
				"my new header value ");
		request.addHeader(extensionHeader);

		Header callInfoHeader = headerFactory.createHeader("Call-Info",
				"<http://www.antd.nist.gov>");
		request.addHeader(callInfoHeader);

		// Create the client transaction.
		inviteCTid = sipProvider.getNewClientTransaction(request);

		// send the request out.
		inviteCTid.sendRequest();

		responseDialog = inviteCTid.getDialog();

	}

	/**
	 * Create a Invite request with a given call Id Used when proxy
	 * authorization requested for an initial invite
	 * 
	 * @param callId
	 * @return
	 */
	public Request createCallRequest(String callId) {
		Request request = null;
		try {

			Address fromNameAddress = addressFactory.createAddress(initiatorID
					.getUser());
			fromNameAddress.setDisplayName(initiatorName);
			FromHeader fromHeader = headerFactory.createFromHeader(
					fromNameAddress, "12345");

			// create To Header
			Address toNameAddress = addressFactory.createAddress(receiverID
					.getUser());
			toNameAddress.setDisplayName(receiverName);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress,
					null);

			// create Request URI
			SipURI requestURI = addressFactory
					.createSipURI(receiverID.getUser().substring(
							receiverID.getUser().indexOf(":") + 1,
							receiverID.getUser().indexOf("@")), receiverID
							.getUser().substring(
									receiverID.getUser().indexOf("@") + 1));

			// Create ViaHeaders

			ArrayList viaHeaders = new ArrayList();
			String ipAddress = udpListeningPoint.getIPAddress();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
					sipProvider.getListeningPoint(transportUDP).getPort(),
					transportUDP, null);

			// add via headers
			viaHeaders.add(viaHeader);

			// Create ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory
					.createContentTypeHeader("application", "sdp");

			// Create a new CallId header
			CallIdHeader callIdHeader = headerFactory
					.createCallIdHeader(callId);

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(2L,
					Request.INVITE);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);

			// Create the request.
			request = messageFactory.createRequest(requestURI, Request.INVITE,
					callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders,
					maxForwards);

			// Create contact headers
			String host = InetAddress.getLocalHost().getHostAddress();

			SipURI contactUrl = addressFactory.createSipURI(initiatorID
					.getUser().substring(
							initiatorID.getUser().indexOf(":") + 1,
							initiatorID.getUser().indexOf("@")), initiatorID
					.getUser()
					.substring(initiatorID.getUser().indexOf("@") + 1));

			contactUrl.setPort(udpListeningPoint.getPort());
			contactUrl.setLrParam();

			// Create the contact name address.
			SipURI contactURI = null;
			try {
				contactURI = addressFactory.createSipURI(initiatorID.getUser()
						.substring(initiatorID.getUser().indexOf(":") + 1,
								initiatorID.getUser().indexOf("@")), host);
			} catch (Exception e) {
				contactURI = addressFactory.createSipURI(initiatorID.getUser()
						.substring(initiatorID.getUser().indexOf(":") + 1,
								initiatorID.getUser().indexOf("@")), "sip."
						+ initiatorID.getUser().substring(
								initiatorID.getUser().indexOf("@") + 1));

			}
			contactURI.setPort(sipProvider.getListeningPoint(transportUDP)
					.getPort());

			Address contactAddress = addressFactory.createAddress(contactURI);

			// Add the contact address.
			contactAddress.setDisplayName(initiatorID.getUser().substring(
					initiatorID.getUser().indexOf(":") + 1,
					initiatorID.getUser().indexOf("@")));

			contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			// Add the extension header.
			Header extensionHeader = headerFactory.createHeader("My-Header",
					"my header value");
			request.addHeader(extensionHeader);

			String sdpData = new SessionDescriptionImpl().getInviteSDP()
					.toString();

			byte[] contents = sdpData.getBytes();

			request.setContent(contents, contentTypeHeader);
			// You can add as many extension headers as you
			// want.

			extensionHeader = headerFactory.createHeader("My-Other-Header",
					"my new header value ");
			request.addHeader(extensionHeader);

			Header callInfoHeader = headerFactory.createHeader("Call-Info",
					"<http://www.antd.nist.gov>");
			request.addHeader(callInfoHeader);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SdpParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * Create Cancel Request for a pending Out Going Call
	 */
	public void createCallCancel() {

		try {
			Request cancelRequest = inviteCTid.createCancel();
			cancelCTid = sipProvider.getNewClientTransaction(cancelRequest);

			if (ringingReceived) {
				cancelCTid.sendRequest();

			}

			ringingReceived = false;// Now next time caller will be able to
			// cancel it

			System.out.println("On going Call request cancelled");

		} catch (SipException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Processes Bye Request received to the SIP stack
	 * 
	 * @param request
	 * @param serverTransactionId
	 */
	public void processBye(Request request,
			ServerTransaction serverTransactionId) {

		if (sdpImpl != null) {
			System.out.println("Media Session Stopped");
			sdpImpl.disposeFMJ();
			// Now the Call is not active anymore
			setActiveCall(false);
		}

		try {
			System.out
					.println("Sip Client:  Recived a bye for Out going call, from "
							+ request.getRequestURI());
			logger.info("Sip Client:  Recived a bye for Out going call, from "
					+ request.getRequestURI());
			if (serverTransactionId == null) {
				System.out.println("Sip Client:  null TID.");
				logger.debug("Sip Client:  null TID.");
				return;
			}
			Dialog dialog = serverTransactionId.getDialog();
			System.out.println("Dialog State = " + dialog.getState());
			Response response = messageFactory.createResponse(200, request);
			serverTransactionId.sendResponse(response);
			System.out.println("Sip Client:  Sending OK.");
			System.out.println("Dialog State = " + dialog.getState());

		} catch (Exception ex) {
			ex.printStackTrace();
			// System.exit(0);

		}
	}

	/**
	 * Use by terminate call method to terminate an out going call after a given
	 * time
	 * 
	 * @author Administrator
	 * 
	 */
	class ByeTask extends TimerTask {
		Dialog dialog;

		public ByeTask(Dialog dialog) {
			this.dialog = dialog;
		}

		public void run() {
			try {
				Request byeRequest = this.dialog.createRequest(Request.BYE);
				ClientTransaction ct = sipProvider
						.getNewClientTransaction(byeRequest);
				dialog.sendRequest(ct);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(0);
			}

		}

	}

	/**
	 * Create a Bye request to terminate an out going cal after a given time
	 * period
	 * 
	 * @param timeMills
	 */
	public void terminateOutGoingCall(long timeMills) {
		if (!isByeTaskRunning()) {
			setByeTaskRunning(true);
			new Timer().schedule(new ByeTask(getResponseDialog()), timeMills);
		}

		// Now the call is not active
		setActiveCall(false);
	}

	public void terminateIncomingCall(long timeMills) {
		if (!isByeTaskRunning()) {
			setByeTaskRunning(true);
			new Timer()
					.schedule(new ByeTask(inviteSTid.getDialog()), timeMills);
		}
	}

	public void hangupActiveCall(long timeMills) {
		// terminateIncomingCall(timeMills);
		terminateIncomingCall();
		terminateOutGoingCall(timeMills);
	}

	/**
	 * Create a Bye request to end the commencing incoming call
	 */
	public void terminateIncomingCall() {

		if (sdpImpl != null) {
			System.out.println("Media Session stoppped");
			sdpImpl.disposeFMJ();

			// Now the call is not active anymore
			setActiveCall(false);
		}

		try {
			System.out
					.println("Before sending Bye to incoming call Dialog Status: "
							+ requestDialog.getState());
			Request byeRequest = requestDialog.createRequest(Request.BYE);

			cancelCTid = sipProvider.getNewClientTransaction(byeRequest);

			requestDialog.sendRequest(cancelCTid);

			System.out
					.println("After sending Bye to incoming call Dialog Status: "
							+ requestDialog.getState());

		} catch (TransactionUnavailableException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Use to reject an incoming call without answering
	 */
	public void rejectIncomingCall() {
		// Busy scenario
		// Instead of 200 OK, send 480 response

		try {
			if (inviteSTid.getState() != TransactionState.COMPLETED) {
				System.out.println("Sip Call: Dialog state before 480: "
						+ inviteSTid.getDialog().getState());

				Response tempararilyUnavailableResponse = messageFactory
						.createResponse(Response.TEMPORARILY_UNAVAILABLE,
								inviteRequest);

				inviteSTid.sendResponse(tempararilyUnavailableResponse);
				System.out.println("Sip Call: Dialog state after 480: "
						+ inviteSTid.getDialog().getState());
			}
		} catch (SipException ex) {
			ex.printStackTrace();
		} catch (InvalidArgumentException ex) {
			ex.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new Register request for user registration
	 * 
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws UnknownHostException
	 * @throws SipException
	 */
	// Registering for incoming sip call sessions
	public void createRegisterRequest() throws ParseException,
			InvalidArgumentException, UnknownHostException, SipException {

		// create >From Header
		Address fromNameAddress = addressFactory.createAddress(initiatorID
				.getUser());
		fromNameAddress.setDisplayName(initiatorName);
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
				"98765");

		// create To Header
		Address toNameAddress = addressFactory.createAddress(initiatorID
				.getUser());
		toNameAddress.setDisplayName(initiatorName);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		// create Request URI
		SipURI requestURI = new SipUri();
		requestURI.setHost(initiatorID.getUser().substring(
				initiatorID.getUser().indexOf("@") + 1));

		// Create ViaHeaders

		ArrayList viaHeaders = new ArrayList();
		String ipAddress = udpListeningPoint.getIPAddress();
		ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
				sipProvider.getListeningPoint(transportUDP).getPort(),
				transportUDP, null);

		// add via headers
		viaHeaders.add(viaHeader);

		// Create Content Length Header
		ContentLengthHeader contentLegthHeader = headerFactory
				.createContentLengthHeader(0);

		// Create a new CallId header
		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		// Create a new Cseq header
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
				Request.REGISTER);

		// Create a new MaxForwardsHeader
		MaxForwardsHeader maxForwards = headerFactory
				.createMaxForwardsHeader(70);

		// Create the request.
		Request request = messageFactory.createRequest(requestURI,
				Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);

		// Create contact headers
		@SuppressWarnings("unused")
		String host = InetAddress.getLocalHost().getHostAddress();

		SipURI contactUrl = addressFactory.createSipURI(initiatorID.getUser()
				.substring(initiatorID.getUser().indexOf(":") + 1,
						initiatorID.getUser().indexOf("@")), initiatorID
				.getUser().substring(initiatorID.getUser().indexOf("@") + 1));

		contactUrl.setPort(udpListeningPoint.getPort());
		contactUrl.setLrParam();

		// Create the contact name address.
		SipURI contactURI = null;

		contactURI = addressFactory.createSipURI(initiatorID.getUser()
				.substring(initiatorID.getUser().indexOf(":") + 1,
						initiatorID.getUser().indexOf("@")), initiatorID
				.getUser().substring(initiatorID.getUser().indexOf("@") + 1));

		contactURI.setPort(sipProvider.getListeningPoint(transportUDP)
				.getPort());

		Address contactAddress = addressFactory.createAddress(contactURI);

		// Add the contact address.
		contactAddress.setDisplayName(initiatorName);

		contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		// create Call Info Header
		Header callInfoHeader = headerFactory.createHeader("Call-Info",
				"<http://www.antd.nist.gov>");
		request.addHeader(callInfoHeader);

		// Insert content Length
		request.addHeader(contentLegthHeader);

		// Create the client transaction.
		registerCTid = sipProvider.getNewClientTransaction(request);

		// send the request out.
		registerCTid.sendRequest();

		requestDialog = registerCTid.getDialog();

	}

	/**
	 * Create a Register request with a given call Id Use when authorization
	 * requested
	 * 
	 * @param callId
	 * @return
	 */
	public Request createRegisterRequest(String callId) {
		Request request = null;
		try {
			// create >From Header
			Address fromNameAddress = addressFactory.createAddress(initiatorID
					.getUser());
			fromNameAddress.setDisplayName(initiatorName);
			FromHeader fromHeader = headerFactory.createFromHeader(
					fromNameAddress, "56789");

			// create To Header
			Address toNameAddress = addressFactory.createAddress(initiatorID
					.getUser());
			toNameAddress.setDisplayName(initiatorName);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress,
					null);

			// create Request URI
			SipURI requestURI = new SipUri();
			requestURI.setHost(initiatorID.getUser().substring(
					initiatorID.getUser().indexOf("@") + 1));

			// Create ViaHeaders

			ArrayList viaHeaders = new ArrayList();
			String ipAddress = udpListeningPoint.getIPAddress();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
					sipProvider.getListeningPoint(transportUDP).getPort(),
					transportUDP, null);

			// add via headers
			viaHeaders.add(viaHeader);

			// Create Content Length Header
			ContentLengthHeader contentLegthHeader = headerFactory
					.createContentLengthHeader(0);

			// Create a new CallId header
			CallIdHeader callIdHeader = headerFactory
					.createCallIdHeader(callId);

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(2L,
					Request.REGISTER);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);

			// Create the request.
			request = messageFactory.createRequest(requestURI,
					Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);

			// Create contact headers
			String host = InetAddress.getLocalHost().getHostAddress();

			SipURI contactUrl = addressFactory.createSipURI(initiatorID
					.getUser().substring(
							initiatorID.getUser().indexOf(":") + 1,
							initiatorID.getUser().indexOf("@")), initiatorID
					.getUser()
					.substring(initiatorID.getUser().indexOf("@") + 1));

			contactUrl.setPort(udpListeningPoint.getPort());
			contactUrl.setLrParam();

			// Create the contact name address.
			SipURI contactURI = null;
			try {
				contactURI = addressFactory.createSipURI(initiatorID.getUser()
						.substring(initiatorID.getUser().indexOf(":") + 1,
								initiatorID.getUser().indexOf("@")), host);

			} catch (Exception e) {
				// contactURI =
				// addressFactory.createSipURI(initiatorID.getUser()
				// .substring(initiatorID.getUser().indexOf("<") + 5,
				// initiatorID.getUser().indexOf("@")), "sip."
				// + initiatorID.getUser().substring(
				// initiatorID.getUser().indexOf("@") + 1,
				// initiatorID.getUser().indexOf(">")));

			}
			contactURI.setPort(sipProvider.getListeningPoint(transportUDP)
					.getPort());

			Address contactAddress = addressFactory.createAddress(contactURI);

			// Add the contact address.
			contactAddress.setDisplayName(initiatorName);

			contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			Header callInfoHeader = headerFactory.createHeader("Call-Info",
					"<http://www.antd.nist.gov>");
			request.addHeader(callInfoHeader);

			// Insert content Length
			request.addHeader(contentLegthHeader);

			// Create the Expires
			ExpiresHeader expiresHeader = headerFactory
					.createExpiresHeader(3600);
			request.addHeader(expiresHeader);

			// Create User-Agent
			List userAgents = new ArrayList();
			userAgents.add("Eclipse ECF 3.0");
			UserAgentHeader userAgentHeader = headerFactory
					.createUserAgentHeader(userAgents);
			request.addHeader(userAgentHeader);

			// Create Allow header
			String methods = "INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
			AllowHeader allowHeader = headerFactory.createAllowHeader(methods);
			request.addHeader(allowHeader);

			return request;

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * Create a Authorization included response for Authorization request
	 * recieved for Register
	 * 
	 * @param response
	 * @param uriReq
	 */
	public void processResponseAuthorization(Response response, URI uriReq) {
		Request requestauth = null;
		try {
			System.out.println("Processing and Preparing Authorization");

			String callId = ((CallIdHeader) response
					.getHeader(CallIdHeader.NAME)).getCallId();
			requestauth = createRegisterRequest(callId);

			String schema = ((WWWAuthenticate) (response
					.getHeader(SIPHeaderNames.WWW_AUTHENTICATE))).getScheme();
			String nonce = ((WWWAuthenticate) (response
					.getHeader(SIPHeaderNames.WWW_AUTHENTICATE))).getNonce();
			realm = ((WWWAuthenticate) (response
					.getHeader(SIPHeaderNames.WWW_AUTHENTICATE))).getRealm();
			AuthorizationHeader wwwAuthheader = headerFactory
					.createAuthorizationHeader(schema);
			wwwAuthheader.setUsername(authUserName);
			wwwAuthheader.setRealm(realm);
			wwwAuthheader.setNonce(nonce);
			wwwAuthheader.setURI(uriReq);

			DigestClientAuthenticationMethod digest = new DigestClientAuthenticationMethod();

			digest.initialize(realm, authUserName, uriReq.toString(), nonce,
					authPassword, ((CSeqHeader) response
							.getHeader(CSeqHeader.NAME)).getMethod(), null,
					"MD5");

			String respuestaM = digest.generateResponse();
			wwwAuthheader.setResponse(respuestaM);

			System.out.println("Proxy Response modified : "
					+ wwwAuthheader.getResponse());

			wwwAuthheader.setAlgorithm("MD5");

			requestauth.addHeader(wwwAuthheader);

			// Create the client transaction.
			registerCTid = sipProvider.getNewClientTransaction(requestauth);

			// send the request out.
			registerCTid.sendRequest();

			requestDialog = registerCTid.getDialog();

			System.out.println("REGISTER AUTHORIZATION sent:\n" + requestauth);
		} catch (ParseException pa) {
			System.out
					.println("processResponseAuthorization() ParseException:");
			System.out.println(pa.getMessage());
			pa.printStackTrace();
		} catch (Exception ex) {
			System.out.println("processResponseAuthorization() Exception:");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

	/**
	 * Create a proxy authorization included for Proxy auth required request for
	 * Invite
	 * 
	 * @param response
	 * @param uriReq
	 */
	public void processResponseProxyAuthorization(Response response, URI uriReq) {
		Request requestauth = null;
		try {
			System.out.println("Processing and Preparing Authorization");

			String callId = ((CallIdHeader) response
					.getHeader(CallIdHeader.NAME)).getCallId();

			requestauth = createCallRequest(callId);

			String schema = ((ProxyAuthenticate) (response
					.getHeader(SIPHeaderNames.PROXY_AUTHENTICATE))).getScheme();
			String nonce = ((ProxyAuthenticate) (response
					.getHeader(SIPHeaderNames.PROXY_AUTHENTICATE))).getNonce();
			realm = ((ProxyAuthenticate) (response
					.getHeader(SIPHeaderNames.PROXY_AUTHENTICATE))).getRealm();
			ProxyAuthorizationHeader proxyAuth = headerFactory
					.createProxyAuthorizationHeader(schema);
			proxyAuth.setUsername(authUserName);
			proxyAuth.setRealm(realm);
			proxyAuth.setNonce(nonce);
			proxyAuth.setURI(uriReq);

			DigestClientAuthenticationMethod digest = new DigestClientAuthenticationMethod();

			digest.initialize(realm, authUserName, uriReq.toString(), nonce,
					authPassword, ((CSeqHeader) response
							.getHeader(CSeqHeader.NAME)).getMethod(), null,
					"MD5");

			String respuestaM = digest.generateResponse();
			proxyAuth.setResponse(respuestaM);

			System.out.println("Proxy Auth Response modified : "
					+ proxyAuth.getResponse());

			proxyAuth.setAlgorithm("MD5");

			requestauth.addHeader(proxyAuth);

			// Create the client transaction.
			inviteCTid = sipProvider.getNewClientTransaction(requestauth);

			// send the request out.
			inviteCTid.sendRequest();

			responseDialog = inviteCTid.getDialog();

			System.out.println("INVITE PROXY AUTHORIZATION sent:\n"
					+ requestauth);
		} catch (ParseException pa) {
			System.out
					.println("processResponseAuthorization() ParseException:");
			System.out.println(pa.getMessage());
			pa.printStackTrace();
		} catch (Exception ex) {
			System.out.println("processResponseAuthorization() Exception:");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

	/**
	 * Process the invite request.
	 */
	public void processInvite(RequestEvent requestEvent,
			ServerTransaction serverTransaction) {
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		Request request = requestEvent.getRequest();
		try {
			System.out.println("Sip Client: got an Invite from "
					+ request.getRequestURI() + " sending Trying to INVITER.");

			Response response = messageFactory.createResponse(Response.RINGING,
					request);
			ServerTransaction st = requestEvent.getServerTransaction();

			if (st == null) {
				st = sipProvider.getNewServerTransaction(request);
			}
			requestDialog = st.getDialog();

			st.sendResponse(response);

			inviteSTid = st;

			inviteRequest = request;

			// //Newly added to store the SDP data for processing in the
			// inviteOK()
			inviteSDP = new String((byte[]) request.getContent());
			System.out.println(inviteSDP);
			// / we can use SDPFactory to decode this message

			Thread.currentThread().sleep(2000);

			// TODO Only for Junit Test for evaluation
			// TODO Remove and decide using UI
			System.out.println("Please enter 1 to answer the Call");
			System.out.println("Please enter 2 to reject the call");
			System.out
					.println("Make sure to enter correct option for correct test case");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String userResponse = reader.readLine();
			int usersDecision = Integer.parseInt(userResponse);

			switch (usersDecision) {
			case 1:
				acceptIncomingCall();
				break;
			case 2:
				rejectIncomingCall();
				break;

			default:
				acceptIncomingCall();
				break;
			}

			// TODO Decide upon the user response from Console
			// To Accept the Call
			// acceptIncomingCall();

			// To Reject the Call
			// rejectIncomingCall();

			// To terminate incoming answered ongoing call
			// Thread.currentThread().sleep(10000);

			// terminateIncomingCall();

		} catch (Exception ex) {
			ex.printStackTrace();
			// System.exit(0);
		}
	}

	public void acceptIncomingCall(SipUriID remoteUser) {
		try {
			acceptIncomingCall();
		} catch (SdpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use to accept incoming calls
	 * 
	 * @throws IOException
	 * @throws SdpException
	 */
	public void acceptIncomingCall() throws SdpException, IOException {
		try {
			if (inviteSTid.getState() != TransactionState.COMPLETED) {
				System.out.println("Sip Call: Dialog state before 200: "
						+ inviteSTid.getDialog().getState());

				Response okResponse = messageFactory.createResponse(
						Response.OK, inviteRequest);

				SipURI contactURI = null;
				try {
					contactURI = addressFactory.createSipURI(initiatorID
							.getUser().substring(
									initiatorID.getUser().indexOf(":") + 1,
									initiatorID.getUser().indexOf("@")),
							initiatorID.getUser().substring(
									initiatorID.getUser().indexOf("@") + 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
				contactURI.setPort(sipProvider.getListeningPoint(transportUDP)
						.getPort());

				Address contactAddress = addressFactory
						.createAddress(contactURI);

				// Add the contact address.
				contactAddress.setDisplayName(initiatorID.getUser().substring(
						initiatorID.getUser().indexOf(":") + 1,
						initiatorID.getUser().indexOf("@")));

				contactHeader = headerFactory
						.createContactHeader(contactAddress);
				okResponse.addHeader(contactHeader);

				ToHeader toHeader = (ToHeader) okResponse
						.getHeader(ToHeader.NAME);
				toHeader.setTag("98765");

				// Create Allow header
				String methods = "INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
				AllowHeader allowHeader = headerFactory
						.createAllowHeader(methods);
				okResponse.addHeader(allowHeader);

				// Create ContentTypeHeader
				ContentTypeHeader contentTypeHeader = headerFactory
						.createContentTypeHeader("application", "sdp");

				// ///Have to send this after considering the sdp data in the
				// INVITE REQUEST
				// SessionDescriptionImpl sdpImpl = new
				// SessionDescriptionImpl();
				// String sdpData = sdpImpl.getOkSDP(inviteSDP).toString();
				sdpImpl = new SessionDescriptionImpl();
				String sdpData = sdpImpl.getOkSDP(inviteSDP).toString();

				byte[] contents = sdpData.getBytes();

				okResponse.setContent(contents, contentTypeHeader);

				inviteSTid.sendResponse(okResponse);

				System.out.println("Sip Call: Dialog state after 200: "
						+ inviteSTid.getDialog().getState());

				// TODO TEST
				sdpImpl.initFMJ();

				// TODO Test JUnit
				// Since we have initiated , now it's an active call
				setActiveCall(true);

			}
		} catch (SipException ex) {
			ex.printStackTrace();
		} catch (InvalidArgumentException ex) {
			ex.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process the Cancel request
	 * 
	 * @return
	 */
	public void processCancel(RequestEvent requestEvent,
			ServerTransaction serverTransactionId) {

		Request request = requestEvent.getRequest();

		try {
			System.out
					.println("Sip Client :  Received a cancel. Sending OK to Cancel.");
			if (serverTransactionId == null) {
				System.out.println("Sip Client:  null tid.");
				return;
			}
			Response response = messageFactory.createResponse(200, request);
			serverTransactionId.sendResponse(response);

			if (requestDialog.getState() != DialogState.CONFIRMED) {
				response = messageFactory.createResponse(
						Response.REQUEST_TERMINATED, inviteRequest);
				inviteSTid.sendResponse(response);

				System.out
						.println("Request Terminated Response sent to Invite Request.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// System.exit(0);

		}
	}

	/**
	 * Process ACK request
	 * 
	 * @return
	 */
	public void processAck(RequestEvent requestEvent,
			ServerTransaction serverTransaction) {
		try {
			System.out.println("Sip  client: Received an ACK! ");
			System.out.println("Dialog State = " + requestDialog.getState());
			SipProvider provider = (SipProvider) requestEvent.getSource();
			if (!callerSendsBye) {// / If we terminate call set to true or false
				Request byeRequest = requestDialog.createRequest(Request.BYE);

				ClientTransaction ct = provider
						.getNewClientTransaction(byeRequest);
				requestDialog.sendRequest(ct);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static String getPublicIp() throws IOException {

		java.net.URL URL = new java.net.URL("http://www.whatismyip.org/");

		java.net.HttpURLConnection Conn = (HttpURLConnection) URL
				.openConnection();

		java.io.InputStream InStream = Conn.getInputStream();

		java.io.InputStreamReader Isr = new java.io.InputStreamReader(InStream);

		java.io.BufferedReader Br = new java.io.BufferedReader(Isr);

		String publicIp = Br.readLine();

		logger.debug("Your IP address is " + publicIp);

		return publicIp;

	}

	/*****************************************************************************************************
	 * End Of SIP Call Methods Section
	 ******************************************************************************************************/

	// Call variable getter and Setter section
	public SipFactory getSipFactory() {
		return sipFactory;
	}

	public void setSipFactory(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	public static SipProvider getSipProvider() {
		return sipProvider;
	}

	public static void setSipProvider(SipProvider sipProvider) {
		SipCall.sipProvider = sipProvider;
	}

	public static AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public static void setAddressFactory(AddressFactory addressFactory) {
		SipCall.addressFactory = addressFactory;
	}

	public static MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public static void setMessageFactory(MessageFactory messageFactory) {
		SipCall.messageFactory = messageFactory;
	}

	public static HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public static void setHeaderFactory(HeaderFactory headerFactory) {
		SipCall.headerFactory = headerFactory;
	}

	public static SipStack getSipStack() {
		return sipStack;
	}

	public static void setSipStack(SipStack sipStack) {
		SipCall.sipStack = sipStack;
	}

	public ContactHeader getContactHeader() {
		return contactHeader;
	}

	public void setContactHeader(ContactHeader contactHeader) {
		this.contactHeader = contactHeader;
	}

	public ListeningPoint getUdpListeningPoint() {
		return udpListeningPoint;
	}

	public void setUdpListeningPoint(ListeningPoint udpListeningPoint) {
		this.udpListeningPoint = udpListeningPoint;
	}

	public ListeningPoint getTcpListeningPoint() {
		return tcpListeningPoint;
	}

	public void setTcpListeningPoint(ListeningPoint tcpListeningPoint) {
		this.tcpListeningPoint = tcpListeningPoint;
	}

	public ClientTransaction getInviteCTid() {
		return inviteCTid;
	}

	public void setInviteCTid(ClientTransaction inviteTid) {
		this.inviteCTid = inviteTid;
	}

	public Dialog getResponseDialog() {
		return responseDialog;
	}

	public void setResponseDialog(Dialog dialog) {
		this.responseDialog = dialog;
	}

	public boolean isByeTaskRunning() {
		return byeTaskRunning;
	}

	public void setByeTaskRunning(boolean byeTaskRunning) {
		this.byeTaskRunning = byeTaskRunning;
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

	public SipClient getSipClient() {
		return sipClient;
	}

	public void setSipClient(SipClient sipClient) {
		this.sipClient = sipClient;
	}

	public void setRegisterCTid(ClientTransaction registerCtid) {
		this.registerCTid = registerCtid;
	}

	public ClientTransaction getRegisterCTid() {
		return registerCTid;
	}

	public void setRequestDialog(Dialog requestDialog) {
		this.requestDialog = requestDialog;
	}

	public Dialog getRequestDialog() {
		return requestDialog;
	}

	public void setRingingReceived(boolean ringingReceived) {
		this.ringingReceived = ringingReceived;
	}

	public boolean isRingingReceived() {
		return ringingReceived;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public boolean isActiveCall() {
		return isActiveCall;
	}

	public void setActiveCall(boolean isActiveCall) {
		this.isActiveCall = isActiveCall;
	}

	public static void main(String args[]) throws ParseException,
			PeerUnavailableException, IOException {

		try {
			SipLocalParticipant localParty = new SipLocalParticipant(
					(SipUriID) new SipUriNamespace()
							.createInstance(new Object[] { "sip:2233371083@sip2sip.info" }),
					"Harshana Eranga", "4j5yx83hs5", "proxy.sipthor.net");

			System.out.println(localParty.getInitiatorID());
			SipCall ecfCall = new SipCall(localParty);

			SipRemoteParticipant remoteParty = new SipRemoteParticipant(
					(SipUriID) new SipUriNamespace()
							.createInstance(new Object[] { "sip:3333@sip2sip.info" }),
					"Harshana Eranga Martin");

			System.out
					.println(((SipUriID) new SipUriNamespace()
							.createInstance(new Object[] { "sip:2233371083@sip2sip.info" }))
							.getUser());
			System.out.println(localParty.getInitiatorID());

			// System.out.println(new SipUriID("sip:2233371083@sip2sip.info"));
			AddressFactory add = SipFactory.getInstance()
					.createAddressFactory();
			System.out.println(add.createSipURI("Harshana", "Martin")
					.toString());
			// System.out.println(new
			// SipUriID(add.createSipURI("Harshana","Martin")));

			ecfCall.initiateCall(remoteParty);

		} catch (IDCreateException e) {
			e.printStackTrace();
		}

	}

}
