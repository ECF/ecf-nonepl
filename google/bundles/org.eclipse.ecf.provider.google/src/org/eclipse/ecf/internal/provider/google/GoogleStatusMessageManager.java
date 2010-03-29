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

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.jivesoftware.smack.packet.IQ;
import org.w3c.dom.*;

public class GoogleStatusMessageManager {

	public static final String PRESENCE_VERSION = "1";
	public static final int DEFAULT_PRESENCE_UPDATE_PRIORITY = 24;
	private ArrayBlockingQueue<String> defaultStatusList = null; // keeps the
	// default
	// (available mode)
	// status msgs
	private ArrayBlockingQueue<String> dndStatusList = null; // keeps the busy
	// status

	private int statusMax;
	private int statusListMax;
	private int statusListContentsMax;
	private String statusMinVer;

	private String status;
	private boolean isDnd;
	private boolean isInvisible;
	private GoogleContainer container;

	public boolean isInvisible() {
		return isInvisible;
	}

	public void setInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}

	public ArrayBlockingQueue<String> getDefaultStatusList() {
		if (defaultStatusList == null)
			return new ArrayBlockingQueue<String>(0);
		return defaultStatusList;
	}

	// utility function to get an array representation of the queue. the first
	// element in the array is the element added most recently to the queue
	public static ArrayList<String> getStatusListAsReverseArray(
			ArrayBlockingQueue<String> queue) {
		java.util.Iterator<String> it = queue.iterator();
		ArrayList<String> list = new ArrayList<String>(queue.size());
		ArrayList<String> returnList = new ArrayList<String>(queue.size());
		while (it.hasNext()) {
			list.add(it.next());
		}
		for (int j = list.size() - 1; j >= 0; j--) {
			returnList.add(list.get(j));
		}
		return returnList;
	}

	public ArrayBlockingQueue<String> getDndStatusList() {
		return dndStatusList;
	}

	public int getStatusMax() {
		return statusMax;
	}

	public int getStatusListMax() {
		return statusListMax;
	}

	public int getStatusListContentsMax() {
		return statusListContentsMax;
	}

	public String getStatusMinVer() {
		return statusMinVer;
	}

	public String getStatus() {
		return status;
	}

	public void setDnd(boolean isDnd) {
		this.isDnd = isDnd;
	}

	public GoogleStatusMessageManager(GoogleContainer container) {
		this.container = container;
	}

	public void processSharedStatusMessageIQ(GoogleIQ gIQ,
			boolean isRequestResponse) {
		Document doc = gIQ.getChildDocument();
		Node node = doc.getFirstChild();

		if (isRequestResponse) {
			statusMax = Integer.parseInt(node.getAttributes().getNamedItem(
					"status-max").getNodeValue());
			statusListMax = Integer.parseInt(node.getAttributes().getNamedItem(
					"status-list-max").getNodeValue());
			statusListContentsMax = Integer.parseInt(node.getAttributes()
					.getNamedItem("status-list-contents-max").getNodeValue());
			statusMinVer = node.getAttributes().getNamedItem("status-min-ver") == null ? "1"
					: node.getAttributes().getNamedItem("status-min-ver")
							.getNodeValue();
		}
		NodeList childList = node.getChildNodes();
		Node item;
		for (int i = 0; i < childList.getLength(); i++) {
			item = childList.item(i);
			if (item.getNodeName().equals("status")) {
				status = item.getTextContent();
				status = GenericIQProvider.convertToRawText(status);
			} else if (item.getNodeName().equals("show")) {
				isDnd = item.getTextContent().equals("dnd") ? true : false;
			} else if (item.getNodeName().equals("status-list")) {

				NodeList statusNodeList = item.getChildNodes();
				Node statusItem;
				ArrayBlockingQueue<String> statusList = new ArrayBlockingQueue<String>(
						statusListContentsMax);
				ArrayList<String> tempList = new ArrayList<String>(
						statusListContentsMax);
				for (int j = 0; j < statusNodeList.getLength(); j++) {
					statusItem = statusNodeList.item(j);
					if (statusItem.getNodeName().equals("status")) {
						tempList.add(statusItem.getTextContent());
					}
				}
				for (int j = tempList.size() - 1; j >= 0; j--) {
					statusList.offer(tempList.get(j));
				}
				if (item.getAttributes().getNamedItem("show").getNodeValue()
						.equals("dnd")) {
					dndStatusList = statusList;
				} else {
					defaultStatusList = statusList;
				}
			} else if (item.getNodeName().equals("invisible")) {
				isInvisible = item.getAttributes().getNamedItem("value")
						.getNodeValue().equals("true") ? true : false;
			}
		}
		// setStatusMessage("Testing again", false);
	}

	public void setStatusMessage(String message, boolean isDnd) {
		this.isDnd = isDnd;
		this.status = message;
		ArrayBlockingQueue queue = null;
		if (isDnd) {
			if (dndStatusList == null) {
				dndStatusList = new ArrayBlockingQueue<String>(
						statusListContentsMax);
			}
			queue = dndStatusList;
		} else {
			queue = defaultStatusList;
		}

		if (queue.contains(message)) {
			queue.remove(message);
			queue.offer(message);
		} else {
			boolean success = queue.offer(message);
			if (!success) {
				queue.poll();
				queue.offer(message);
			}
		}

		if (isDnd) {
			dndStatusList = queue;
		} else {
			defaultStatusList = queue;
		}

		// create the child xml element;
		String childXmlElement;
		childXmlElement = "<query xmlns='google:shared-status' version='"
				+ PRESENCE_VERSION + "'>";
		childXmlElement += "<status>" + message + "</status>";
		childXmlElement += "<show>" + (isDnd ? "dnd" : "default") + "</show>";
		childXmlElement += "<status-list show='default'>";

		ArrayList<String> list = getStatusListAsReverseArray(defaultStatusList);
		for (String str : list) {
			childXmlElement += "<status>" + str + "</status>";
		}
		childXmlElement += "</status-list>";

		if (dndStatusList != null) {
			childXmlElement += "<status-list show='dnd'>";
			list = getStatusListAsReverseArray(dndStatusList);

			for (String str : list) {
				childXmlElement += "<status>" + str + "</status>";
			}
			childXmlElement += "</status-list>";
		}

		childXmlElement += "<invisible value='"
				+ (isInvisible ? "true" : "false") + "'/>";
		childXmlElement += "</query>";

		final String finalChildXmlElement = childXmlElement;
		IQ iq = new IQ() {

			@Override
			public String getChildElementXML() {
				return finalChildXmlElement;
			}

		};
		String user = container.getXMPPConnection().getUser();
		iq.setTo(user.substring(0, user.lastIndexOf("/")));
		iq.setType(IQ.Type.SET);
		container.getXMPPConnection().sendPacket(iq);

		/*
		 * Presence presence = new Presence(Presence.Type.AVAILABLE, message,
		 * isDnd ? Presence.Mode.DND : Presence.Mode.CHAT); try {
		 * container.getRosterManager().getPresenceSender()
		 * .sendPresenceUpdate(null, presence); } catch (ECFException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public void setStatusMessage(String status) {
		setStatusMessage(status, isDnd);
	}

	public void alterDnd() {
		isDnd = !isDnd;
		setStatusMessage(status);
	}

	public boolean isDnd() {
		return isDnd;
	}

}
