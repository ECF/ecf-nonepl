/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.skype.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 *
 */
public class SkypeConnectWizardPage extends WizardPage {

	String username;
	
	static ImageDescriptor skypeIcon = AbstractUIPlugin
	.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
			"icons/SkypeIcons/icons/SkypeBlue_48x48.png");


	public SkypeConnectWizardPage(String username) {
		super("");
		this.username = username;
		setTitle("Skype Connection Wizard");
		setDescription("Connect to Skype Account '"+this.username+"'");
		setPageComplete(true);
		setImageDescriptor(skypeIcon);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Press Finish below to connect to Skype user account '"+username+"'");
		
		setControl(parent);
	}
}
