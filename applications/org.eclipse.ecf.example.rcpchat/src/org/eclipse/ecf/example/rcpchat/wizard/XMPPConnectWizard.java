/****************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.example.rcpchat.wizard;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageEvent;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class XMPPConnectWizard extends Wizard implements IConnectWizard {

	XMPPConnectWizardPage page;

	private IContainer container;

	private ID targetID;

	private IConnectContext connectContext;


	public void addPages() {
		page = new XMPPConnectWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IContainer container) {
		this.container = container;
		this.workbench = workbench;
	}

	private IWorkbench workbench;
	private IChatMessageSender icms;
	private ITypingMessageSender itms;

	private void openView() {
		try {
			MultiRosterView view = (MultiRosterView) workbench
					.getActiveWorkbenchWindow().getActivePage().showView(
							MultiRosterView.VIEW_ID);
			view.addContainer(container);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void displayMessage(IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) workbench
						.getActiveWorkbenchWindow().getActivePage().findView(
								MessagesView.VIEW_ID);
				if (view != null) {
					IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view
							.getSite().getAdapter(
									IWorkbenchSiteProgressService.class);
					view.openTab(icms, itms, targetID, message.getFromID());
					view.showMessage(message);
					service.warnOfContentChange();
				} else {
					try {

						IWorkbenchPage page = workbench
								.getActiveWorkbenchWindow().getActivePage();
						view = (MessagesView) page.showView(
								MessagesView.VIEW_ID, null,
								IWorkbenchPage.VIEW_CREATE);
						if (!page.isPartVisible(view)) {
							IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view
									.getSite()
									.getAdapter(
											IWorkbenchSiteProgressService.class);
							service.warnOfContentChange();
						}
						view.openTab(icms, itms, targetID, message.getFromID());
						view
								.showMessage(message);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void displayTypingNotification(final ITypingMessageEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) workbench
						.getActiveWorkbenchWindow().getActivePage().findView(
								MessagesView.VIEW_ID);
				if (view != null) {
					view.displayTypingNotification(e);
				}
			}
		});
	}

	public boolean performFinish() {
		connectContext = ConnectContextFactory
				.createPasswordConnectContext(page.getPassword());

		try {
			targetID = IDFactory.getDefault().createID(
					container.getConnectNamespace(), page.getConnectID());
		} catch (IDCreateException e) {
			// TODO: This needs to be handled properly
			e.printStackTrace();
			return false;
		}

		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);
		container.addListener(new IContainerListener() {
			public void handleEvent(IContainerEvent event) {
				if (event instanceof IContainerConnectedEvent) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							openView();
						}
					});
				}
			}
		});

		IChatManager icm = adapter.getChatManager();
		icms = icm.getChatMessageSender();
		itms = icm.getTypingMessageSender();

		icm.addMessageListener(new IIMMessageListener() {
			public void handleMessageEvent(IIMMessageEvent e) {
				if (e instanceof IChatMessageEvent) {
					displayMessage((IChatMessageEvent) e);
				} else if (e instanceof ITypingMessageEvent) {
					displayTypingNotification((ITypingMessageEvent) e);
				}
			}
		});

		new AsynchContainerConnectAction(container, targetID, connectContext).run();

		return true;
	}

}
