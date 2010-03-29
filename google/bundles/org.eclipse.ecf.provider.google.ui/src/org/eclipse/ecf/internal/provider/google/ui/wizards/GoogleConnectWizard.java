/****************************************************************************
 * Copyright (c) 2007, 2009 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.google.ui.wizards;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.provider.google.ui.*;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.im.*;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class GoogleConnectWizard extends Wizard implements IConnectWizard, INewWizard {

	protected GoogleConnectWizardPage page;

	// private IContainer container;
	protected IContainer container;

	private ID targetID;

	private IConnectContext connectContext;

	protected String usernameAtHost;

	public GoogleConnectWizard() {
		super();
	}

	public GoogleConnectWizard(String usernameAtHost) {
		this();
		this.usernameAtHost = usernameAtHost;
	}

	/*
	 * protected IIncomingFileTransferRequestListener requestListener = new
	 * IIncomingFileTransferRequestListener() { public void
	 * handleFileTransferRequest( final IFileTransferRequestEvent event) {
	 * Display.getDefault().asyncExec(new Runnable() { public void run() { final
	 * String username = event.getRequesterID().getName(); final
	 * IFileTransferInfo transferInfo = event .getFileTransferInfo(); final
	 * String fileName = transferInfo.getFile().getName(); final Object[]
	 * bindings = new Object[] { username, fileName,
	 * ((transferInfo.getFileSize() == -1) ? "unknown" //$NON-NLS-1$ :
	 * (transferInfo.getFileSize() + " bytes")), //$NON-NLS-1$
	 * (transferInfo.getDescription() == null) ? "none" //$NON-NLS-1$ :
	 * transferInfo.getDescription() }; if (MessageDialog.openQuestion(shell,
	 * NLS.bind( Messages.XMPPConnectWizard_FILE_RECEIVE_TITLE, username),
	 * NLS.bind( Messages.XMPPConnectWizard_FILE_RECEIVE_MESSAGE, bindings))) {
	 * final FileDialog fd = new FileDialog(shell, SWT.OPEN); // XXX this should
	 * be some default path gotten from // preference. For now we'll have it be
	 * the user.home // system property
	 * fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
	 * fd.setFileName(fileName); final int suffixLoc =
	 * fileName.lastIndexOf('.'); if (suffixLoc != -1) { final String ext =
	 * fileName.substring(fileName .lastIndexOf('.'));
	 * fd.setFilterExtensions(new String[] { ext }); } fd.setText(NLS.bind(
	 * Messages.XMPPConnectWizard_FILE_SAVE_TITLE, username)); final String res
	 * = fd.open(); if (res == null) event.reject(); else { try { final
	 * FileOutputStream fos = new FileOutputStream( new File(res));
	 * event.accept(fos, new IFileTransferListener() { public void
	 * handleTransferEvent( IFileTransferEvent event) { // XXX Should have some
	 * some UI // for transfer events if (event instanceof
	 * IIncomingFileTransferReceiveDoneEvent) { try { fos.close(); } catch
	 * (final IOException e) { } } } }); } catch (final Exception e) {
	 * MessageDialog .openError( shell,
	 * Messages.XMPPConnectWizard_RECEIVE_ERROR_TITLE, NLS .bind(
	 * Messages.XMPPConnectWizard_RECEIVE_ERROR_MESSAGE, new Object[] {
	 * fileName, username, e .getLocalizedMessage() })); } } } else
	 * event.reject(); } }); }
	 * 
	 * };
	 */

	public void addPages() {
		page = new GoogleConnectWizardPage(usernameAtHost);
		addPage(page);
	}

	public void init(IWorkbench workbench, IContainer container) {
		workbench.getActiveWorkbenchWindow().getShell();
		this.container = container;
		this.workbench = workbench;

		setWindowTitle(Messages.GoogleConnectWizard_WIZARD_TITLE);

	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		workbench.getActiveWorkbenchWindow().getShell();
		this.workbench = workbench;
		this.container = null;
		try {
			this.container = ContainerFactory.getDefault().createContainer("ecf.google.smack");
		} catch (final ContainerCreateException e) {
			// None
		}

		setWindowTitle(Messages.GoogleConnectWizard_WIZARD_TITLE);
	}

	private IWorkbench workbench;
	private IChatMessageSender icms;
	private ITypingMessageSender itms;

	private void openView() {
		try {
			final MultiRosterView view = (MultiRosterView) workbench.getActiveWorkbenchWindow().getActivePage().showView(MultiRosterView.VIEW_ID);
			view.addContainer(container);

			((GoogleContainer) container).getNotificationManager().addNotificationListener(new GoogleNotificationUIDelegator((GoogleContainer) container));

		} catch (final PartInitException e) {
			e.printStackTrace();
		}

	}

	private void displayMessage(IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MessagesView.VIEW_ID);
				if (view != null) {
					final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
					view.openTab(icms, itms, targetID, message.getFromID());
					view.showMessage(message);
					service.warnOfContentChange();
				} else {
					try {

						final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
						view = (MessagesView) page.showView(MessagesView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
						if (!page.isPartVisible(view)) {
							final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
							service.warnOfContentChange();
						}
						view.openTab(icms, itms, targetID, message.getFromID());
						view.showMessage(message);
					} catch (final PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void displayTypingNotification(final ITypingMessageEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final MessagesView view = (MessagesView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MessagesView.VIEW_ID);
				if (view != null)
					view.displayTypingNotification(e);
			}
		});
	}

	public boolean performCancel() {
		if (container != null) {
			container.dispose();

			IContainerManager containerManager = Activator.getDefault().getContainerManager();
			if (containerManager != null) {
				containerManager.removeContainer(container);
			}
		}

		return super.performCancel();
	}

	public boolean performFinish() {

		final String connectID = page.getConnectID();
		final String password = page.getPassword();

		// Save combo text even if we don't successfully login
		page.saveComboText();

		connectContext = ConnectContextFactory.createPasswordConnectContext(password);

		try {
			targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), connectID);
		} catch (final IDCreateException e) {
			new IDCreateErrorDialog(null, connectID, e).open();
			return false;
		}

		// Save combo items if targetID created successfully
		page.saveComboItems();

		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);

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

		final IChatManager icm = adapter.getChatManager();
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

		/*
		 * final ISendFileTransferContainerAdapter ioftca =
		 * (ISendFileTransferContainerAdapter) container
		 * .getAdapter(ISendFileTransferContainerAdapter.class);
		 * ioftca.addListener(requestListener);
		 */
		// Connect
		new AsynchContainerConnectAction(container, targetID, connectContext, null, new Runnable() {
			public void run() {
				cachePassword(connectID, password);
			}
		}).run();

		return true;
	}

	protected void cachePassword(final String connectID, String password) {
		if (password != null && !password.equals("")) {
			final PasswordCacheHelper pwStorage = new PasswordCacheHelper(connectID);
			pwStorage.savePassword(password);
		}
	}

}
