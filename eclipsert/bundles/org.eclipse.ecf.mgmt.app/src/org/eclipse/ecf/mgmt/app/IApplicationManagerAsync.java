package org.eclipse.ecf.mgmt.app;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IApplicationManagerAsync extends IAsyncRemoteServiceProxy {

	public void getApplicationsAsync(IAsyncCallback<IApplicationInfo[]> callback);
	public IFuture getApplicationsAsync();
	
	public void getRunningApplicationsAsync(IAsyncCallback<IApplicationInstanceInfo[]> callback);
	public IFuture getRunningApplicationsAsync();
	
	public void startAsync(String applicationId, String[] applicationArgs, IAsyncCallback<IStatus> callback);
	public IFuture startAsync(String applicationId, String[] applicationArgs);
	
	public void stopAsync(String applicationInstanceId, IAsyncCallback<IStatus> callback);
	public IFuture stopAsync(String applicationInstanceId);
	
	public void lockAsync(String applicationId, IAsyncCallback<IStatus> callback);
	public IFuture lockAsync(String applicationId);
	
	public void unlockAsync(String applicationId, IAsyncCallback<IStatus> callback);
	public IFuture unlockAsync(String applicationId);

}
