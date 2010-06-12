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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IInstallableUnitInfo;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IRepositoryManagerAsync extends IAsyncRemoteServiceProxy {

	public void getKnownMetadataRepositoriesAsync(Integer flags,
			IAsyncCallback<URI[]> callback);

	public IFuture getKnownMetadataRepositoriesAsync(Integer flags);

	public void getKnownMetadataRepositoriesAsync(IAsyncCallback<URI[]> callback);

	public IFuture getKnownMetadataRepositoriesAsync();

	public void getKnownArtifactRepositoriesAsync(Integer flags,
			IAsyncCallback<URI[]> callback);

	public IFuture getKnownArtifactRepositoriesAsync(Integer flags);

	public void getKnownArtifactRepositoriesAsync(IAsyncCallback<URI[]> callback);

	public IFuture getKnownArtifactRepositoriesAsync();

	public void addArtifactRepositoryAsync(URI location, Integer flags,
			IAsyncCallback<IStatus> callback);

	public IFuture addArtifactRepositoryAsync(URI location, Integer flags);

	public void addArtifactRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture addArtifactRepositoryAsync(URI location);

	public void addMetadataRepositoryAsync(URI location, Integer flags,
			IAsyncCallback<IStatus> callback);

	public IFuture addMetadataRepositoryAsync(URI location, Integer flags);

	public void addMetadataRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture addMetadataRepositoryAsync(URI location);

	public void removeArtifactRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture removeArtifactRepositoryAsync(URI location);

	public void removeMetadataRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture removeMetadataRepositoryAsync(URI location);

	public void addRepositoryAsync(URI location, Integer flags,
			IAsyncCallback<IStatus> callback);

	public IFuture addRepositoryAsync(URI location, Integer flags);

	public void addRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture addRepositoryAsync(URI location);

	public void removeRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture removeRepositoryAsync(URI location);

	public void refreshArtifactRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture refreshArtifactRepositoryAsync(URI location);

	public void refreshMetadataRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture refreshMetadataRepositoryAsync(URI location);

	public void refreshRepositoryAsync(URI location,
			IAsyncCallback<IStatus> callback);

	public IFuture refreshRepositoryAsync(URI location);

	public void getArtifactRepositoryInfoAsync(Integer flags,
			IAsyncCallback<IRepositoryInfo[]> callback);

	public IFuture getArtifactRepositoryInfoAsync(Integer flags);

	public void getArtifactRepositoryInfoAsync(
			IAsyncCallback<IRepositoryInfo[]> callback);

	public IFuture getArtifactRepositoryInfoAsync();

	public void getArtifactRepositoryInfoAsync(URI location, Integer flags,
			IAsyncCallback<IRepositoryInfo> callback);

	public IFuture getArtifactRepositoryInfoAsync(URI location, Integer flags);

	public void getArtifactRepositoryInfoAsync(URI location,
			IAsyncCallback<IRepositoryInfo> callback);

	public IFuture getArtifactRepositoryInfoAsync(URI location);

	public void getMetadataRepositoryInfoAsync(Integer flags,
			IAsyncCallback<IRepositoryInfo[]> callback);

	public IFuture getMetadataRepositoryInfoAsync(Integer flags);

	public void getMetadataRepositoryInfoAsync(
			IAsyncCallback<IRepositoryInfo[]> callback);

	public IFuture getMetadataRepositoryInfoAsync();

	public void getMetadataRepositoryInfoAsync(URI location, Integer flags,
			IAsyncCallback<IRepositoryInfo> callback);

	public IFuture getMetadataRepositoryInfoAsync(URI location, Integer flags);

	public void getMetadataRepositoryInfoAsync(URI location,
			IAsyncCallback<IRepositoryInfo> callback);

	public IFuture getMetadataRepositoryInfoAsync(URI location);

	public void getInstallableFeaturesAsync(URI location,
			IAsyncCallback<IInstallableUnitInfo[]> callback);

	public IFuture getInstallableFeaturesAsync(URI location);
}
