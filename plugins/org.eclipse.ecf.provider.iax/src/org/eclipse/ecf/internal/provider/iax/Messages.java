/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.iax;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.iax.messages"; //$NON-NLS-1$
	public static String SharedObjectCallContainerAdapter_Exception_Not_Null;
	public static String SharedObjectCallContainerAdapter_Exception_IAX;
	public static String IAXCallSession_Exception_Call_Wrong_State;
	public static String IAXCallSession_Exception_Invalid_Receiver;
	public static String IAXFailureReason_IO_Error;
	public static String IAXFailureReason_Micellaneous;
	public static String IAXFailureReason_No_Codec_Found;
	public static String IAXFailureReason_No_Proxy;
	public static String IAXFailureReason_Not_Authorized;
	public static String IAXFailureReason_Not_Friend;
	public static String IAXFailureReason_Offline;
	public static String IAXFailureReason_Recipient_Blocked;
	public static String IAXFailureReason_Recording_Error;
	public static String IAXFailureReason_Remote_Sound_Problem;
	public static String IAXFailureReason_Session_Terminated;
	public static String IAXFailureReason_Unknown;
	public static String IAXFailureReason_User_Not_Exist;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
