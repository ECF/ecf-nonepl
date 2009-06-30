package org.remotercp.common.servicelauncher;

import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * This class is used to create executables for the extension point
 * "org.remotercp.remoteService".
 * 
 * @author eugrei
 * 
 */
public class ServiceLauncher {
	private final static Logger logger = Logger.getLogger(ServiceLauncher.class
			.getName());

	public static void startRemoteServices() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("org.remotercp.remoteService");

		IConfigurationElement[] configurationElements = extensionPoint
				.getConfigurationElements();

		if (configurationElements == null || configurationElements.length == 0) {
			logger.severe("No remote services started. "
					+ "ExtensionPont has not registered extensions");
		}

		for (IConfigurationElement element : configurationElements) {
			try {
				Object executableExtension = element
						.createExecutableExtension("class");
				assert executableExtension != null : "executableExtension != null";

				if (executableExtension instanceof IRemoteServiceLauncher) {
					IRemoteServiceLauncher launcher = (IRemoteServiceLauncher) executableExtension;
					launcher.startServices();
				}
			} catch (CoreException e) {
				logger
						.severe("Unable to create executable Extension for element: "
								+ element.toString());
				e.printStackTrace();
			}
		}
	}

}
