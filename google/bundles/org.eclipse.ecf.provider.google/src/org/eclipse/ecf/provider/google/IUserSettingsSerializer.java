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

package org.eclipse.ecf.provider.google;

public interface IUserSettingsSerializer {
	// this is the interface that must be implemented by the UI project
	// which will handle user settings serialization

	public void setSetting(String setting, String value);

	public String getSetting(String setting);

}
