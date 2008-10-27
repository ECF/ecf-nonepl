package org.remotercp.util.authorization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * This helper class is responsible for processes regarding the extension
 * registry.
 * 
 * @author Eugen Reiswich
 * @date 27.10.2008
 * 
 */
public class ExtensionRegistryHelper {

	/**
	 * Returns the executable objects for a given extension point if any exist.
	 * 
	 * @param extensionPointId
	 *            The ID of the extension point
	 * @return List with executable objects
	 * @throws NullPointerException
	 *             , CoreException
	 */
	public static List<Object> getExecutablesForExtensionPoint(
			String extensionPointId) throws CoreException, NullPointerException {

		List<Object> executables = new ArrayList<Object>();

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(extensionPointId);
		if (extensionPoint == null) {
			throw new NullPointerException("No extension point found for id: "
					+ extensionPointId);
		} else {
			IConfigurationElement[] configurationElements = extensionPoint
					.getConfigurationElements();
			// are extensions available for the given extension point?
			if (configurationElements != null
					&& configurationElements.length > 0) {
				for (IConfigurationElement configurationElement : configurationElements) {
					Object ecutableExtension = configurationElement
							.createExecutableExtension("class");
					executables.add(ecutableExtension);
				}
			}
		}

		return executables;
	}

}
