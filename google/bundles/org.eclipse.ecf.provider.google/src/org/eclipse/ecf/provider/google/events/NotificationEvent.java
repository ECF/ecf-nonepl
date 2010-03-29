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
package org.eclipse.ecf.provider.google.events;

import java.util.Hashtable;

public class NotificationEvent implements org.eclipse.ecf.core.util.Event,
		Comparable<NotificationEvent> {

	/**
	 * 
	 */

	private static final long serialVersionUID = 6778230948680452081L;

	public static final String TYPE_MAIL_NOTIFICATION = "MAIL_NOTIFICATION";
	public static final String TYPE_UNDEFINED = "UNDEFINED";
	public static final String TYPE_INCOMING_CALL = "INCOMING_CALL";
	public static final String NOTIFICATION_STRING = "NOTIFICATION_STRING";

	public static final String JID = "JID";

	public static final String INCOMING_CALL_EVENT = "INCOMING_CALL_EVENT";

	public static final String INCOMING_FILE_EVENT = "INCOMING_FILE_EVENT";

	public static final String TYPE_INCOMING_FILE = "INCOMING_FILE";

	public static final String FILE_SAVE_LOCATION_CHANGE_EVENT = "FILE_SAVE_LOCATION_CHANGE_EVENT";

	public static final String FILE_SAVE_LOCATION = "FILE_SAVE_LOCATION";

	private Hashtable<String, Object> properties = new Hashtable<String, Object>();

	protected String type;

	private long timeStamp;

	public long getTimeStamp() {
		return timeStamp;
	}

	public NotificationEvent(Hashtable<String, Object> properties) {
		this(TYPE_UNDEFINED, properties);
		// this.type = TYPE_UNDEFINED;
		// this.properties = properties;
		// timeStamp = System.currentTimeMillis();
	}

	public void initialize(String type, Hashtable<String, Object> properties) {
		this.type = type;
		this.properties = properties;
		if (properties.get(NOTIFICATION_STRING) == null) {
			properties.put(NOTIFICATION_STRING, "");
		}

	}

	public NotificationEvent(String type, Hashtable<String, Object> properties) {
		initialize(type, properties);
	}

	public NotificationEvent(String notification) {
		// this.type = TYPE_UNDEFINED;
		Hashtable<String, Object> prop = new Hashtable<String, Object>();
		prop.put(NOTIFICATION_STRING, notification);
		initialize(TYPE_UNDEFINED, prop);
	}

	public void setProperties(Hashtable<String, Object> properties) {
		this.properties = properties;
	}

	public String getNotification() {
		return (String) properties.get(NOTIFICATION_STRING);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public Object getProperty(String property) {
		return properties.get(property);
	}

	public Hashtable getProperties() {
		return properties;
	}

	public int compareTo(NotificationEvent otherNotification) {
		if (getTimeStamp() < otherNotification.getTimeStamp())
			return -1;
		if (getTimeStamp() == otherNotification.getTimeStamp())
			return 0;
		return 1;

	}

	public String getTitle() {
		if (this.getType().equals(NotificationEvent.TYPE_INCOMING_CALL)) {
			return "Incoming Call...";
		} else if (this.getType().equals(NotificationEvent.TYPE_UNDEFINED)) {
			return "";
		}
		return "";
	}
}
