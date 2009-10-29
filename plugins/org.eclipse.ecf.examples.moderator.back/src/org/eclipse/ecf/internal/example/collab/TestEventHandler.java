/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TestEventHandler implements EventHandler {

	public void handleEvent(Event event) {
		System.out.println("handleEvent\n\ttopic=" + event.getTopic()
				+ "\n\tmessage=" + event.getProperty("message") + "\n\tsender="
				+ event.getProperty("sender"));
	}

}
