package org.remotercp.preferences.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.core.util.ECFException;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.preferences.PreferencesActivator;

public class RemotePreferencesServiceImpl implements IRemotePreferenceService {

	public File getPreferences(String[] preferenceFilter) throws ECFException {
		File preferences = null;

		IPreferencesService preferenceService = Platform
				.getPreferencesService();
		IEclipsePreferences rootNode = preferenceService.getRootNode();

		try {
			preferences = File.createTempFile("preferences", ".ini");
			/*
			 * XXX: if boolean preference values are set to "false" they won't
			 * be exported. This could be a problem if the admin would like to
			 * change exactly these property!
			 */
			OutputStream out = new FileOutputStream(preferences);
			preferenceService
					.exportPreferences(rootNode, out, preferenceFilter);
			// preferenceService.exportPreferences(rootNode,
			// new IPreferenceFilter[] { getPreferenceFilter() }, out);

			// FileReader reader = new FileReader(tempFile);
			// BufferedReader bufReader = new BufferedReader(reader);
			// String line;
			// int count = 0;
			//
			// // first line is the date of the export, ignore it
			// line = bufReader.readLine();
			// count++;
			// while ((line = bufReader.readLine()) != null) {
			//
			// /* split keys and values */
			// int keyValueSeparator = line.indexOf("=");
			// String key = line.substring(0, keyValueSeparator);
			// String value = line.substring(keyValueSeparator + 1);
			//
			// /*
			// * store preferences in map. value could not be set yet but key
			// * must exist!
			// */
			// if (key != null)
			// preferences.put(key, value);
			//
			// // System.out.println("Key: " + key + " value: " + value);
			// count++;
			// }
			// bufReader.close();
			// reader.close();

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

		return preferences;
	}

	// private IPreferenceFilter getPreferenceFilter() {
	// return new IPreferenceFilter() {
	//
	// public Map getMapping(String scope) {
	// return null;
	// }
	//
	// /*
	// * InstanceScope == preferences stored in workspace
	// * ConfigurationScope == all workspaces share the same preferences
	// *
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.core.runtime.preferences.IPreferenceFilter#getScopes()
	// */
	// public String[] getScopes() {
	// return new String[] { InstanceScope.SCOPE,
	// ConfigurationScope.SCOPE };
	// }
	//
	// };
	// }

	// protected void getPreferences(Preferences preferences,
	// Map<String, String> preferencesMap) {
	// try {
	// String[] childrenNames = preferences.childrenNames();
	//
	// if (childrenNames != null && childrenNames.length > 0) {
	// /* look recursive for all tree elements */
	// for (String child : childrenNames) {
	// Preferences node = preferences.node(child);
	// getPreferences(node, preferencesMap);
	// }
	// }else{
	// String[] keys = preferences.keys();
	// for(String key : keys){
	// preferences.get
	// }
	// }
	// } catch (BackingStoreException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public void setPreferences(Map<String, String> preferences) {
		// TODO Auto-generated method stub

	}
}
