/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.google.ui;

import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.provider.google.IUserSettingsSerializer;
import org.eclipse.jface.dialogs.IDialogSettings;

public class GoogleUserSettingSerializer implements IUserSettingsSerializer {

	private static final String FILESAVELOC_SETTING = "FILESAVELOC_SETTING";

	public GoogleUserSettingSerializer(GoogleContainer container) {
		container.setUserSettingSerializer(this);
	}

	public String getSetting(String setting) {
		if (setting.equals(FILESAVELOC_SETTING)) {
			return getFileSaveLocation();
		}
		return null;
	}

	public void setSetting(String setting, String value) {
		final IDialogSettings pageSettings = getUserSettings();
		if (pageSettings != null)
			pageSettings.put(setting, value);

	}

	protected String getFileSaveLocation() {
		final IDialogSettings pageSettings = getUserSettings();
		if (pageSettings != null) {
			final String text = pageSettings.get(FILESAVELOC_SETTING);
			return text;
		}
		return System.getProperty("user.home");
	}

	public IDialogSettings getUserSettings() {
		IDialogSettings pageSettings = null;

		final IDialogSettings dialogSettings = Activator.getDefault().getDialogSettings();
		if (dialogSettings != null) {
			pageSettings = dialogSettings.getSection("GOOGLE_SETTINGS");
			if (pageSettings == null)
				pageSettings = dialogSettings.addNewSection("GOOGLE_SETTINGS");
			return pageSettings;
		}
		return null;

	}

}
