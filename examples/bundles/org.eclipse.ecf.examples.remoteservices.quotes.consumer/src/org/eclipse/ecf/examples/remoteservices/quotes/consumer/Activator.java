package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import org.eclipse.ecf.services.quotes.QuoteService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		bundleContext.addServiceListener(new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent event) {

				if (event.getType() == ServiceEvent.REGISTERED) {
					QuoteService service = (QuoteService) context
							.getService(event.getServiceReference());

					System.out.print("\n\n\nA new Quote Service: ");
					System.out.println(service.getServiceDescription());
					System.out.print("\t" + service.getRandomQuote() + "\n\n");

				}
			}
		}, "(objectclass=" + QuoteService.class.getName() + ")");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
