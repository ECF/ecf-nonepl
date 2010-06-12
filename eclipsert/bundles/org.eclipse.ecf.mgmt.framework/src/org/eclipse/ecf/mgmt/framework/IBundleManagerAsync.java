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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IBundleManagerAsync extends IAsyncRemoteServiceProxy {

	public void getBundleSymbolicIdsAsync(IAsyncCallback<String[]> callback);

	public IFuture getBundleSymbolicIdsAsync();

	public void getBundlesAsync(IBundleId bundleId,
			IAsyncCallback<IBundleInfo[]> callback);

	public IFuture getBundlesAsync(IBundleId bundleId);

	public void getBundlesAsync(IAsyncCallback<IBundleInfo[]> callback);

	public IFuture getBundlesAsync();

	public void getBundleAsync(Long bundleid,
			IAsyncCallback<IBundleInfo> callback);

	public IFuture getBundleAsync(Long bundleid);

	public void startAsync(IBundleId bundleId, IAsyncCallback<IStatus> callback);

	public IFuture startAsync(IBundleId bundleId);

	public void startAsync(Long bundleId, IAsyncCallback<IStatus> callback);

	public IFuture startAsync(Long bundleId);

	public void stopAsync(IBundleId bundleId, IAsyncCallback<IStatus> callback);

	public IFuture stopAsync(IBundleId bundleId);

	public void stopAsync(Long bundleId, IAsyncCallback<IStatus> callback);

	public IFuture stopAsync(Long bundleId);

	public void diagnoseAsync(IBundleId bundleId,
			IAsyncCallback<IStatus> callback);

	public IFuture diagnose(IBundleId bundleId);

}
