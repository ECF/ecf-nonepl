/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository;

import java.net.URI;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface IRepositoryInfo {

	public String getDescription();

	public URI getLocation();

	public String getName();

	public Map getProperties();

	public String getProvider();

	public String getType();

	public String getVersion();

	public boolean isModifiable();
}
