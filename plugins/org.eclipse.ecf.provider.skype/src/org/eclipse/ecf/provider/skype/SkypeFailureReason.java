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

import org.eclipse.ecf.call.FailureReason;

/**
 *
 */
public class SkypeFailureReason extends FailureReason {

	/**
	 * See https://developer.skype.com/Docs/Skype4COMLib/TCallFailureReason
	 */
	public static final String [] reasons = new String[] {
		"unknown",
		"miscellaneous error",
		"user or phone number does not exist",
		"user is offline",
		"no proxy found",
		"session terminated",
		"no common codec found",
		"audio I/O error",
		"problem with remote sound device",
		"recipient blocked call",
		"recipient not friend",
		"user not authorized by recipient",
		"sound recording error"
	};
	
	/**
	 * @param code
	 */
	public SkypeFailureReason(int code) {
		super(code,reasons[code]);
	}

}
