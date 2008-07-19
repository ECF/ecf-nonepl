package org.remotercp.common.preferences;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public interface IRemotePreferenceService {

	public File getPreferences(String[] preferenceFilter) throws ECFException;

	public IStatus setPreferences(Map<String, String> preferences)
			throws ECFException;

}
