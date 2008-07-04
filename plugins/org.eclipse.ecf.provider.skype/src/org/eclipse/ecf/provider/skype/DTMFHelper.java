/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter;

import com.skype.Call;
import com.skype.SkypeException;

/**
 *
 */
public class DTMFHelper implements IDTMFCallSessionAdapter {

	private final Call call;

	/**
	 * @param call
	 */
	public DTMFHelper(Call call) {
		this.call = call;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter#sendDTMF(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF)
	 */
	public void sendDTMF(DTMF command) throws CallException {
		final Call.DTMF com = convertDTMF(command);
		if (com == null)
			throw new CallException("Invalid DTMF: " + command.toChar());
		try {
			call.send(com);
		} catch (final SkypeException e) {
			throw new CallException(e);
		}
	}

	/**
	 * @param command
	 * @return
	 */
	private Call.DTMF convertDTMF(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF command) {
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_0))
			return Call.DTMF.TYPE_0;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_1))
			return Call.DTMF.TYPE_1;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_2))
			return Call.DTMF.TYPE_2;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_3))
			return Call.DTMF.TYPE_3;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_4))
			return Call.DTMF.TYPE_4;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_5))
			return Call.DTMF.TYPE_5;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_6))
			return Call.DTMF.TYPE_6;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_7))
			return Call.DTMF.TYPE_7;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_8))
			return Call.DTMF.TYPE_8;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_9))
			return Call.DTMF.TYPE_9;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_SHARP))
			return Call.DTMF.TYPE_SHARP;
		if (command.equals(org.eclipse.ecf.telephony.call.dtmf.IDTMFCallSessionAdapter.DTMF.TYPE_ASTERISK))
			return Call.DTMF.TYPE_ASTERISK;
		return null;
	}

}
