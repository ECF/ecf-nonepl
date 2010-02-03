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
 * Container connected event
 * 
 */
public class ContainerConnectedEvent implements IContainerConnectedEvent {
	private static final long serialVersionUID = 3833467322827617078L;

	private final ID joinedContainerID;

	private final ID localContainerID;

	public ContainerConnectedEvent(ID localContainerID, ID targetID) {
		super();
		this.localContainerID = localContainerID;
		this.joinedContainerID = targetID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerConnectedEvent#getTargetID()
	 */
	public ID getTargetID() {
		return joinedContainerID;
	}

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
		StringBuffer buf = new StringBuffer("ContainerConnectedEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append("]"); //$NON-NLS-1$
		buf.append(getTargetID()).append(";"); //$NON-NLS-1$
		return buf.toString();
	}
}