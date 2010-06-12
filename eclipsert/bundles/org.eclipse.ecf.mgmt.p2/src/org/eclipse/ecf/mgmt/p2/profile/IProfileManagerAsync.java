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
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IProfileManagerAsync extends IAsyncRemoteServiceProxy {

	@SuppressWarnings("rawtypes")
	public void addProfileAsync(String profileId, Map properties,
			IAsyncCallback<IStatus> callback);

	@SuppressWarnings("rawtypes")
	public IFuture addProfileAsync(String profileId, Map properties);

	public IStatus removeProfileAsync(String profileId,
			IAsyncCallback<IStatus> callback);

	public IFuture removeProfileAsync(String profileId);

	public void getProfileIdsAsync(IAsyncCallback<String[]> callback);

	public IFuture getProfileIdsAsync();

	public void getProfileAsync(String profileId,
			IAsyncCallback<IProfileInfo> callback);

	public IFuture getProfileAsync(String profileId);

	public void getProfilesAsync(IAsyncCallback<IProfileInfo[]> callback);

	public IFuture getProfilesAsync();

	public void getInstalledFeaturesAsync(String profileId,
			IAsyncCallback<IInstallableUnitInfo[]> callback);

	public IFuture getInstalledFeaturesAsync(String profileId);
}
