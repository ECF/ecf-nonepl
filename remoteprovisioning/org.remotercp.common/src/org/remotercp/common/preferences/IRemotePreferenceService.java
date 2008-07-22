package org.remotercp.common.preferences;

import java.util.Map;
import java.util.SortedMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;

public interface IRemotePreferenceService extends IRemoteServiceLauncher {

	public SortedMap<String, String> getPreferences(String[] preferenceFilter)
			throws ECFException;

	public IStatus setPreferences(Map<String, String> preferences)
			throws ECFException;

}
