/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.profile;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IInstallableUnitInfo;

@SuppressWarnings("rawtypes")
public interface IProfileManager {

	public IStatus addProfile(String profileId, Map properties);

	public IStatus removeProfile(String profileId);

	public String[] getProfileIds();

	public IProfileInfo getProfile(String profileId);

	public IProfileInfo[] getProfiles();

	public IInstallableUnitInfo[] getInstalledFeatures(String profileId);
}
