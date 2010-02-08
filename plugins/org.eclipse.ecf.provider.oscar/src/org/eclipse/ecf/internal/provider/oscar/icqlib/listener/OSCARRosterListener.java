/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.icqlib.listener;

import ru.caffeineim.protocols.icq.integration.events.*;
import ru.caffeineim.protocols.icq.integration.listeners.ContactListListener;

public class OSCARRosterListener implements ContactListListener {

	public void onSsiAuthReply(SsiAuthReplyEvent e) {
		// XXX
	}

	public void onSsiAuthRequest(SsiAuthRequestEvent e) {
		// XXX
	}

	public void onSsiFutureAuthGrant(SsiFutureAuthGrantEvent e) {
		// XXX
	}

	public void onSsiModifyingAck(SsiModifyingAckEvent e) {
		// XXX
	}

	public void onUpdateContactList(ContactListEvent e) {
		// XXX
	}
}
