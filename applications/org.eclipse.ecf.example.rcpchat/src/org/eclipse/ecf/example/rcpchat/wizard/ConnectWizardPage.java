/*******************************************************************************
 * Copyright (c) 2005 Ed Burnette, Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ed Burnette, Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.rcpchat.wizard;

import org.eclipse.ecf.core.ContainerDescription;
import org.eclipse.ecf.example.rcpchat.RcpChatPlugin;
import org.eclipse.ecf.ui.wizards.JoinGroupWizardPage;

public class ConnectWizardPage extends JoinGroupWizardPage {
	public ConnectWizardPage(ContainerDescription[] descriptions) {
		super(descriptions);
        setTitle(RcpChatPlugin.CONNECT_WIZARD_PAGE_TITLE);
        setDescription(RcpChatPlugin.CONNECT_WIZARD_PAGE_DESCRIPTION);
	}
	public ConnectWizardPage() {
		super();
	}
}
