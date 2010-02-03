package org.eclipse.ecf.android;
/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/

import java.io.Serializable;
import java.util.Map;

/**
 * Description of shared object type. This class provides the information
 * necessary to determine the type of a shared object instance. It is used by
 * the SharedObjectDescription class to specify the <b>local</b> type of a
 * shared object instance, and by ReplicaSharedObjectDescription to specify the
 * type of a remote shared object instance.
 * 
 * @see SharedObjectDescription
 */
public class SharedObjectTypeDescription implements Serializable {
	private static final long serialVersionUID = -553771188695892646L;

	protected String name;

	protected ISharedObjectInstantiator instantiator;

	protected String description;

	protected Map typeProperties;

	protected String className;

	public SharedObjectTypeDescription(String name,
			ISharedObjectInstantiator instantiator, String desc, Map props) {
		this.name = name;
		this.instantiator = instantiator;
		this.description = desc;
		this.typeProperties = props;
	}

	public SharedObjectTypeDescription(String className, Map props) {
		this.className = className;
		this.typeProperties = props;
	}

	public String getClassName() {
		return className;
	}

	public String getDescription() {
		return description;
	}

	public ISharedObjectInstantiator getInstantiator() {
		return instantiator;
	}

	public String getName() {
		return name;
	}

	public Map getTypeProperties() {
		return typeProperties;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("SharedObjectTypeDescription["); //$NON-NLS-1$
		buf.append("name=").append(name).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("instantiator=").append(instantiator).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("className=").append(className).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("typeProperties=").append(typeProperties).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return buf.toString();
	}
}
