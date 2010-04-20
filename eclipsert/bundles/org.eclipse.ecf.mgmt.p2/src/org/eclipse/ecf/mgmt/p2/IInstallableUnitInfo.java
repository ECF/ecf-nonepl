/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2;

import java.util.Collection;
import java.util.Map;


@SuppressWarnings("rawtypes")
public interface IInstallableUnitInfo {

	public IVersionedId getId();

	public Map getProperties();

	public boolean isSingleton();

	public boolean isResolved();

	public Collection getLicenses();

	public ICopyrightInfo getCopyright();

	public IUpdateDescriptorInfo getUpdateDescriptor();

}
