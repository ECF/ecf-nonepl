package org.eclipse.ecf.mgmt.ds;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IScrManagerAsync extends IAsyncRemoteServiceProxy {

	public void getComponentInfoAsync(IBundleId bundleId, IAsyncCallback<IComponentInfo[]> callback);
	public IFuture getComponentInfoAsync(IBundleId bundleId);
	public void getComponentInfoAsync(Long componentId, IAsyncCallback<IComponentInfo> callback);
	public IFuture getComponentInfoAsync(Long componentId);
	public void getAllComponentInfoAsync(IAsyncCallback<IComponentInfo[]> callback);
	public IFuture getAllComponentInfoAsync();

	public void enableAsync(Long componentId, IAsyncCallback<IStatus> callback);
	public IFuture enableAsync(Long componentId);
	public void disableAsync(Long componentId, IAsyncCallback<IStatus> callback);
	public IFuture disableAsync(Long componentId);

}
