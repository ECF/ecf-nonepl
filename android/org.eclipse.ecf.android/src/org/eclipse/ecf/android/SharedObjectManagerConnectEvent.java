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
 * Shared object manager connection event. Instances implementing this interface
 * are sent to IContainerListeners when the
 * {@link ISharedObjectManager#connectSharedObjects(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.identity.ID[])}
 * is called.
 * 
 */
public class SharedObjectManagerConnectEvent implements
		ISharedObjectManagerConnectionEvent {
	private static final long serialVersionUID = 3544670676712633650L;

	ID localContainerID = null;

	ISharedObjectConnector connector = null;

	public SharedObjectManagerConnectEvent(ID localContainerID,
			ISharedObjectConnector connector) {
		this.localContainerID = localContainerID;
		this.connector = connector;
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
	 * @see org.eclipse.ecf.core.sharedobject.events.ISharedObjectManagerConnectionEvent#getConnector()
	 */
	public ISharedObjectConnector getConnector() {
		return connector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.ISharedObjectManagerEvent#getSharedObjectID()
	 */
	public ID getSharedObjectID() {
		return connector.getSenderID();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("SharedObjectManagerConnectEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append(";"); //$NON-NLS-1$
		buf.append(getConnector()).append(";"); //$NON-NLS-1$
		buf.append(getSharedObjectID()).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}
