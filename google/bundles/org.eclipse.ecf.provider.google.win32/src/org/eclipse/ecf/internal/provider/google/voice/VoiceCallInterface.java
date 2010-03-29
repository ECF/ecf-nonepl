/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google.voice;

import java.util.HashSet;

public class VoiceCallInterface {

	public HashSet<String> voiceEnabledSet;
	public String activeCallerID = "";
	public String incomingCallerID;

	private static int OTHER_SIDE_HANGUP_CALL = 1;
	private static int OTHER_SIDE_REJECTED_CALL = 2;
	private static int OTHER_SIDE_ANSWERED_CALL = 3;
	private static int RECEIVE_CALL = 4;
	private static int ROSTER_ENTRY_VOICE_ADD = 5;
	private static int ROSTER_ENTRY_VOICE_REMOVED = 6;

	private static VoiceCallInterface self = null;

	private native void nativeInitXMPPSession(String username, String password);

	private native void nativeDisconnect();

	private native void nativeAnswerReceivingCall(String jid);

	private native void nativeRejectReceivingCall();

	private native void nativeMuteActiveCall();

	private native void nativeUnmuteActiveCall();

	private native void nativeHangupActiveCall();

	private native void nativeInitVoiceCall(String jid);

	// private native boolean nativeIsCallActive();

	public static String getJid(String jid) {
		return jid.substring(0, jid.indexOf("/") < 0 ? jid.length() : jid
				.indexOf("/"));
	}

	public void changeActiveCallerID(String jid) {
		jid = getJid(jid);
		activeCallerID = jid;
		// System.out.println("ACTIVE CALL: " + activeCallerID);
	}

	private GoogleVoiceThread voiceThread;

	private GoogleCallSessionContainerAdapter callAdapter;

	private void callback(String string) {
		System.out.println(string);
	}

	private void callbackReceivingCall(String jid) {
		jid = getJid(jid);
		/*
		 * incomingCallerID = jid; Hashtable<String, Object> properties = new
		 * Hashtable<String, Object>(); properties.put(NotificationEvent.JID,
		 * jid); properties.put(NotificationEvent.NOTIFICATION_STRING,
		 * "Incoming Call from " + jid); NotificationEvent event = new
		 * NotificationEvent( NotificationEvent.TYPE_INCOMING_CALL, properties);
		 * container.getNotificationManager().notifyListeners(event);
		 */
		callAdapter.incomingCallRequest(jid);
	}

	private void callbackAddRosterVoiceCapabilities(String jid) {
		jid = getJid(jid);
		voiceEnabledSet.add(jid);
	}

	private void callbackRemoveRosterVoiceCapabilities(String jid) {
		jid = getJid(jid);
		voiceEnabledSet.remove(jid);
	}

	static {
		System.loadLibrary("gipsvoiceenginelite");
		System.loadLibrary("call");
	}

	private VoiceCallInterface(GoogleCallSessionContainerAdapter callAdapter) {
		try {
			voiceThread = new GoogleVoiceThread();
			voiceEnabledSet = new HashSet<String>();
			this.callAdapter = callAdapter;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static VoiceCallInterface getDefault(
			GoogleCallSessionContainerAdapter adapter) {
		if (self == null) {
			self = new VoiceCallInterface(adapter);
		}
		return self;
	}

	class GoogleVoiceThread extends Thread {
		private String username;
		private String password;

		public GoogleVoiceThread(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public GoogleVoiceThread() {
			username = null;
			password = null;
		}

		public void setLoginDetails(String username, String password) {
			this.username = username;
			this.password = password;

		}

		public void initVoiceCall(String jid) {
			nativeInitVoiceCall(jid);
		}

		public void run() {
			nativeInitXMPPSession(username, password);
		}

		public void hangupActiveCall() {
			nativeHangupActiveCall();
		}

		public void muteActiveCall() {
			nativeMuteActiveCall();
		}

		public void unmuteActiveCall() {
			nativeUnmuteActiveCall();
		}

		public void rejectReceivingCall() {
			nativeRejectReceivingCall();
		}

		public void answerReceivingCall(String jid) {
			nativeAnswerReceivingCall(jid);

		}

		public void disconnect() {
			// nativeDisconnect();
		}

	}

	public void initXMPPSession(final String username, final String password) {
		voiceThread.setLoginDetails(username, password);
		voiceThread.start();

	}

	public void hangupActiveCall() {
		voiceThread.hangupActiveCall();
	}

	public String getActiveCallerID() {
		if (activeCallerID == null)
			return "";
		return activeCallerID;
	}

	public void muteActiveCall() {
		voiceThread.muteActiveCall();
	}

	public void unmuteActiveCall() {
		voiceThread.unmuteActiveCall();
	}

	public void rejectReceivingCall() {
		voiceThread.rejectReceivingCall();
	}

	public void AnswerReceivingCall(String jid) {
		voiceThread.answerReceivingCall(jid);
	}

	public void initVoiceCall(final String jid, GoogleCallSession session) {
		voiceThread.initVoiceCall(jid);
	}

	public boolean isVoiceEnabled(String jid) {
		return voiceEnabledSet.contains(jid);
	}

	public boolean isCallActive() {
		// return voiceThread.isCallActive();
		return !(activeCallerID.trim().length() == 0 || activeCallerID.trim()
				.equals(""));
	}

	public String getIncomingCallerID() {
		return incomingCallerID;
	}

	public void initVoiceCall(String jid) {
		voiceThread.initVoiceCall(jid);
	}

	public void disconnect() {
		voiceThread.disconnect();
	}
}
