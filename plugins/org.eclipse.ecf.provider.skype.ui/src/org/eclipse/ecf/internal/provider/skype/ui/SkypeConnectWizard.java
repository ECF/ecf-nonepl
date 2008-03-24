package org.eclipse.ecf.internal.provider.skype.ui;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
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
import org.eclipse.ecf.provider.skype.SkypeContainer;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class SkypeConnectWizard extends Wizard implements IConnectWizard, INewWizard {

	SkypeConnectWizardPage page;

	IWorkbench workbench = null;
	SkypeContainer container = null;

	public boolean canFinish() {
		return true;
	}

	public void addPages() {
		final SkypeUserID userID = container.getSkypeUserID();
		page = new SkypeConnectWizardPage(userID.getName());
		addPage(page);
	}

	public void init(IWorkbench workbench, IContainer container) {
		this.workbench = workbench;
		this.container = (SkypeContainer) container;
		setWindowTitle(Messages.SkypeConnectWizard_NEW_CONNECTION_TITLE);
	}

	private void openView() {
		try {
			final MultiRosterView view = (MultiRosterView) workbench.getActiveWorkbenchWindow().getActivePage().showView(MultiRosterView.VIEW_ID);
			view.addContainer(container);
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	private IChatMessageSender icms;
	private ITypingMessageSender itms;

	ID localID;

	private void displayMessage(IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MessagesView.VIEW_ID);
				if (view != null) {
					final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
					view.openTab(icms, itms, localID, message.getFromID());
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
						view.openTab(icms, itms, localID, message.getFromID());
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
				if (view != null) {
					view.displayTypingNotification(e);
				}
			}
		});
	}

	public boolean performFinish() {
		if (container.getConnectedID() != null) {
			try {
				workbench.getActiveWorkbenchWindow().getActivePage().showView(MultiRosterView.VIEW_ID);
			} catch (final PartInitException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);

			container.addListener(new IContainerListener() {
				public void handleEvent(final IContainerEvent event) {
					if (event instanceof IContainerConnectedEvent) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								localID = ((IContainerConnectedEvent) event).getTargetID();
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

			final ICallSessionContainerAdapter callAdapter = (ICallSessionContainerAdapter) container.getAdapter(ICallSessionContainerAdapter.class);
			callAdapter.addCallSessionRequestListener(createCallSessionRequestListener());

			new AsynchContainerConnectAction(container, null, null).run(null);

		}
		return true;
	}

	/**
	 * @return
	 */
	private ICallSessionRequestListener createCallSessionRequestListener() {
		return new ICallSessionRequestListener() {

			public void handleCallSessionRequest(ICallSessionRequestEvent event) {
				System.out.println("handleCallSessionRequest(" + event + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// nothing to do
		this.workbench = workbench;

		try {
			this.container = (SkypeContainer) ContainerFactory.getDefault().createContainer("ecf.call.skype"); //$NON-NLS-1$
		} catch (final ContainerCreateException e) {
			// None
		}

		setWindowTitle(Messages.SkypeConnectWizard_NEW_CONNECTION_TITLE);
	}

}
