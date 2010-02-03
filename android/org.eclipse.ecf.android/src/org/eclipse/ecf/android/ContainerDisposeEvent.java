/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


public class ContainerDisposeEvent implements IContainerDisposeEvent {
	private static final long serialVersionUID = 3618138961349062706L;

	private final ID localContainerID;

	public ContainerDisposeEvent(ID localContainerID) {
		super();
		this.localContainerID = localContainerID;
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
		StringBuffer buf = new StringBuffer("ContainerDisposeEvent["); //$NON-NLS-1$
		buf.append(getLocalContainerID()).append("]"); //$NON-NLS-1$
		return buf.toString();
	}
}