/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.jms.activemq.ui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.example.collab.actions.URIClientConnectAction;
import org.eclipse.ecf.internal.provider.jms.activemq.ui.Activator;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class JoinGroupWizard extends Wizard implements IConnectWizard,
		INewWizard {

	private static final String DIALOG_SETTINGS = JoinGroupWizard.class
			.getName();

	JoinGroupWizardPage mainPage;
	private IResource resource;

	protected IContainer container;

	public JoinGroupWizard() {
	}

	public JoinGroupWizard(IResource resource, IWorkbench workbench) {
		super();
		this.resource = resource;
		setWindowTitle("ActiveMQ Connect");
		final IDialogSettings dialogSettings = Activator.getDefault()
				.getDialogSettings();
		IDialogSettings wizardSettings = dialogSettings
				.getSection(DIALOG_SETTINGS);
		if (wizardSettings == null)
			wizardSettings = dialogSettings.addNewSection(DIALOG_SETTINGS);

		setDialogSettings(wizardSettings);
	}

	protected ISchedulingRule getSchedulingRule() {
		return resource;
	}

	public void addPages() {
		super.addPages();
		mainPage = new JoinGroupWizardPage();
		addPage(mainPage);
	}

	public boolean performFinish() {
		try {
			finishPage(new NullProgressMonitor());
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void finishPage(final IProgressMonitor monitor)
			throws InterruptedException, CoreException {

		mainPage.saveDialogSettings();
		URIClientConnectAction client = null;
		final String groupName = mainPage.getJoinGroupText();
		final String nickName = mainPage.getNicknameText();
		final String containerType = ActiveMQ.CLIENT_CONTAINER_NAME;
		final boolean autoLogin = mainPage.getAutoLoginFlag();
		try {
			client = new URIClientConnectAction(containerType, groupName,
					nickName, "", resource, autoLogin);
			client.run(null);
		} catch (final Exception e) {
			final String id = Activator.getDefault().getBundle()
					.getSymbolicName();
			throw new CoreException(new Status(Status.ERROR, id, IStatus.ERROR,
					"Could not connect to " + groupName, e));
		}
	}

	public void init(IWorkbench workbench, IContainer container) {
		this.container = container;

	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.container = null;
		try {
			this.container = ContainerFactory.getDefault().createContainer(
					ActiveMQ.CLIENT_CONTAINER_NAME);
		} catch (final ContainerCreateException e) {
			// None
		}

		setWindowTitle(JoinGroupWizard.DIALOG_SETTINGS);

	}
}
