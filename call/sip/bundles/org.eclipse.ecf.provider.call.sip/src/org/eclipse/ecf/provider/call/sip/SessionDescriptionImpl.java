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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;

import org.eclipse.ecf.provider.fmj.Transceiver;



public class SessionDescriptionImpl {

	private String publicIP;

	private String localIP;

	private TreeMap<String, String> localMediaMApAudio;

	private ArrayList<String> remoteMediaAudio;

	private int remoteAudioPort;

	private int remoteAudioPortBehindNAT;

	private String remoteIPAddress;

	private String remotePrivateIPAddress;

	private String remoteGlobalIPAddress;

	private String remoteNetworkType;

	private String remoteIPType;

	private ArrayList<String> commonMediaAudio;
	
	Transceiver transceiver;

	/**
	 * 
	 */
	public SessionDescriptionImpl() {
	}

	public static String getPublicIp() {

		try {
			java.net.URL URL = new java.net.URL("http://www.whatismyip.org/");

			java.net.HttpURLConnection Conn = (HttpURLConnection) URL
					.openConnection();

			java.io.InputStream InStream = Conn.getInputStream();

			java.io.InputStreamReader Isr = new java.io.InputStreamReader(
					InStream);

			java.io.BufferedReader Br = new java.io.BufferedReader(Isr);

			String publicIp = Br.readLine();

			return publicIp;
		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(-1);
			try {
				System.out.println("Public IP retrieving failed. Using localIP");
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public SessionDescription getInviteSDP() throws SdpParseException,
			IOException {

		publicIP = getPublicIp();

		String sdp = "v=0\r\n" + "o=- 2 5" + " IN IP4 " + publicIP + "\r\n"
				+ "s=Eclipse ECF\r\n" + "p=+94 71 6062650\r\n" + "c=IN IP4 "
				+ publicIP + "\r\n" + "t=0 0\r\n"
				+ "m=audio 6022 RTP/AVP 0 8 97 98\r\n"
				+ "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:8 PCMA/8000\r\n"
				+ "a=rtpmap:97 speex/16000\r\n" + "a=rtpmap:98 speex/8000\r\n"
				+ "a=sendrecv\r\n";

		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdpSession = sdpFactory
				.createSessionDescription(sdp);
		return sdpSession;
	}

	public SessionDescription getOkSDP(String inviteSDP) throws SdpException,
			IOException {

		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = sdpFactory.createSessionDescription(inviteSDP);

		Connection c = sdp.getConnection();

		remoteIPAddress = c.getAddress();
		remoteIPType = c.getAddressType();
		remoteNetworkType = c.getNetworkType();

		System.out.println("Remote IP= " + remoteIPAddress);

		Vector<MediaDescription> remoteCapabilities = sdp
				.getMediaDescriptions(true);

		System.out.println(remoteCapabilities);

		for (int i = 0; i < remoteCapabilities.size(); i++) {
			MediaDescription m = (MediaDescription) remoteCapabilities
					.elementAt(i);

			// //TODO TEST
			// Added last
			Vector attributeLst = m.getAttributes(true);
			System.out.println("Attributes a= " + attributeLst);
			for (int z = 0; z < attributeLst.size(); z++) {
				String attribute = attributeLst.get(z).toString();
				System.out.println("Attribute " + z + " = " + attribute);

				String[] resArr = attribute.split(":");
				if (resArr.length > 2) {
					String[] realArr = resArr[2].trim().split(" ");
					if (realArr.length > 3) {
						if (realArr[2].startsWith("192.168.")
								|| realArr[2].startsWith("10.")) {
							remotePrivateIPAddress = realArr[2];
							System.out.println("remotePrivateIPAddress ="
									+ remotePrivateIPAddress);
						} else {
							remoteGlobalIPAddress = realArr[2];
							System.out.println("remoteGlobalIPAddress= "
									+ remoteGlobalIPAddress);
						}

						remoteAudioPortBehindNAT = Integer.parseInt(realArr[3]
								.trim());
						System.out.println("remoteAudioPortBehindNAT= "
								+ remoteAudioPortBehindNAT);
					}

				}

			}

			System.out.println("m = " + m.toString());
			Media media = m.getMedia();

			if (media.getMediaType().equalsIgnoreCase("Audio")
					&& media.getProtocol().equalsIgnoreCase("RTP/AVP")) {
				if (remoteMediaAudio == null) {
					remoteMediaAudio = new ArrayList<String>(media
							.getMediaFormats(false));
				} else {
					Vector<String> mediaFormats = media.getMediaFormats(false);

					remoteMediaAudio.removeAll(mediaFormats);
					remoteMediaAudio.addAll(mediaFormats);
				}

				remoteAudioPort = media.getMediaPort();
			}

			System.out.println("remoteAudioPort= " + remoteAudioPort);
			System.out.println("remoteMediaAudio= " + remoteMediaAudio);
		}

		String commonAudioCodec = selectBestAudioCodec();

		publicIP = getPublicIp();
		localIP = Inet4Address.getLocalHost().getHostAddress();

		String sdpData = "v=0\r\n" + "o=- 2 5" + " IN IP4 " + publicIP + "\r\n"
				+ "s=Eclipse ECF\r\n" + "p=+94 71 6062650\r\n" + "c=IN IP4 "
				+ publicIP + "\r\n" + "t=0 0\r\n" + "m=audio 6022 RTP/AVP "
				+ commonAudioCodec + "\r\n" + "a=sendrecv\r\n";

		sdp = sdpFactory.createSessionDescription(sdpData);

		return sdp;

	}

	public void initFMJ() {

		 transceiver = new Transceiver("" + remoteIPAddress, ""
				+ remoteAudioPort);
		transceiver.initiateMediaSession();

	}
	
	public void disposeFMJ(){
		
		if(transceiver!=null)
			transceiver.closeMediaSession();
		
	}

	private String selectBestAudioCodec() {

		localMediaMApAudio = getLocalAudioCodec();
		commonMediaAudio = new ArrayList<String>();

		commonMediaAudio.addAll(localMediaMApAudio.keySet());

		commonMediaAudio.retainAll(remoteMediaAudio);

		String list = null;
		if (commonMediaAudio != null) {
			for (String value : commonMediaAudio) {
				if (list == null)
					list = "" + value;
				else
					list += " " + value;
			}
		}
		return list;

	}

	private TreeMap<String, String> getLocalAudioCodec() {

		TreeMap<String, String> local = new TreeMap<String, String>();

		local.put("0", "PCMU/8000");
		local.put("8", "PCMA/8000");
		local.put("97", "speex/16000");
		local.put("98", "speex/8000");

		return local;
	}

	public void resolveOkSDP(String incomingOKSDP) throws SdpException {
		System.out
				.println("This is the SDP data recived to resolve from Listener as a reply to invite="
						+ incomingOKSDP);
		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = sdpFactory
				.createSessionDescription(incomingOKSDP);

		Connection c = sdp.getConnection();

		remoteIPAddress = c.getAddress();
		remoteIPType = c.getAddressType();
		remoteNetworkType = c.getNetworkType();

		System.out.println("Remote IP= " + remoteIPAddress);

		Vector<MediaDescription> remoteCapabilities = sdp
				.getMediaDescriptions(true);

		System.out.println(remoteCapabilities);

		for (int i = 0; i < remoteCapabilities.size(); i++) {
			MediaDescription m = (MediaDescription) remoteCapabilities
					.elementAt(i);

			Vector attributeLst = m.getAttributes(true);
			System.out.println("Attributes a= " + attributeLst);
			for (int z = 0; z < attributeLst.size(); z++) {
				String attribute = attributeLst.get(z).toString();
				System.out.println("Attribute " + z + " = " + attribute);

				String[] resArr = attribute.split(":");
				if (resArr.length > 2) {
					String[] realArr = resArr[2].trim().split(" ");
					if (realArr.length > 3) {
						if (realArr[2].startsWith("192.168.")
								|| realArr[2].startsWith("10.")) {
							remotePrivateIPAddress = realArr[2];
							System.out.println("remotePrivateIPAddress ="
									+ remotePrivateIPAddress);
						} else {
							remoteGlobalIPAddress = realArr[2];
							System.out.println("remoteGlobalIPAddress= "
									+ remoteGlobalIPAddress);
						}

						remoteAudioPortBehindNAT = Integer.parseInt(realArr[3]
								.trim());
						System.out.println("remoteAudioPortBehindNAT= "
								+ remoteAudioPortBehindNAT);
					}

				}

			}

			System.out.println("m = " + m.toString());
			Media media = m.getMedia();

			if (media.getMediaType().equalsIgnoreCase("Audio")
					&& media.getProtocol().equalsIgnoreCase("RTP/AVP")) {
				if (remoteMediaAudio == null) {
					remoteMediaAudio = new ArrayList<String>(media
							.getMediaFormats(false));
				} else {
					Vector<String> mediaFormats = media.getMediaFormats(false);

					remoteMediaAudio.removeAll(mediaFormats);
					remoteMediaAudio.addAll(mediaFormats);
				}

				remoteAudioPort = media.getMediaPort();
			}

			System.out.println("remoteAudioPort= " + remoteAudioPort);
			System.out.println("remoteMediaAudio= " + remoteMediaAudio);
		}

		String commonAudioCodec = selectBestAudioCodec();

		// OK Received cz remote party agreed to answer the call. So initiate
		// media session to remote party
		initFMJ();
	}

}
