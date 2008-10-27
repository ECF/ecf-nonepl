package org.remotercp.preferences.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.remotercp.common.authorization.IOperationAuthorization;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.common.status.SerializableStatus;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.preferences.PreferencesActivator;
import org.remotercp.util.authorization.ExtensionRegistryHelper;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.preferences.PreferencesUtil;

public class RemotePreferencesServiceImpl implements IRemotePreferenceService {

	private IPreferencesService preferenceService;

	private final static Logger logger = Logger
			.getLogger(RemotePreferencesServiceImpl.class.getName());

	public SortedMap<String, String> getPreferences(String[] preferenceFilter)
			throws ECFException {
		SortedMap<String, String> preferencesMap = null;
		File preferencesFile = null;

		this.preferenceService = Platform.getPreferencesService();
		IEclipsePreferences rootNode = this.preferenceService.getRootNode();

		try {
			preferencesFile = File.createTempFile("preferences", ".ini");
			/*
			 * XXX: if boolean preference values are set to "false" or values
			 * are null they won't be exported. This could be a problem if the
			 * admin would like to change exactly these properties!
			 */
			OutputStream out = new FileOutputStream(preferencesFile);
			this.preferenceService.exportPreferences(rootNode,
					new IPreferenceFilter[] { getPreferenceFilter() }, out);

			preferencesMap = PreferencesUtil
					.createPreferencesFromFile(preferencesFile);

		} catch (FileNotFoundException e) {
			IStatus error = new Status(Status.ERROR,
					PreferencesActivator.PLUGIN_ID,
					"Could not store remote preferences in a file", e);
			throw new ECFException(error);
		} catch (IOException e) {
			IStatus error = new Status(Status.ERROR,
					PreferencesActivator.PLUGIN_ID,
					"Could not store remote preferences in a file", e);
			throw new ECFException(error);
		} catch (CoreException e) {
			IStatus error = new Status(Status.ERROR,
					PreferencesActivator.PLUGIN_ID,
					"Unable to export preferences ", e);
			throw new ECFException(error);
		}

		return preferencesMap;
	}

	private IPreferenceFilter getPreferenceFilter() {
		return new IPreferenceFilter() {

			@SuppressWarnings("unchecked")
			public Map getMapping(String scope) {
				return null;
			}

			/*
			 * InstanceScope == preferences stored in workspace
			 * ConfigurationScope == all workspaces share the same preferences
			 * 
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.core.runtime.preferences.IPreferenceFilter#getScopes
			 * ()
			 */
			public String[] getScopes() {
				return new String[] { InstanceScope.SCOPE,
						ConfigurationScope.SCOPE, DefaultScope.SCOPE };
			}

		};
	}

	/**
	 * This method sets local preferences for the given key-value pairs.
	 * 
	 * @param preferences
	 *            The key/value preference pairs
	 * @param fromId
	 *            The ID of the client who requests to change the preferences
	 *            remotely
	 * @return A status which describes the success of this method
	 */
	public IStatus setPreferences(Map<String, String> preferences, ID fromId)
			throws ECFException {

		// check if authorization has been provided
		try {
			List<Object> executablesForExtensionPoint = ExtensionRegistryHelper
					.getExecutablesForExtensionPoint("org.remotercp.authorization");

			for (Object executable : executablesForExtensionPoint) {
				if (executable instanceof IOperationAuthorization) {
					IOperationAuthorization operation = (IOperationAuthorization) executable;
					boolean canExecute = operation.canExecute(fromId,
							"setPreferences");
					if (canExecute) {
						return this.changePreferences(preferences, fromId);
					}
				}
			}

		} catch (NullPointerException e) {
			logger
					.log(Level.WARNING,
							"No extensions found for extension point org.remotercp.authorization");
			/*
			 * authorization extension has not been provided, ignore
			 * authorization and keep on going
			 */
			return this.changePreferences(preferences, fromId);
		} catch (CoreException e1) {
			logger.log(Level.SEVERE, "org.remotercp.authorization");
		}

		return new Status(Status.ERROR, PreferencesActivator.PLUGIN_ID,
				"Unable to store preference");
	}

	/**
	 * This method changes the existing preferences to the new provided values.
	 * 
	 * @param preferences
	 * @param fromId
	 * @return
	 * @throws ECFException
	 */
	private IStatus changePreferences(Map<String, String> preferences, ID fromId)
			throws ECFException {
		IEclipsePreferences rootNode = this.preferenceService.getRootNode();

		for (String key : preferences.keySet()) {
			try {
				Preferences node = rootNode.node(key);
				if (node != null) {

					/* XXX is this the right way to change preferences??? */
					String name = node.name();
					Preferences parent = node.parent();
					parent.sync();
					// remove old node
					node.removeNode();
					String value = preferences.get(key);
					// create new node
					parent.put(name, value);
					parent.flush();
				}
			} catch (BackingStoreException e) {
				IStatus error = new Status(Status.ERROR,
						PreferencesActivator.PLUGIN_ID,
						"Unable to store preference with the key: " + key, e);
				// throw remote exception
				throw new ECFException(error);
			}
		}

		IStatus okStatus = new SerializableStatus(Status.OK,
				PreferencesActivator.PLUGIN_ID,
				"Preferences have been successfully saved!");
		return okStatus;
	}

	public void startServices() {
		logger.info("********* Starting service: "
				+ RemotePreferencesServiceImpl.class.getName() + "********");

		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				PreferencesActivator.getBundleContext(), ISessionService.class);
		Assert.isNotNull(sessionService);

		sessionService.registerRemoteService(IRemotePreferenceService.class
				.getName(), new RemotePreferencesServiceImpl(), null);
	}
}
