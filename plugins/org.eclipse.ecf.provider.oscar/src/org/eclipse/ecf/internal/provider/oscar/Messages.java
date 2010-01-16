/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.oscar.messages"; //$NON-NLS-1$

	public static String OSCAR_NAMESPACE_EXCEPTION_ID_CREATE;
	public static String OSCARID_EXCEPTION_USERNAME_NOT_NULL;
	public static String OSCARID_EXCEPTION_INVALID_UID;
	public static String OSCAR_CONNECTION_EXCEPTION_ALREADY_CONNECTED;
	public static String OSCAR_CONNECTION_EXCEPTION_LOGIN_FAILED;
	public static String OSCAR_CONNECTION_EXCEPTION_NO_DATA;
	public static String OSCAR_CONNECTION_EXCEPTION_SEND_FAILED;
	public static String OSCAR_CONNECTION_EXCEPTION_INTERRUPT;
	public static String OSCAR_CONTAINER_EXCEPTION_INVALID_RESPONSE_FROM_SERVER;
	public static String OSCAR_CONTAINER_EXCEPTION_HANDLING_ASYCH_EVENT;
	public static String OSCAR_CONTAINER_EXCEPTION_DESERIALIZED_OBJECT_NULL;
	public static String OSCAR_CONTAINER_UNRECOGONIZED_CONTAINER_MESSAGE;
	public static String OSCAR_CONTAINER_UNEXPECTED_EVENT;
	public static String OSCAR_CHAT_EXCEPTION_RECEIVER_NULL;
	public static String OSCAR_CHAT_EXCEPTION_SEND_FAILED;
	public static String OSCAR_CHAT_EXCEPTION_ID_IS_NOT_OSCARID;
	public static String OSCAR_SO_MESSAGE_ERROR;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// empty constructor
	}
}
