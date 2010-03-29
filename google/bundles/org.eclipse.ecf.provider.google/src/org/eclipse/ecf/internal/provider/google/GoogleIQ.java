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

import java.io.StringReader;
import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jivesoftware.smack.packet.IQ;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class GoogleIQ extends IQ {

	public static final String XMLNS_OFF_THE_RECORD = "google:nosave";
	public static final String XMLNS_GOOGLE_SETTING = "google:setting";
	public static final String XMLNS_SHARED_STATUS = "google:shared-status";
	public static final String XMLNS_GMAIL_NOTIFICATIONS = "google:mail:notify";

	private IQ iq;
	private Document childDocument;

	public GoogleIQ(IQ iq) throws XMLParseException {
		this.iq = iq;
		String str = iq.getChildElementXML();
		if (str == null)
			throw new XMLParseException();
		childDocument = parseXMLString(str);
	}

	public String getChildXMLNamespace() {
		Node node;
		try {
			node = childDocument.getChildNodes().item(0).getAttributes()
					.getNamedItem("xmlns");
		} catch (Exception e) {
			return "";
		}
		if (node == null)
			return "";
		return node.getNodeValue();
	}

	public String getPacketID() {
		return iq.getPacketID();
	}

	public void setPacketID(String id) {
		iq.setPacketID(id);
	}

	public IQ.Type getType() {
		return iq.getType();
	}

	public void setType(IQ.Type type) {
		iq.setType(type);
	}

	public String getTo() {
		return iq.getTo();
	}

	public void setTo(String to) {
		iq.setTo(to);
	}

	public String getFrom() {
		return iq.getFrom();
	}

	public void setFrom(String from) {
		iq.setFrom(from);
	}

	public String toXML() {
		return iq.toXML();
	}

	private static Document parseXMLString(String xmlstring) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlstring));
			return db.parse(inStream);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public Document getChildDocument() {
		return childDocument;

	}

	public String getChildElementXML() {
		return iq.getChildElementXML();
	}

}
