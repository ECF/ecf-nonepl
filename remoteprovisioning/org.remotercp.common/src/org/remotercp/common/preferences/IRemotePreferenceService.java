package org.remotercp.common.preferences;

import java.util.Map;

import org.eclipse.ecf.core.util.ECFException;

public interface IRemotePreferenceService {

	public Map<String, String> getPreferences(String[] preferenceFilter) throws ECFException;

	public void setPreferences(Map<String, String> preferences);

}
