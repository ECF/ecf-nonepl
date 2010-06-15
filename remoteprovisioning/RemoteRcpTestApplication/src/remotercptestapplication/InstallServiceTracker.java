package remotercptestapplication;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;

public class InstallServiceTracker extends ServiceTracker {

	private IInstallFeaturesService _service;
	private List<IInstallServiceListener> _listener;

	public InstallServiceTracker(BundleContext context) {
		super(context, IInstallFeaturesService.class.getName(), null);
		_listener = new ArrayList<IInstallServiceListener>();
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		_service = (IInstallFeaturesService) super.addingService(reference);
		
		informBindToListener();
		return _service;
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		_service = null;
		
		informUnbindToListener();
		super.removedService(reference, service);
	}
	
	public void addInstallServiceListener(IInstallServiceListener listener) {
		_listener.add(listener);
		
		if (_service != null) {
			listener.bindInstallService(_service);
		}
	}
	
	private void informBindToListener() {
		for (IInstallServiceListener listener : _listener) {
			listener.bindInstallService(_service);
		}
	}
	
	private void informUnbindToListener() {
		for (IInstallServiceListener listener : _listener) {
			listener.unbindInstallService();
		}
	}

}
