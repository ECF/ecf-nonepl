/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


/**
 * Manger create event. Sent/triggered when
 * {@link ISharedObjectManager#createSharedObject(org.eclipse.ecf.core.sharedobject.SharedObjectDescription)}
 * is called
 */
public class SharedObjectManagerCreateEvent implements
		ISharedObjectManagerEvent {
	private static final long serialVersionUID = 3905527103070878006L;

	ID localContainerID = null;

	ID sharedObjectID = null;

	public SharedObjectManagerCreateEvent(ID localContainerID, ID sharedObjectID) {
		this.localContainerID = localContainerID;
		this.sharedObjectID = sharedObjectID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerEvent#getLocalContainerID()
	 */
	public ID getLocalContainerID() {
		return localContainerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.ISharedObjectManagerEvent#getSharedObjectID()
	 */
	public ID getSharedObjectID() {
		return sharedObjectID;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("SharedObjectManagerCreateEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append(";"); //$NON-NLS-1$
		buf.append(getSharedObjectID()).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}
