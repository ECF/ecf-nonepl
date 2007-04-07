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

package org.eclipse.ecf.provider.skype;

import org.eclipse.ecf.call.CallSessionFailureReason;
import org.eclipse.ecf.internal.provider.skype.Messages;

/**
 * 
 */
public class SkypeFailureReason extends CallSessionFailureReason {

	/**
	 * See https://developer.skype.com/Docs/ApiDoc/src
	 */
	public static final String[] reasons = new String[] {
			Messages.SkypeFailureReason_Unknown,
			Messages.SkypeFailureReason_Micellaneous,
			Messages.SkypeFailureReason_User_Not_Exist,
			Messages.SkypeFailureReason_Offline,
			Messages.SkypeFailureReason_No_Proxy,
			Messages.SkypeFailureReason_Session_Terminated,
			Messages.SkypeFailureReason_No_Codec_Found,
			Messages.SkypeFailureReason_IO_Error,
			Messages.SkypeFailureReason_Remote_Sound_Problem,
			Messages.SkypeFailureReason_Recipient_Blocked,
			Messages.SkypeFailureReason_Not_Friend,
			Messages.SkypeFailureReason_Not_Authorized,
			Messages.SkypeFailureReason_Recording_Error };

	/**
	 * @param code
	 */
	public SkypeFailureReason(int code) {
		super(code, (code >= 0 && code < reasons.length) ? reasons[code]
				: reasons[0]);
	}

}
