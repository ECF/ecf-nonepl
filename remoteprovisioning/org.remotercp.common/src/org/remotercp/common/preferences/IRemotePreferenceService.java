package org.remotercp.common.preferences;

import java.io.File;
import java.util.Map;

import org.eclipse.ecf.core.util.ECFException;

public interface IRemotePreferenceService {

	public File getPreferences(String[] preferenceFilter) throws ECFException;

	public void setPreferences(Map<String, String> preferences);

}
