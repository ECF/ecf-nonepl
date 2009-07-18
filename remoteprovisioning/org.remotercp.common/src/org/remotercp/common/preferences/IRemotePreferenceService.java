package org.remotercp.common.preferences;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;

public interface IRemotePreferenceService {

	public SortedMap<String, String> getPreferences(String[] preferenceFilter)
			throws ECFException;

	public List<IStatus> setPreferences(Map<String, String> preferences,
			ID fromId) throws ECFException;

}
