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

import javax.management.modelmbean.XMLParseException;
import org.eclipse.ecf.internal.provider.xmpp.events.IQEvent;
import org.eclipse.ecf.provider.google.events.NotificationEvent;
import org.jivesoftware.smack.packet.IQ;
import org.w3c.dom.*;

public class GoogleIQProcessor {

	private GoogleContainerPresenceHelper helper;

	public GoogleIQProcessor(
			GoogleContainerPresenceHelper googleContainerPresenceHelper) {
		this.helper = googleContainerPresenceHelper;
	}

	public boolean handleEvent(IQEvent event) {

		IQ temp = event.getIQ();
		GoogleIQ iq;
		try {
			iq = new GoogleIQ(temp);
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			return false;
		}

		if (checkforFeatureDiscoveryIQ(iq))
			return true;
		if (checkForNoSaveRequestResponse(iq))
			return true;
		if (checkForNoSaveNotification(iq))
			return true;
		if (checkForSharedStatusListRequestResponse(iq))
			return true;
		if (checkForSharedStatusListChangeNotification(iq))
			return true;
		if (checkForEmailNotificationRequestResponse(iq))
			return true;
		if (checkForEmailNotification(iq))
			return true;
		return false;
	}

	// public for testing
	public boolean checkForEmailNotification(GoogleIQ gIQ) {

		if (gIQ.getType().equals(IQ.Type.SET)) {
			if (gIQ.getChildXMLNamespace().equals(
					GoogleIQ.XMLNS_GMAIL_NOTIFICATIONS)
					&& gIQ.getChildDocument().getFirstChild().getNodeName()
							.equals("new-mail")) {

				helper.container.getMailManager()
						.respondForNewMailNotification(gIQ);
				return true;

			}
		}

		return false;
	}

	// public for testing
	public boolean checkForSharedStatusListChangeNotification(GoogleIQ gIQ) {

		if (gIQ.getType().equals(IQ.Type.SET)
				&& gIQ.getChildXMLNamespace().equals(
						GoogleIQ.XMLNS_SHARED_STATUS)) {
			IQ iq = new IQ() {

				@Override
				public String getChildElementXML() {
					return "<query xmlns=\"google:shared-status\"/>";
				}

			};
			iq.setType(IQ.Type.RESULT);
			iq.setPacketID(gIQ.getPacketID());
			helper.container.getXMPPConnection().sendPacket(iq); // send ack

			helper.getStatusMessageManager().processSharedStatusMessageIQ(gIQ,
					false);

			return true;
		}

		return false;
	}

	// public for testing
	public boolean checkForEmailNotificationRequestResponse(GoogleIQ gIQ) {
		if (gIQ.getType().equals(IQ.Type.RESULT)) {
			if (gIQ.getChildXMLNamespace().equals(
					GoogleIQ.XMLNS_GMAIL_NOTIFICATIONS)) {
				helper.container.getMailManager().processMailNotification(gIQ);
				return true;

			}
		}
		return false;

	}

	// public for testing
	public boolean checkForSharedStatusListRequestResponse(GoogleIQ gIQ) {
		if (gIQ.getType().equals(IQ.Type.RESULT)
				&& gIQ.getChildXMLNamespace().equals(
						GoogleIQ.XMLNS_SHARED_STATUS)) {
			helper.getStatusMessageManager().processSharedStatusMessageIQ(gIQ,
					true);
			return true;
		}
		return false;
	}

	// public for testing
	public boolean checkForNoSaveNotification(GoogleIQ gIQ) {
		if (gIQ.getType().equals(IQ.Type.SET)) {

			Document doc = gIQ.getChildDocument();

			String namespace = gIQ.getChildXMLNamespace();
			if (!namespace.equals(GoogleIQ.XMLNS_OFF_THE_RECORD))
				return false;

			NodeList childNodes = doc.getChildNodes().item(0).getChildNodes();
			Node item;
			for (int i = 0; i < childNodes.getLength(); i++) {
				item = childNodes.item(i);
				if (item.getNodeName().equals("item")) {
					String jid = item.getAttributes().getNamedItem("jid")
							.getNodeValue();
					boolean isEnabled = item.getAttributes().getNamedItem(
							"value").getNodeValue().equals("enabled") ? true
							: false;
					String source = item.getAttributes().getNamedItem("source")
							.getNodeValue();

					helper.nosaveHashMap.put(jid, isEnabled);
					helper.nosaveHashMap.put(source, isEnabled);

					helper.container.getNotificationManager().notifyListeners(
							new NotificationEvent(isEnabled ? "Chat with "
									+ jid + " is now off the record"
									: "Chat with " + jid
											+ " is no longer off the record"));

				}
			}
			return true;
		}
		return false;

	}

	// public for testing
	public boolean checkForNoSaveRequestResponse(GoogleIQ gIQ) {

		if (gIQ.getPacketID().equals("otr-1")
				&& gIQ.getType().equals(IQ.Type.RESULT)) {
			Document doc = gIQ.getChildDocument();
			NodeList childNodes = doc.getChildNodes().item(0).getChildNodes();
			Node item;
			for (int i = 0; i < childNodes.getLength(); i++) {
				item = childNodes.item(i);
				if (item.getNodeName().equals("nos:item")) {
					helper.nosaveHashMap.put(item.getAttributes().getNamedItem(
							"jid").getNodeValue(), item.getAttributes()
							.getNamedItem("value").getNodeValue().equals(
									"enabled") ? true : false);

				}
			}
			return true;
		}
		return false;
	}

	// public for testing
	public boolean checkforFeatureDiscoveryIQ(GoogleIQ gIQ) {
		// returns true if the iq stanza is related to user settings
		if (gIQ.getFrom() == null)
			return false;

		if (gIQ.getType().equals(IQ.Type.RESULT)
				&& gIQ.getFrom().equals("gmail.com")) {
			Document doc = gIQ.getChildDocument();
			NodeList childNodes = doc.getChildNodes().item(0).getChildNodes();
			Node item;
			for (int i = 0; i < childNodes.getLength(); i++) {
				item = childNodes.item(i);
				if (item.getNodeName().equals("feature")) {
					((GoogleContainerAccountManager) helper.container
							.getAccountManager())
							.addFeature(item.getAttributes()
									.getNamedItem("var").getNodeValue());
				}

			}

			return true;
		}
		return false;
	}

}
