/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
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
 * Container disconnected event.
 */
public class ContainerDisconnectedEvent implements IContainerDisconnectedEvent {
	private static final long serialVersionUID = 3256437002059527733L;

	private final ID departedContainerID;

	private final ID localContainerID;

	/**
	 * Creates a new ContainerDisconnectedEvent to indicate that the container
	 * has now completely disconnected from its target host.
	 * 
	 * @param localContainerID
	 *            the ID of the local container
	 * @param targetID
	 *            the ID of the target
	 */
	public ContainerDisconnectedEvent(ID localContainerID, ID targetID) {
		this.localContainerID = localContainerID;
		this.departedContainerID = targetID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerDisconnectedEvent#getTargetID()
	 */
	public ID getTargetID() {
		return departedContainerID;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("ContainerDisconnectedEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append(";").append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append(getTargetID()).append(";"); //$NON-NLS-1$
		return buf.toString();
	}
}