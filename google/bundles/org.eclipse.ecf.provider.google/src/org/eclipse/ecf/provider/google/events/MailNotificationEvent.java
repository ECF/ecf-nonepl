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

public class MailNotificationEvent extends NotificationEvent {

	public static final String SUBJECT = "SUBJECT";
	public static final String PARTICIPATION = "PARTICIPATION";
	public static final String MESSAGE_COUNT = "MESSAGE_COUNT";
	public static final String URL = "URL";
	public static final String SENDERS = "SENDERS";
	public static final String LABELS = "LABELS";
	public static final String SNIPPET = "SNIPPET";

	public class SenderElement {

		String name, address;
		boolean isOriginator, isUnread;

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public boolean isOriginator() {
			return isOriginator;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setIsOriginator(String isOriginator) {
			this.isOriginator = Integer.parseInt(isOriginator) == 0 ? false
					: true;
		}

		public void setUnread(String isUnread) {
			this.isUnread = Integer.parseInt(isUnread) == 0 ? false : true;
		}
	}

	public MailNotificationEvent(Hashtable<String, Object> properties) {
		super(properties);
		this.type = NotificationEvent.TYPE_MAIL_NOTIFICATION;
	}

	public MailNotificationEvent(String notification) {
		super(notification);
	}

	public String getTitle() {
		return (String) this.getProperty(MailNotificationEvent.SUBJECT);
	}
}
