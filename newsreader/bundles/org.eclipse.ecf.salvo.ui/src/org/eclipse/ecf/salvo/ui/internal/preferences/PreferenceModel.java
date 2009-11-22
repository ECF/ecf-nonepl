package org.eclipse.ecf.salvo.ui.internal.preferences;

import org.eclipse.ecf.salvo.ui.internal.Activator;

public class PreferenceModel {

	public static final String VIEW_PER_GROUP = "viewPerGroup";

	public final static PreferenceModel instance = new PreferenceModel();

	protected PreferenceModel() {
	}

	public void setViewPerGroup(boolean viewPerGroup) {
		System.out.println(viewPerGroup);
		Activator.getDefault().getPreferenceStore().setValue(VIEW_PER_GROUP,
				viewPerGroup);
	}

	public boolean getViewPerGroup() {
		return Activator.getDefault().getPreferenceStore().getBoolean(
				VIEW_PER_GROUP);
	}

}
