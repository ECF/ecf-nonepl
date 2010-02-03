/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.io.Serializable;

public class RemoteSharedObjectEvent implements ISharedObjectMessageEvent,
		Serializable {
	private static final long serialVersionUID = 3257572797621680182L;

	private final ID senderSharedObjectID;

	private final ID remoteContainerID;

	private final Object data;

	public RemoteSharedObjectEvent(ID senderObj, ID remoteCont, Object data) {
		super();
		this.senderSharedObjectID = senderObj;
		this.remoteContainerID = remoteCont;
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.api.events.ISharedObjectEvent#getSenderSharedObject()
	 */
	public ID getSenderSharedObjectID() {
		return senderSharedObjectID;
	}

	public ID getRemoteContainerID() {
		return remoteContainerID;
	}

	public Event getEvent() {
		return this;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("RemoteSharedObjectEvent["); //$NON-NLS-1$
		sb.append(getSenderSharedObjectID()).append(";"); //$NON-NLS-1$
		sb.append(getRemoteContainerID()).append(";"); //$NON-NLS-1$
		sb.append(getData()).append("]"); //$NON-NLS-1$
		return sb.toString();
	}
}