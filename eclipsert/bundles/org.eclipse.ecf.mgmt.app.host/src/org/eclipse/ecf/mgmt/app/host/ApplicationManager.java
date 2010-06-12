/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.app.host;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.app.host.Activator;
import org.eclipse.ecf.mgmt.app.ApplicationInfo;
import org.eclipse.ecf.mgmt.app.ApplicationInstanceInfo;
import org.eclipse.ecf.mgmt.app.IApplicationInfo;
import org.eclipse.ecf.mgmt.app.IApplicationInstanceInfo;
import org.eclipse.ecf.mgmt.app.IApplicationManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class ApplicationManager implements IApplicationManager, IAdaptable {

	private BundleContext context;
	private LogService log;
	private Object lock = new Object();
	private ServiceTracker applicationDescriptorsTracker;
	private final Object applicationDescriptorsLock = new Object();
	private ServiceTracker applicationInstancesTracker;
	private final Object applicationInstancesLock = new Object();

	public ApplicationManager(BundleContext context, LogService log) {
		this.context = context;
		this.log = log;
	}

	public ApplicationManager(BundleContext context) {
		this(context, null);
	}

	public IApplicationInfo[] getApplications() {
		ServiceReference[] appDescriptorServiceReferences = getApplicationDescriptorServiceReferences();
		List results = new ArrayList();
		if (appDescriptorServiceReferences == null
				|| appDescriptorServiceReferences.length == 0)
			return (IApplicationInfo[]) results
					.toArray(new IApplicationInfo[] {});
		for (int i = 0; i < appDescriptorServiceReferences.length; i++) {
			ApplicationDescriptor appDescriptor = getApplicationDescriptor(appDescriptorServiceReferences[i]);
			if (appDescriptor != null)
				results.add(new ApplicationInfo(appDescriptor));
			ungetServiceReference(appDescriptorServiceReferences[i]);
		}
		return (IApplicationInfo[]) results.toArray(new IApplicationInfo[] {});
	}

	private ApplicationDescriptor getApplicationDescriptor(
			ServiceReference serviceReference) {
		if (context == null)
			return null;
		return (ApplicationDescriptor) context.getService(serviceReference);
	}

	public IApplicationInstanceInfo[] getRunningApplications() {
		ServiceReference[] appInstanceServiceReferences = getApplicationInstanceServiceReferences();
		List results = new ArrayList();
		if (appInstanceServiceReferences == null
				|| appInstanceServiceReferences.length == 0)
			return (IApplicationInstanceInfo[]) results
					.toArray(new IApplicationInstanceInfo[] {});
		for (int i = 0; i < appInstanceServiceReferences.length; i++) {
			ApplicationHandle appInstanceHandle = getApplicationInstanceDescriptor(appInstanceServiceReferences[i]);
			if (appInstanceHandle != null)
				results.add(new ApplicationInstanceInfo(appInstanceHandle));
			ungetServiceReference(appInstanceServiceReferences[i]);
		}
		return (IApplicationInstanceInfo[]) results
				.toArray(new IApplicationInstanceInfo[] {});
	}

	private ApplicationHandle getApplicationInstanceDescriptor(
			ServiceReference serviceReference) {
		if (context == null)
			return null;
		return (ApplicationHandle) context.getService(serviceReference);
	}

	private ServiceReference[] getApplicationInstanceServiceReferences() {
		synchronized (applicationInstancesLock) {
			if (applicationInstancesTracker == null) {
				applicationInstancesTracker = new ServiceTracker(context,
						org.osgi.service.application.ApplicationHandle.class
								.getName(), null);
				applicationInstancesTracker.open();
			}
		}
		return applicationInstancesTracker.getServiceReferences();
	}

	public IStatus start(String applicationId, String[] applicationArgs) {
		if (applicationId == null)
			return createErrorStatus("applicationId cannot be null"); //$NON-NLS-1$
		ServiceReference appServiceReference = getApplicationDescriptorServiceReference(applicationId);
		if (appServiceReference == null)
			return createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		ApplicationDescriptor appDescriptor = getApplicationDescriptor(appServiceReference);
		if (appDescriptor == null)
			return createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		HashMap args = new HashMap();
		if (applicationArgs != null)
			args.put("application.args", applicationArgs); //$NON-NLS-1$
		try {
			appDescriptor.launch(args);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (Exception e) {
			logException(
					"start exception for applicationId=" + applicationId, e); //$NON-NLS-1$
			return createErrorStatus(
					"Exception starting applicationId=" + applicationId, e); //$NON-NLS-1$
		} finally {
			ungetServiceReference(appServiceReference);
		}
	}

	private void ungetServiceReference(ServiceReference appServiceReference) {
		if (context == null)
			return;
		context.ungetService(appServiceReference);
	}

	public IStatus stop(String applicationInstanceId) {
		if (applicationInstanceId == null)
			return createErrorStatus("applicationInstanceId cannot be null"); //$NON-NLS-1$
		ServiceReference appInstanceServiceReference = getApplicationInstanceServiceReference(applicationInstanceId);
		if (appInstanceServiceReference == null)
			return createErrorStatus("appInstance service reference not found for applicationInstanceId=" + applicationInstanceId); //$NON-NLS-1$

		ApplicationHandle appInstance = getApplicationInstanceDescriptor(appInstanceServiceReference);
		if (appInstance == null)
			return createErrorStatus("appInstance not found for applicationInstanceId=" + applicationInstanceId); //$NON-NLS-1$
		try {
			appInstance.destroy();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (Exception e) {
			logException(
					"stop exception for applicationInstanceId=" + applicationInstanceId, e); //$NON-NLS-1$
			return createErrorStatus(
					"could not destroy applicationInstanceId=" + applicationInstanceId, e); //$NON-NLS-1$
		} finally {
			ungetServiceReference(appInstanceServiceReference);
		}
	}

	public IStatus lock(String applicationId) {
		if (applicationId == null)
			createErrorStatus("applicationId cannot be null"); //$NON-NLS-1$
		ServiceReference appServiceReference = getApplicationDescriptorServiceReference(applicationId);
		if (appServiceReference == null)
			createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		ApplicationDescriptor appDescriptor = getApplicationDescriptor(appServiceReference);
		if (appDescriptor == null)
			createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		try {
			appDescriptor.lock();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (Exception e) {
			logException("lock exception for applicationId=" + applicationId, e); //$NON-NLS-1$
			return createErrorStatus(
					"could not lock applicationId=" + applicationId, e); //$NON-NLS-1$
		} finally {
			ungetServiceReference(appServiceReference);
		}
	}

	public IStatus unlock(String applicationId) {
		if (applicationId == null)
			createErrorStatus("applicationId cannot be null"); //$NON-NLS-1$
		ServiceReference appServiceReference = getApplicationDescriptorServiceReference(applicationId);
		if (appServiceReference == null)
			createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		ApplicationDescriptor appDescriptor = getApplicationDescriptor(appServiceReference);
		if (appDescriptor == null)
			createErrorStatus("application descriptor cannot be found for applicationId=" + applicationId); //$NON-NLS-1$
		try {
			appDescriptor.unlock();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (Exception e) {
			logException(
					"unlock exception for applicationId=" + applicationId, e); //$NON-NLS-1$
			return createErrorStatus(
					"could not unlock applicationId=" + applicationId, e); //$NON-NLS-1$
		} finally {
			ungetServiceReference(appServiceReference);
		}
	}

	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		final IAdapterManager adapterManager = Activator.getDefault()
				.getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

	public void close() {
		synchronized (lock) {
			synchronized (applicationDescriptorsLock) {
				if (applicationDescriptorsTracker != null) {
					applicationDescriptorsTracker.close();
					applicationDescriptorsTracker = null;
				}
			}
			synchronized (applicationInstancesLock) {
				if (applicationInstancesTracker != null) {
					applicationInstancesTracker.close();
					applicationInstancesTracker = null;
				}
			}
			context = null;
			log = null;
		}
	}

	protected ServiceReference[] getApplicationDescriptorServiceReferences() {
		synchronized (applicationDescriptorsLock) {
			if (applicationDescriptorsTracker == null) {
				applicationDescriptorsTracker = new ServiceTracker(context,
						ApplicationDescriptor.class.getName(), null);
				applicationDescriptorsTracker.open();
			}
		}
		return applicationDescriptorsTracker.getServiceReferences();
	}

	private void logException(String message, Throwable exception) {
		if (log != null) {
			log.log(LogService.LOG_ERROR, message, exception);
		} else {
			if (message != null)
				System.err.println(message);
			if (exception != null)
				exception.printStackTrace(System.err);
		}
	}

	private ServiceReference getApplicationDescriptorServiceReference(
			String applicationId) {
		ServiceReference[] appDescriptorServiceReferences = getApplicationDescriptorServiceReferences();

		if (appDescriptorServiceReferences == null || applicationId == null)
			return null;
		ServiceReference result = null;
		for (int i = 0; i < appDescriptorServiceReferences.length; i++) {
			String id = (String) appDescriptorServiceReferences[i]
					.getProperty(Constants.SERVICE_PID);
			if (applicationId.equals(id))
				return appDescriptorServiceReferences[i];
		}
		return result;
	}

	private ServiceReference getApplicationInstanceServiceReference(
			String applicationInstanceId) {
		ServiceReference[] appInstanceServiceReferences = getApplicationInstanceServiceReferences();
		if (appInstanceServiceReferences == null
				|| applicationInstanceId == null)
			return null;
		ServiceReference result = null;
		for (int i = 0; i < appInstanceServiceReferences.length; i++) {
			String id = (String) appInstanceServiceReferences[i]
					.getProperty(Constants.SERVICE_PID);
			if (applicationInstanceId.equals(id))
				return appInstanceServiceReferences[i];
		}
		return result;
	}

	private IStatus createErrorStatus(String message, Throwable e) {
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				message, e);
	}

	private IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

}
