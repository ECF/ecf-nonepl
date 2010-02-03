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

package org.eclipse.ecf.android;


/**
 * Event processor to process SharedObjectMsgEvents
 * 
 * @see IEventProcessor
 * @see BaseSharedObject#addEventProcessor(IEventProcessor)
 */
public class SharedObjectMsgEventProcessor implements IEventProcessor {

	BaseSharedObject sharedObject = null;

	public SharedObjectMsgEventProcessor(BaseSharedObject sharedObject) {
		super();
		this.sharedObject = sharedObject;
	}

	public boolean processEvent(Event event) {
		if (!(event instanceof ISharedObjectMessageEvent))
			return false;
		return processSharedObjectMsgEvent((ISharedObjectMessageEvent) event);
	}

	protected boolean processSharedObjectMsgEvent(
			ISharedObjectMessageEvent event) {
		return sharedObject.handleSharedObjectMsgEvent(event);
	}
}
