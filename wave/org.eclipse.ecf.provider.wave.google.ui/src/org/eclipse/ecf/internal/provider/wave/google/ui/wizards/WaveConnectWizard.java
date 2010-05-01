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

package org.eclipse.ecf.internal.provider.wave.google.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.provider.wave.google.ui.Activator;
import org.eclipse.ecf.internal.provider.wave.google.ui.Messages;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;

public class WaveConnectWizard extends Wizard implements IConnectWizard,
		INewWizard {

	private IWorkbench workbench;
	private IContainer container;
	private Shell shell;
	private WaveConnectWizardPage page;
	private ID targetID;
	private IConnectContext connectContext;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		IContainer container = null;
		try {
			container = ContainerFactory.getDefault().createContainer(
					"ecf.googlewave.client");
		} catch (final ContainerCreateException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"failed to create container", e));
		}

		init(workbench, container);
	}

	@Override
	public void init(IWorkbench workbench, IContainer container) {
		shell = workbench.getActiveWorkbenchWindow().getShell();

		this.workbench = workbench;
		this.container = container;

		setWindowTitle(Messages.WaveConnectWizardPage_WIZARD_PAGE_DESCRIPTION);
	}

	@Override
	public boolean performFinish() {
		final String email = page.getEmail();
		final String server = page.getServer();
		final String password = page.getPassword();

		try {
			targetID = container.getConnectNamespace().createInstance(
					new Object[] { email, server });
		} catch (final IDCreateException e) {
			new IDCreateErrorDialog(null, email, e).open();
			return false;
		}
		
		connectContext = ConnectContextFactory.createPasswordConnectContext(password);

		container.addListener(new IContainerListener() {
			public void handleEvent(IContainerEvent event) {
				if (event instanceof IContainerConnectedEvent) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							// XXX: Todo
							Status status = new Status(Status.OK, Activator.PLUGIN_ID, "connection established");
							StatusManager.getManager().addLoggedStatus(status);
						}
					});
				}
			}
		});

		new AsynchContainerConnectAction(container, targetID, connectContext).run();
		
		return true;
	}

	public void addPages() {
		page = new WaveConnectWizardPage();
		addPage(page);
	}

	public boolean performCancel() {
		if (container != null) {
			container.dispose();

			IContainerManager containerManager = Activator.getDefault()
					.getContainerManager();
			if (containerManager != null) {
				containerManager.removeContainer(container);
			}
		}

		return super.performCancel();
	}
}
