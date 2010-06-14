package org.remotercp.provisioning.service;

import java.util.List;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.provisioning.service.listener.IProvisioningAgentServiceListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class UpdateActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.provisioning";

	// The shared instance
	private static UpdateActivator plugin;

	private static BundleContext bundlecontext;

	private ProvisioningAgentServiceTracker provisioningAgentServiceTracker;

	/**
	 * The constructor
	 */
	public UpdateActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundlecontext = context;

		provisioningAgentServiceTracker = new ProvisioningAgentServiceTracker();
		provisioningAgentServiceTracker.open();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		provisioningAgentServiceTracker.close();
		plugin = null;
		super.stop(context);
	}

	public void registerServiceListener(
			IProvisioningAgentServiceListener listener) {
		provisioningAgentServiceTracker.addListener(listener);
	}

	private class ProvisioningAgentServiceTracker extends ServiceTracker {

		private List<IProvisioningAgentServiceListener> listener;
		private IProvisioningAgent provisioningAgent;

		public ProvisioningAgentServiceTracker() {
			super(bundlecontext, IProvisioningAgent.class.getName(), null);
		}

		protected void addListener(IProvisioningAgentServiceListener listener) {
			if (provisioningAgent != null) {
				listener.bindProvisioningAgent(provisioningAgent);
			}
			this.listener.add(listener);
		}

		@Override
		public Object addingService(ServiceReference reference) {
			provisioningAgent = (IProvisioningAgent) super
					.addingService(reference);

			informListener();

			return provisioningAgent;
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			provisioningAgent = null;
			informListener();
			super.removedService(reference, service);
		}

		private void informListener() {
			for (IProvisioningAgentServiceListener serviceListener : listener) {
				serviceListener.bindProvisioningAgent(provisioningAgent);
			}
		}

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UpdateActivator getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundlecontext;
	}

	public static ImageDescriptor getImageDescriptor(String imagePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imagePath);
	}
}
