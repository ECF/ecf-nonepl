package org.eclipse.ecf.examples.tests.remoteservice.soap.host.hello;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	public static final String PLUGIN_ID = "org.eclipse.ecf.examples.tests.provider.soap.hello"; //$NON-NLS-1$

	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
//		try {
//			ServiceTracker containerFactoryServiceTracker = new ServiceTracker(
//					context, IContainerFactory.class.getName(), null);
//			containerFactoryServiceTracker.open();
//			IContainerFactory containerFactory = (IContainerFactory) containerFactoryServiceTracker
//					.getService();
//
//			IContainer container = containerFactory
//					.createContainer("org.eclipse.ecf.remoteservice.soap.host.hello");
//
//			HelloSoapContainer helloContainer = (HelloSoapContainer) container;
//
//			Assert.assertNotNull(helloContainer);
//
//			helloContainer.deployRemoteServiceAsWebService();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void stop(BundleContext context) throws Exception {
		context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

}
