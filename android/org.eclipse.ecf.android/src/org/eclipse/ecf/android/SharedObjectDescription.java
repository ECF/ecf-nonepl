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
import java.util.HashMap;
import java.util.Map;

/**
 * Description of a local ISharedObject instance.
 * 
 */
public class SharedObjectDescription implements Serializable {
	private static final long serialVersionUID = -999672007680512082L;

	protected SharedObjectTypeDescription typeDescription;

	protected ID id;

	protected Map properties = null;

	protected SharedObjectDescription(
			SharedObjectTypeDescription typeDescription, ID id, Map properties) {
		this.typeDescription = typeDescription;
		this.id = id;
		this.properties = (properties == null) ? new HashMap() : properties;
	}

	protected SharedObjectDescription(
			SharedObjectTypeDescription typeDescription, ID id) {
		this(typeDescription, id, null);
	}

	public SharedObjectDescription(String typeName, ID id, Map properties) {
		this.typeDescription = new SharedObjectTypeDescription(typeName, null, null, null);
		this.id = id;
		this.properties = (properties == null) ? new HashMap() : properties;
	}

	public SharedObjectDescription(Class clazz, ID id, Map properties) {
		this.typeDescription = new SharedObjectTypeDescription(clazz.getName(),
				null);
		this.id = id;
		this.properties = (properties == null) ? new HashMap() : properties;
	}

	public SharedObjectTypeDescription getTypeDescription() {
		return typeDescription;
	}

	public ID getID() {
		return id;
	}

	public Map getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("SharedObjectDescription["); //$NON-NLS-1$
		sb.append("typeDescription=").append(typeDescription); //$NON-NLS-1$ 
		sb.append(";id=").append(id); //$NON-NLS-1$ 
		sb.append(";props=").append(properties).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ 
		return sb.toString();
	}
}