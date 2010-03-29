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

import java.util.Hashtable;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.internal.provider.xmpp.XMPPContainerPresenceHelper;
import org.eclipse.ecf.internal.provider.xmpp.events.IQEvent;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.jivesoftware.smack.packet.IQ;

public class GoogleContainerPresenceHelper extends XMPPContainerPresenceHelper {

	GoogleContainer container;
	GoogleIQProcessor IQProcessor;
	GoogleStatusMessageManager statusManager;
	Hashtable<String, Boolean> nosaveHashMap = new Hashtable<String, Boolean>();

	public GoogleContainerPresenceHelper(GoogleContainer container) {
		super(container);
		this.container = container;
		IQProcessor = new GoogleIQProcessor(this);
		statusManager = new GoogleStatusMessageManager(container);
	}

	public GoogleStatusMessageManager getStatusMessageManager() {
		return statusManager;
	}

	public void handleEvent(Event event) {
		boolean done = false;
		if (event instanceof IQEvent) {
			done = IQProcessor.handleEvent((IQEvent) event);
		}
		if (!done) {
			super.handleEvent(event);
		}
	}

	public boolean getNosaveState(String jid) {
		return nosaveHashMap.get(jid);
	}

	public void setNoSaveEnabled(final String jid, final boolean isEnabled) {
		IQ iq = new IQ() {

			@Override
			public String getChildElementXML() {
				return "<query xmlns='google:nosave'>    <item xmlns='google:nosave' jid='"
						+ jid
						+ "' value='"
						+ (isEnabled ? "enabled" : "disabled")
						+ "'/>  </query>";

			}

		};

		String user = container.getXMPPConnection().getUser();
		iq.setType(IQ.Type.SET);
		iq.setPacketID("otr-2");
		iq.setFrom(user);
		iq.setTo(user.substring(0, user.lastIndexOf("/")));
		container.getXMPPConnection().sendPacket(iq);

	}

}
