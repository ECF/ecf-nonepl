/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


public class ContainerDisconnectingEvent implements IContainerDisconnectingEvent {
	private static final long serialVersionUID = 3257570607204742200L;

	ID localContainerID;

	ID groupID;

	public ContainerDisconnectingEvent(ID localContainerID, ID targetID) {
		this.localContainerID = localContainerID;
		this.groupID = targetID;
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
	 * @see org.eclipse.ecf.core.events.IContainerDisconnectingEvent#getTargetID()
	 */
	public ID getTargetID() {
		return groupID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("ContainerDisconnectingEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append(";"); //$NON-NLS-1$
		buf.append(getTargetID()).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}