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

/*
 * Created on Nov 29, 2004
 *  
 */
package org.eclipse.ecf.android;

import java.util.HashMap;
import java.util.Map;

public class SOConfig implements ISharedObjectConfig {
	protected SOContainer container = null;
	protected ID sharedObjectID;
	protected ID homeContainerID;
	protected boolean isActive;
	protected Map properties;
	protected SOContext context;

	public SOConfig(ID sharedObjectID, ID homeContainerID, SOContainer cont, Map dict) {
		super();
		this.sharedObjectID = sharedObjectID;
		this.homeContainerID = homeContainerID;
		isActive = false;
		properties = dict;
		this.container = cont;
	}

	protected void makeActive(IQueueEnqueue queue) {
		isActive = true;
		if (container.getID().equals(homeContainerID)) {
			this.context = container.createSharedObjectContext(this, queue);
		} else {
			this.context = container.createRemoteSharedObjectContext(this, queue);
		}
	}

	protected synchronized void makeInactive() {
		if (isActive) {
			this.context.makeInactive();
			this.context = null;
			isActive = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectConfig#getSharedObjectID()
	 */
	public ID getSharedObjectID() {
		return sharedObjectID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectConfig#getHomeContainerID()
	 */
	public ID getHomeContainerID() {
		return homeContainerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectConfig#getContext()
	 */
	public ISharedObjectContext getContext() {
		if (isActive) {
			return context;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectConfig#getProperties()
	 */
	public Map getProperties() {
		if (properties == null)
			return new HashMap();
		return properties;
	}
}