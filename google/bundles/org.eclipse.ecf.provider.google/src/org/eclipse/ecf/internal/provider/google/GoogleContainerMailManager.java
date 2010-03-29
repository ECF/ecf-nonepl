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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.provider.google.events.MailNotificationEvent;
import org.jivesoftware.smack.packet.IQ;
import org.w3c.dom.*;

public class GoogleContainerMailManager {

	private static final String GMAIL_LABEL_DELIMITER = "|";

	String timeStamp;
	String tidStamp;
	private GoogleContainer container;

	public GoogleContainerMailManager(GoogleContainer container) {
		this.container = container;
		tidStamp = "0";
	}

	public void respondForNewMailNotification(GoogleIQ gIQ) {

		// respond with with an <iq/> stanza of type 'result' as defined in
		// section 9.2.3 of the XMPP-core specification
		IQ respondIq = new IQ() {

			@Override
			public String getChildElementXML() {
				return null;
			}

		};
		respondIq.setFrom(gIQ.getFrom());
		respondIq.setTo(gIQ.getTo());
		respondIq.setPacketID(gIQ.getPacketID());
		respondIq.setType(IQ.Type.RESULT);

		container.getXMPPConnection().sendPacket(respondIq);

		// after responding, query for new mails

		IQ iq = new IQ() {

			@Override
			public String getChildElementXML() {
				return "<query xmlns='google:mail:notify' newer-than-time='"
						+ timeStamp + "' newer-than-tid='" + tidStamp + "'/>";
			}

		};
		iq.setType(IQ.Type.GET);
		String user = container.getXMPPConnection().getUser();
		iq.setPacketID("notify-1");
		iq.setFrom(user);
		iq.setTo(user.substring(0, user.lastIndexOf("/")));

		container.getXMPPConnection().sendPacket(iq);
	}

	// this function processes the 'senders' node of the mail query response.
	// This was isolated only to improve the readability of the code.
	private ArrayList processSenderList(Node attribItem,
			MailNotificationEvent mailNotificationEvent) {
		NodeList senderNodeList = attribItem.getChildNodes();
		ArrayList<MailNotificationEvent.SenderElement> senderList = new ArrayList<MailNotificationEvent.SenderElement>();
		Node senderItem;
		MailNotificationEvent.SenderElement senderElement = null;
		for (int k = 0; k < senderNodeList.getLength(); k++) {

			senderItem = senderNodeList.item(k);
			senderElement = mailNotificationEvent.new SenderElement();

			senderElement.setAddress(senderItem.getAttributes().getNamedItem(
					"address").getNodeValue());

			// name, isOriginator and isUnread are optional fields.
			senderElement.setName(senderItem.getAttributes().getNamedItem(
					"name") == null ? "" : senderItem.getAttributes()
					.getNamedItem("name").getNodeValue());
			senderElement.setIsOriginator(senderItem.getAttributes()
					.getNamedItem("originator") == null ? "0" : senderItem
					.getAttributes().getNamedItem("originator").getNodeValue());
			senderElement.setUnread(senderItem.getAttributes().getNamedItem(
					"unread") == null ? "0" : senderItem.getAttributes()
					.getNamedItem("unread").getNodeValue());
			senderList.add(senderElement);
		}
		return senderList;
	}

	public void processMailNotification(GoogleIQ gIQ) {

		Document doc = gIQ.getChildDocument();
		Node node = doc.getFirstChild();

		timeStamp = node.getAttributes().getNamedItem("result-time")
				.getNodeValue();
		NodeList threadList = node.getChildNodes();
		Node item;
		Hashtable attribHash;
		for (int i = 0; i < threadList.getLength(); i++) {
			// process each thread
			MailNotificationEvent mailNotificationEvent = new MailNotificationEvent(
					new Hashtable<String, Object>());
			item = threadList.item(i);
			updateTidStamp(item.getAttributes().getNamedItem("tid")
					.getNodeValue());
			NodeList mailAttribList = item.getChildNodes();
			Node attribItem;
			attribHash = new Hashtable<String, String>();
			attribHash.put(MailNotificationEvent.PARTICIPATION, item
					.getAttributes().getNamedItem("participation"));
			attribHash.put(MailNotificationEvent.URL, item.getAttributes()
					.getNamedItem("url").getNodeValue());
			attribHash.put(MailNotificationEvent.MESSAGE_COUNT, item
					.getAttributes().getNamedItem("messages"));

			for (int j = 0; j < mailAttribList.getLength(); j++) {
				attribItem = mailAttribList.item(j);
				if (attribItem.getNodeName().equals("subject")) {
					attribHash.put(MailNotificationEvent.SUBJECT, attribItem
							.getTextContent());
					attribHash.put(MailNotificationEvent.NOTIFICATION_STRING,
							attribItem.getTextContent());
				} else if (attribItem.getNodeName().equals("snippet")) {
					attribHash.put(MailNotificationEvent.SNIPPET, attribItem
							.getTextContent());
				} else if (attribItem.getNodeName().equals("senders")) {
					ArrayList senderList = processSenderList(attribItem,
							mailNotificationEvent);
					attribHash.put(MailNotificationEvent.SENDERS, senderList);
				} else if (attribItem.getNodeName().equals("labels")) {
					String[] strs = attribItem.getTextContent().split(
							GMAIL_LABEL_DELIMITER);
					ArrayList<String> labels = new ArrayList();
					for (String str : strs) {
						labels.add(str);
					}
					attribHash.put(MailNotificationEvent.LABELS, labels);
				}

			}
			mailNotificationEvent.setProperties(attribHash);
			container.getNotificationManager().notifyListeners(
					mailNotificationEvent);
		}

	}

	private void updateTidStamp(String tid) {

		BigInteger bTid = new BigInteger(tid);
		BigInteger bTidStamp = new BigInteger(tidStamp);
		if (bTidStamp.compareTo(bTid) < 0) {
			tidStamp = tid;
		}

	}

}
