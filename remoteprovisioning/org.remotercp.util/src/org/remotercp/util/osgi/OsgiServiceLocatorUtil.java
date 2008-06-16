package org.remotercp.util.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiServiceLocatorUtil {

	public static <T> T getOSGiService(BundleContext context, Class<T> service)
			throws ClassCastException {

		ServiceTracker serviceTracker = new ServiceTracker(context, service
				.getName(), null);

		serviceTracker.open();
		T serviceObject = service.cast(serviceTracker.getService());
		serviceTracker.close();

		return serviceObject;
	}
}
