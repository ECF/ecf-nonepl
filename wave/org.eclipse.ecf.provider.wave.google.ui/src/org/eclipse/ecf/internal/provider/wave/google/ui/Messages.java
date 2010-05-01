/****************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sebastian Schmidt <mail@schmidt-seb.de> - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.wave.google.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.wave.google.ui.messages"; //$NON-NLS-1$

	public static String WaveConnectWizardPage_EmailAddressLabel;
	public static String WaveConnectWizardPage_PasswordLabel;
	public static String WaveConnectWizardPage_EmailAddressRequired;
	public static String WaveConnectWizardPage_EmailAddressInvalid;
	public static String WaveConnectWizardPage_PasswordRequired;
	public static String WaveConnectWizardPage_Title;
	public static String WaveConnectWizardPage_WIZARD_PAGE_DESCRIPTION;
	public static String WaveConnectWizardPage_ServerLabel;
	public static String WaveConnectWizardPage_ServerRequired;
	public static String WaveConnectWizardPage_ServerInvalid;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// private null constructor
	}
}
