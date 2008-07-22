package org.remotercp.common.servicelauncher;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class ServiceLauncher {
	private final static Logger logger = Logger.getLogger(ServiceLauncher.class
			.getName());

	public static void startRemoteServices() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("org.remotercp.remoteService");
		Assert.isNotNull(extensionPoint);
		
		IConfigurationElement[] configurationElements = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement element : configurationElements) {
			try {
				Object executableExtension = element
						.createExecutableExtension("class");
				Assert.isNotNull(executableExtension);

				if (executableExtension instanceof IRemoteServiceLauncher) {
					IRemoteServiceLauncher launcher = (IRemoteServiceLauncher) executableExtension;
					launcher.startService();
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
