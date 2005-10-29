/*******************************************************************************
 * Copyright (c) 2005 Ed Burnette, Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ed Burnette, Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.rcpchat.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerDescription;
import org.eclipse.ecf.example.rcpchat.RcpChatPlugin;
import org.eclipse.ecf.example.rcpchat.actions.URIClientConnectAction;
import org.eclipse.ecf.ui.wizards.JoinGroupWizard;
import org.eclipse.ui.IWorkbench;

public class ConnectWizard extends JoinGroupWizard {
	public ConnectWizard(IWorkbench wb, String title, ContainerDescription [] descriptions) {
		super(wb,title,descriptions);
	}
	
    public void addPages() {
        mainPage = new ConnectWizardPage(descriptions);
        addPage(mainPage);
    }

    protected void finishPage(final IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		mainPage.saveDialogSettings();
		URIClientConnectAction client = null; 
		String groupName = mainPage.getJoinGroupText(); 
		String nickName = mainPage.getNicknameText(); 
		String containerType = mainPage.getContainerType(); 
		String password = mainPage.getPasswordText();
		try {
			client = new URIClientConnectAction(containerType, groupName,
					nickName, password);
			client.run(null);
		} catch (Exception e) {
			String id = RcpChatPlugin.PLUGIN_ID;
			throw new CoreException(new Status(Status.ERROR, id, 100,
					"Could not connect to " + groupName, e));
		}
	}

}
