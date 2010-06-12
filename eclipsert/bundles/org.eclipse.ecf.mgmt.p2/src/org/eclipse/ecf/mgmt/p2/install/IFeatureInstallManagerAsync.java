package org.eclipse.ecf.mgmt.p2.install;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.IVersionedId;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IFeatureInstallManagerAsync extends IAsyncRemoteServiceProxy {

	public void applyConfigurationAsync(IAsyncCallback<IStatus> callback);

	public IFuture applyConfiguration();

	public void getInstalledFeaturesAsync(String profileId,
			IAsyncCallback<IVersionedId[]> callback);

	public IFuture getInstalledFeaturesAsync(String profileId);

	public void getInstalledFeaturesAsync(
			IAsyncCallback<IVersionedId[]> callback);

	public IFuture getInstalledFeaturesAsync();

	public void getInstallableFeaturesAsync(URI location,
			IAsyncCallback<IVersionedId[]> callback);

	public IFuture getInstallableFeaturesAsync(URI location);

	public void getInstallableFeaturesAsync(
			IAsyncCallback<IVersionedId[]> callback);

	public IFuture getInstallableFeaturesAsync();

	public void installFeatureAsync(IVersionedId featureId, String profileId,
			IAsyncCallback<IStatus> callback);

	public IFuture installFeatureAsync(IVersionedId featureId, String profileId);

	public void installFeatureAsync(IVersionedId featureId,
			IAsyncCallback<IStatus> callback);

	public IFuture installFeatureAsync(IVersionedId featureId);

	public void uninstallFeatureAsync(IVersionedId featureId, String profileId,
			IAsyncCallback<IStatus> callback);

	public IFuture uninstallFeatureAsync(IVersionedId featureId,
			String profileId);

	public void uninstallFeatureAsync(IVersionedId featureId,
			IAsyncCallback<IStatus> callback);

	public IFuture uninstallFeatureAsync(IVersionedId featureId);
}
