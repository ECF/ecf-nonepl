/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

public class NamedGroup {
	Connector parent;
	String name;

	public NamedGroup(String name) {
		this.name = name;
	}

	public void setParent(Connector c) {
		this.parent = c;
	}

	public Connector getConnector() {
		return parent;
	}

	public String getRawName() {
		return name;
	}

	public String getName() {
		return cleanGroupName(name);
	}

	public String getIDForGroup() {
		return parent.getID() + getName();
	}

	protected String cleanGroupName(String n) {
		String res = ((n.startsWith("/")) ? n : "/" + n); //$NON-NLS-1$ //$NON-NLS-2$
		return res;
	}
}