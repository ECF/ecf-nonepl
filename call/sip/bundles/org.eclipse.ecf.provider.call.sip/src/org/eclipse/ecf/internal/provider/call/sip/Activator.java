package org.eclipse.ecf.internal.provider.call.sip;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.call.sip"; //$NON-NLS-1$
	
	private static Activator plugin;

	private BundleContext context = null;

	
	
	private ServiceTracker simpleLogServiceTracker;
	private SimpleLogService simpleLogService;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin=this;
		this.context=context;
		// register the service
		context.registerService(
				SimpleLogService.class.getName(), 
				new SimpleLogServiceImpl(), 
				new Hashtable());
		
		// create a tracker and track the log service
		simpleLogServiceTracker = 
			new ServiceTracker(context, SimpleLogService.class.getName(), null);
		simpleLogServiceTracker.open();
		
		// grab the service
		simpleLogService = (SimpleLogService) simpleLogServiceTracker.getService();

		if(simpleLogService != null)
			simpleLogService.log("Yee ha, I'm logging!");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if(simpleLogService != null)
			simpleLogService.log("Yee ha, I'm logging!");
		
		// close the service tracker
		simpleLogServiceTracker.close();
		simpleLogServiceTracker = null;
		
		simpleLogService = null;
	}

}
