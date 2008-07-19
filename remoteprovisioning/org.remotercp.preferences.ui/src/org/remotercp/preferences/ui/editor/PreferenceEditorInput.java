package org.remotercp.preferences.ui.editor;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class PreferenceEditorInput implements IEditorInput {

	private final File preferences;

	public PreferenceEditorInput(File preferences) {
		this.preferences = preferences;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Preference Editor";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Edit Remote Preferences";
	}

	@Override
	public int hashCode() {
		return preferences.hashCode();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public File getPreferences() {
		return preferences;
	}

}
