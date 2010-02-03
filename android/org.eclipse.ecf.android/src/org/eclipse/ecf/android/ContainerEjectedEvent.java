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

public class ContainerEjectedEvent implements IContainerEjectedEvent {
	private static final long serialVersionUID = 3257567299946033970L;

	private final ID localContainerID;

	private final ID groupID;

	private final Serializable reason;

	public ContainerEjectedEvent(ID localContainerID, ID targetID, Serializable reason) {
		super();
		this.localContainerID = localContainerID;
		this.groupID = targetID;
		this.reason = reason;
	}

	public ID getTargetID() {
		return groupID;
	}

	public ID getLocalContainerID() {
		return localContainerID;
	}

	public Serializable getReason() {
		return reason;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("ContainerEjectedEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append(";"); //$NON-NLS-1$
		buf.append(getTargetID()).append(";"); //$NON-NLS-1$
		buf.append(getReason()).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}