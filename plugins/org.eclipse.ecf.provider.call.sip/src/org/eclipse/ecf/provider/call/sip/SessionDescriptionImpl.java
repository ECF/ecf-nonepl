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
import java.net.*;
import java.util.*;
import javax.sdp.*;
import org.eclipse.ecf.provider.fmj.Transceiver;

public class SessionDescriptionImpl {

	private String publicIP;

	private String localIP;

	public String getRemoteNetworkType() {
		return remoteNetworkType;
	}

	public void setRemoteNetworkType(String remoteNetworkType) {
		this.remoteNetworkType = remoteNetworkType;
	}

	public String getRemoteIPType() {
		return remoteIPType;
	}

	public void setRemoteIPType(String remoteIpType) {
		remoteIPType = remoteIpType;
	}

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
			java.net.URL URL = new java.net.URL("http://www.whatismyip.org/"); //$NON-NLS-1$

			java.net.HttpURLConnection Conn = (HttpURLConnection) URL.openConnection();

			java.io.InputStream InStream = Conn.getInputStream();

			java.io.InputStreamReader Isr = new java.io.InputStreamReader(InStream);

			java.io.BufferedReader Br = new java.io.BufferedReader(Isr);

			String publicIp = Br.readLine();

			return publicIp;
		} catch (IOException e) {
			//			e.printStackTrace();
			//			System.exit(-1);
			try {
				System.out.println("Public IP retrieving failed. Using localIP"); //$NON-NLS-1$
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public SessionDescription getInviteSDP() throws SdpParseException {

		publicIP = getPublicIp();

		String sdp = "v=0\r\n" + "o=- 2 5" + " IN IP4 " + publicIP + "\r\n" + "s=Eclipse ECF\r\n" + "p=+94 71 6062650\r\n" + "c=IN IP4 " + publicIP + "\r\n" + "t=0 0\r\n" + "m=audio 6022 RTP/AVP 0 8 97 98\r\n" + "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:8 PCMA/8000\r\n" + "a=rtpmap:97 speex/16000\r\n" + "a=rtpmap:98 speex/8000\r\n" + "a=sendrecv\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$

		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdpSession = sdpFactory.createSessionDescription(sdp);
		return sdpSession;
	}

	public SessionDescription getOkSDP(String inviteSDP) throws SdpException, IOException {

		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = sdpFactory.createSessionDescription(inviteSDP);

		Connection c = sdp.getConnection();

		remoteIPAddress = c.getAddress();
		remoteIPType = c.getAddressType();
		remoteNetworkType = c.getNetworkType();

		System.out.println("Remote IP= " + remoteIPAddress); //$NON-NLS-1$

		Vector<MediaDescription> remoteCapabilities = sdp.getMediaDescriptions(true);

		System.out.println(remoteCapabilities);

		for (int i = 0; i < remoteCapabilities.size(); i++) {
			MediaDescription m = (MediaDescription) remoteCapabilities.elementAt(i);

			// //TODO TEST
			// Added last
			Vector attributeLst = m.getAttributes(true);
			System.out.println("Attributes a= " + attributeLst); //$NON-NLS-1$
			for (int z = 0; z < attributeLst.size(); z++) {
				String attribute = attributeLst.get(z).toString();
				System.out.println("Attribute " + z + " = " + attribute); //$NON-NLS-1$ //$NON-NLS-2$

				String[] resArr = attribute.split(":"); //$NON-NLS-1$
				if (resArr.length > 2) {
					String[] realArr = resArr[2].trim().split(" "); //$NON-NLS-1$
					if (realArr.length > 3) {
						if (realArr[2].startsWith("192.168.") || realArr[2].startsWith("10.")) { //$NON-NLS-1$ //$NON-NLS-2$
							remotePrivateIPAddress = realArr[2];
							System.out.println("remotePrivateIPAddress =" + remotePrivateIPAddress); //$NON-NLS-1$
						} else {
							remoteGlobalIPAddress = realArr[2];
							System.out.println("remoteGlobalIPAddress= " + remoteGlobalIPAddress); //$NON-NLS-1$
						}

						remoteAudioPortBehindNAT = Integer.parseInt(realArr[3].trim());
						System.out.println("remoteAudioPortBehindNAT= " + remoteAudioPortBehindNAT); //$NON-NLS-1$
					}

				}

			}

			System.out.println("m = " + m.toString()); //$NON-NLS-1$
			Media media = m.getMedia();

			if (media.getMediaType().equalsIgnoreCase("Audio") && media.getProtocol().equalsIgnoreCase("RTP/AVP")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (remoteMediaAudio == null) {
					remoteMediaAudio = new ArrayList<String>(media.getMediaFormats(false));
				} else {
					Vector<String> mediaFormats = media.getMediaFormats(false);

					remoteMediaAudio.removeAll(mediaFormats);
					remoteMediaAudio.addAll(mediaFormats);
				}

				remoteAudioPort = media.getMediaPort();
			}

			System.out.println("remoteAudioPort= " + remoteAudioPort); //$NON-NLS-1$
			System.out.println("remoteMediaAudio= " + remoteMediaAudio); //$NON-NLS-1$
		}

		String commonAudioCodec = selectBestAudioCodec();

		publicIP = getPublicIp();
		localIP = Inet4Address.getLocalHost().getHostAddress();

		String sdpData = "v=0\r\n" + "o=- 2 5" + " IN IP4 " + publicIP + "\r\n" + "s=Eclipse ECF\r\n" + "p=+94 71 6062650\r\n" + "c=IN IP4 " + publicIP + "\r\n" + "t=0 0\r\n" + "m=audio 6022 RTP/AVP " + commonAudioCodec + "\r\n" + "a=sendrecv\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

		sdp = sdpFactory.createSessionDescription(sdpData);

		return sdp;

	}

	public void initFMJ() {

		transceiver = new Transceiver("" + remoteIPAddress, "" + remoteAudioPort); //$NON-NLS-1$ //$NON-NLS-2$
		transceiver.initiateMediaSession();

	}

	public void disposeFMJ() {

		if (transceiver != null)
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
					list = "" + value; //$NON-NLS-1$
				else
					list += " " + value; //$NON-NLS-1$
			}
		}
		return list;

	}

	private TreeMap<String, String> getLocalAudioCodec() {

		TreeMap<String, String> local = new TreeMap<String, String>();

		local.put("0", "PCMU/8000"); //$NON-NLS-1$ //$NON-NLS-2$
		local.put("8", "PCMA/8000"); //$NON-NLS-1$ //$NON-NLS-2$
		local.put("97", "speex/16000"); //$NON-NLS-1$ //$NON-NLS-2$
		local.put("98", "speex/8000"); //$NON-NLS-1$ //$NON-NLS-2$

		return local;
	}

	public void resolveOkSDP(String incomingOKSDP) throws SdpException {
		System.out.println("This is the SDP data recived to resolve from Listener as a reply to invite=" + incomingOKSDP); //$NON-NLS-1$
		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = sdpFactory.createSessionDescription(incomingOKSDP);

		Connection c = sdp.getConnection();

		remoteIPAddress = c.getAddress();
		remoteIPType = c.getAddressType();
		remoteNetworkType = c.getNetworkType();

		System.out.println("Remote IP= " + remoteIPAddress); //$NON-NLS-1$

		Vector<MediaDescription> remoteCapabilities = sdp.getMediaDescriptions(true);

		System.out.println(remoteCapabilities);

		for (int i = 0; i < remoteCapabilities.size(); i++) {
			MediaDescription m = (MediaDescription) remoteCapabilities.elementAt(i);

			Vector attributeLst = m.getAttributes(true);
			System.out.println("Attributes a= " + attributeLst); //$NON-NLS-1$
			for (int z = 0; z < attributeLst.size(); z++) {
				String attribute = attributeLst.get(z).toString();
				System.out.println("Attribute " + z + " = " + attribute); //$NON-NLS-1$ //$NON-NLS-2$

				String[] resArr = attribute.split(":"); //$NON-NLS-1$
				if (resArr.length > 2) {
					String[] realArr = resArr[2].trim().split(" "); //$NON-NLS-1$
					if (realArr.length > 3) {
						if (realArr[2].startsWith("192.168.") || realArr[2].startsWith("10.")) { //$NON-NLS-1$ //$NON-NLS-2$
							remotePrivateIPAddress = realArr[2];
							System.out.println("remotePrivateIPAddress =" + remotePrivateIPAddress); //$NON-NLS-1$
						} else {
							remoteGlobalIPAddress = realArr[2];
							System.out.println("remoteGlobalIPAddress= " + remoteGlobalIPAddress); //$NON-NLS-1$
						}

						remoteAudioPortBehindNAT = Integer.parseInt(realArr[3].trim());
						System.out.println("remoteAudioPortBehindNAT= " + remoteAudioPortBehindNAT); //$NON-NLS-1$
					}

				}

			}

			System.out.println("m = " + m.toString()); //$NON-NLS-1$
			Media media = m.getMedia();

			if (media.getMediaType().equalsIgnoreCase("Audio") && media.getProtocol().equalsIgnoreCase("RTP/AVP")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (remoteMediaAudio == null) {
					remoteMediaAudio = new ArrayList<String>(media.getMediaFormats(false));
				} else {
					Vector<String> mediaFormats = media.getMediaFormats(false);

					remoteMediaAudio.removeAll(mediaFormats);
					remoteMediaAudio.addAll(mediaFormats);
				}

				remoteAudioPort = media.getMediaPort();
			}

			System.out.println("remoteAudioPort= " + remoteAudioPort); //$NON-NLS-1$
			System.out.println("remoteMediaAudio= " + remoteMediaAudio); //$NON-NLS-1$
		}

		String commonAudioCodec = selectBestAudioCodec();

		// OK Received cz remote party agreed to answer the call. So initiate
		// media session to remote party
		initFMJ();
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIp) {
		localIP = localIp;
	}

}
