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
import java.util.Map;

/**
 * Description of a remote ISharedObject instance.
 * 
 */
public class ReplicaSharedObjectDescription extends SharedObjectDescription implements Serializable {
	private static final long serialVersionUID = 2764430278848370713L;

	protected static long staticID = 0;

	public static long getNextUniqueIdentifier() {
		return staticID++;
	}

	protected ID homeID;

	protected long identifier;

	public ReplicaSharedObjectDescription(SharedObjectTypeDescription type, ID objectID, ID homeID, Map props, long ident) {
		super(type, objectID, props);
		this.homeID = homeID;
		this.identifier = ident;
	}

	public ReplicaSharedObjectDescription(String typeName, ID objectID, ID homeID, Map props, long ident) {
		super(new SharedObjectTypeDescription(typeName, null, null, null), objectID, props);
		this.homeID = homeID;
		this.identifier = ident;
	}

	public ReplicaSharedObjectDescription(String typeName, ID objectID, ID homeID, Map props) {
		this(typeName, objectID, homeID, props, getNextUniqueIdentifier());
	}

	public ReplicaSharedObjectDescription(String typeName, ID objectID, ID homeID) {
		this(typeName, objectID, homeID, null);
	}

	public ReplicaSharedObjectDescription(Class clazz, ID objectID, ID homeID, Map props, long ident) {
		super(new SharedObjectTypeDescription(clazz.getName(), null), objectID, props);
		this.homeID = homeID;
		this.identifier = ident;
	}

	public ReplicaSharedObjectDescription(Class clazz, ID objectID, ID homeID, Map props) {
		this(clazz, objectID, homeID, props, getNextUniqueIdentifier());
	}

	public ReplicaSharedObjectDescription(Class clazz, ID objectID, ID homeID) {
		this(clazz, objectID, homeID, null);
	}

	public ReplicaSharedObjectDescription(Class clazz, ID objectID) {
		this(clazz, objectID, null, null);
	}

	public ID getHomeID() {
		return homeID;
	}

	public long getIdentifier() {
		return identifier;
	}

	public void setID(ID theID) {
		this.id = theID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("ReplicaSharedObjectDescription["); //$NON-NLS-1$
		sb.append("type=").append(typeDescription).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("id=").append(id).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("homeID=").append(homeID).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("ident=").append(identifier).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}
}