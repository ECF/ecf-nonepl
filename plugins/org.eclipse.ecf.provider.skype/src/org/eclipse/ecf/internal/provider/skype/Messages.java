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

package org.eclipse.ecf.internal.provider.skype;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.skype.messages"; //$NON-NLS-1$
	public static String SharedObjectCallContainerAdapter_Exception_Not_Null;
	public static String SharedObjectCallContainerAdapter_Exception_Skype;
	public static String SkypeCallContainerAdapter_EXCEPTION_CONFERENCE_CALL_NOT_SUPPORTED;
	public static String SkypeCallContainerAdapter_EXCEPTION_UNEXPECTED;
	public static String SkypeCallSession_Exception_Invalid_Receiver;
	public static String SkypeChannelSOConfig_EXCEPTION_COULD_NOT_CREATE_APP;
	public static String SkypeChatManager_EXCEPTION_INVALID_SKYPE_ID;
	public static String SkypeChatManager_EXCEPTION_SKYPE_EXCEPTION;
	public static String SkypeContainer_EXCEPTION_COULDNOT_CONNECT;
	public static String SkypeContainerInstantiator_EXCEPTION_COULD_NOT_CREATE_CONTAINER;
	public static String SkypeFailureReason_IO_Error;
	public static String SkypeFailureReason_Micellaneous;
	public static String SkypeFailureReason_No_Codec_Found;
	public static String SkypeFailureReason_No_Proxy;
	public static String SkypeFailureReason_Not_Authorized;
	public static String SkypeFailureReason_Not_Friend;
	public static String SkypeFailureReason_Offline;
	public static String SkypeFailureReason_Recipient_Blocked;
	public static String SkypeFailureReason_Recording_Error;
	public static String SkypeFailureReason_Remote_Sound_Problem;
	public static String SkypeFailureReason_Session_Terminated;
	public static String SkypeFailureReason_Unknown;
	public static String SkypeFailureReason_User_Not_Exist;
	public static String SkypeRosterManager_EXCEPTION_GETTING_AVATAR;
	public static String SkypeRosterManager_EXCEPTION_PROFILE_NOT_AVAILABLE;
	public static String SkypeRosterManager_EXCEPTION_USER_STATUS;
	public static String SkypeSOContext_EXCEPTION_APP_NOT_AVAILABLE;
	public static String SkypeSOContext_EXCEPTION_SKYPE_FRIEND_NO_APPLICATION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
