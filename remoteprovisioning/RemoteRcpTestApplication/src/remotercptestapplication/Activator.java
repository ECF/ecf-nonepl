package remotercptestapplication;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.StringID.StringIDNamespace;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.provisioning.domain.exception.RemoteOperationException;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;
import org.remotercp.provisioning.domain.version.IVersionedId;
import org.remotercp.provisioning.domain.version.VersionedId;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IInstallServiceListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "ScottTestApplication"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static String repositorylocation = "file:/C:/UpdateSite";

	private static final String FEATURE_TO_INSTALL = "TestFeature.feature.group";

	private URI[] locations;
	
	private IInstallFeaturesService installService;

	private ID adminId = new StringIDNamespace("Test", "RemoteRcp").createInstance(new String[] {"Mein Test"});
	
	private InstallServiceTracker tracker;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		locations = new URI[] {new URI(repositorylocation) };
		System.out.println("Activator.start()");
		tracker = new InstallServiceTracker(context);
		tracker.addInstallServiceListener(this);
		tracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		tracker.close();
		tracker = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public IStatus install() {
		System.out.println("Activator.install()");
		IStatus result = new Status(IStatus.ERROR, PLUGIN_ID,
				"Couldn't install new Feature");
		IVersionedId id = new VersionedId(FEATURE_TO_INSTALL, "2.0.0");
		result = install(id);
		return result;
	}

	private IStatus install(IVersionedId id) {
		System.out.println("Activator.install(" + id.getId() + ")");
		IStatus installStatus = Status.CANCEL_STATUS;
		if (installService != null) {
			installStatus = installService.installFeature(id, locations, adminId);
			System.out.println("	Installation "+installStatus);
		} else {
			System.out.println("No InstallService available");
		}
		return installStatus;
	}

	public IStatus update() {
		IStatus result = new Status(IStatus.ERROR, PLUGIN_ID,
			"Couldn't update the Feature");
		IVersionedId[] ids = new IVersionedId[] {new VersionedId(FEATURE_TO_INSTALL)};
		result = installService.updateFeature(ids, locations, adminId);
		
		return result;
	}

	public IStatus uninstall() {
		IStatus result = new Status(IStatus.ERROR, PLUGIN_ID,
			"Couldn't uninstall the Feature");
		try {
			IVersionedId[] installedFeatures = installService.getInstalledFeatures(adminId);
			for (IVersionedId id : installedFeatures) {
				if (id.getId().equals(FEATURE_TO_INSTALL)) {
					result = installService.uninstallFeature(id, adminId);
					break;
				}
			}
		} catch (RemoteOperationException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void bindInstallService(IInstallFeaturesService service) {
		System.out.println("Activator.bindInstallService()");
		installService = service;
	}

	public void unbindInstallService() {
		System.out.println("Activator.unbindInstallService()");
		installService = null;
	}
}
