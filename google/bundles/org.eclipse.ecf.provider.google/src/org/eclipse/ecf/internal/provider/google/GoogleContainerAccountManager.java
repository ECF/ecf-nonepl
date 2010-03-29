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
package org.eclipse.ecf.internal.provider.google;

import java.util.HashSet;
import org.eclipse.ecf.internal.provider.xmpp.XMPPContainerAccountManager;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

public class GoogleContainerAccountManager extends XMPPContainerAccountManager {

	private GoogleContainer container;
	private HashSet<String> featureTable = null;

	// msgs

	public GoogleContainerAccountManager(GoogleContainer googleContainer) {
		this.container = googleContainer;
		featureTable = new HashSet<String>();
	}

	public void setUserSettingsEnabled(boolean isEnabled) {
	}

	public void retrieveUserSettings() {
		IQ iq = new IQ() {

			public String getChildElementXML() {
				return "<query xmlns='http://jabber.org/protocol/disco#info'/>";
			}

		};

		iq.setType(IQ.Type.GET);
		iq.setTo("gmail.com");

		container.getXMPPConnection().sendPacket(iq);

	}

	public void addFeature(String nodeValue) {
		featureTable.add(nodeValue);
	}

	public HashSet<String> getFeatures() {
		return featureTable;
	}

	public void initialize() {
		registerIQProviders();
		retrieveUserSettings();
		retrieveNoSaveStates();
		retrieveSharedStatusMsgs();
		retrieveEmailNotifications();
	}

	private void retrieveEmailNotifications() {
		IQ iq = new IQ() {

			@Override
			public String getChildElementXML() {
				return "<query xmlns='google:mail:notify'/>";
			}

		};
		iq.setType(IQ.Type.GET);
		String user = container.getXMPPConnection().getUser();
		iq.setPacketID("notify-1");
		iq.setFrom(user);
		iq.setTo(user.substring(0, user.lastIndexOf("/")));

		container.getXMPPConnection().sendPacket(iq);

	}

	private void registerIQProviders() {
		ProviderManager.addIQProvider("mailbox", GoogleIQ.XMLNS_GMAIL_NOTIFICATIONS, new GenericIQProvider());
		ProviderManager.addIQProvider("new-mail", GoogleIQ.XMLNS_GMAIL_NOTIFICATIONS, new GenericIQProvider());
		ProviderManager.addIQProvider("query", GoogleIQ.XMLNS_OFF_THE_RECORD, new GenericIQProvider());
		ProviderManager.addIQProvider("query", GoogleIQ.XMLNS_SHARED_STATUS, new GenericIQProvider());
	}

	private void retrieveSharedStatusMsgs() {
		IQ iq = new IQ() {

			public String getChildElementXML() {
				return "<query xmlns=\'google:shared-status' version='" + GoogleStatusMessageManager.PRESENCE_VERSION + "'/>";
			}

		};

		iq.setType(IQ.Type.GET);
		iq.setPacketID("ss-1");
		String user = container.getXMPPConnection().getUser();
		iq.setTo(user.substring(0, user.lastIndexOf("/")));

		container.getXMPPConnection().sendPacket(iq);

	}

	private void retrieveNoSaveStates() {

		IQ iq = new IQ() {

			public String getChildElementXML() {
				return " <query xmlns='google:nosave' />";
			}

		};

		iq.setType(IQ.Type.GET);
		String user = container.getXMPPConnection().getUser();
		iq.setPacketID("otr-1");
		iq.setFrom(user);
		iq.setTo(user.substring(0, user.lastIndexOf("/")));

		container.getXMPPConnection().sendPacket(iq);

	}

}
