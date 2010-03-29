package org.eclipse.ecf.tests.mgmt.p2;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	private static Activator instance;
	private BundleContext context;
	private IProvisioningAgent agent;
	
	public static Activator getDefault() {
		return instance;
	}
	
	public IProvisioningAgent getProvisioningAgent() {
		return agent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
		ServiceTracker st = new ServiceTracker(context, IProvisioningAgent.class.getName(), this);
		st.open();
		st.close();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		this.agent = null;
		this.context = null;
		instance= null;
	}

	public Object addingService(ServiceReference reference) {
		this.agent = (IProvisioningAgent) context.getService(reference);
		return agent;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
		
	}

	public void removedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
		
	}

	public BundleContext getContext() {
		return context;
	}

}
