/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;

public abstract class ResolvedDependencyInfo implements
		IResolvedDependencyInfo, Serializable {

	private static final long serialVersionUID = 6918193266499867829L;
	private final String name;
	protected IBundleId bundleId;

	public ResolvedDependencyInfo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IBundleId getResolvingBundle() {
		return bundleId;
	}

}
