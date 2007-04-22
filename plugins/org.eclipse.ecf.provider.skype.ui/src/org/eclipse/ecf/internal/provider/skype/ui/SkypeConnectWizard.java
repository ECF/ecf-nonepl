package org.eclipse.ecf.internal.provider.skype.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.IExceptionHandler;
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
import org.eclipse.ecf.ui.dialogs.ContainerConnectErrorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class SkypeConnectWizard extends Wizard implements IConnectWizard {

	SkypeConnectWizardPage page;

	IWorkbench workbench = null;
	SkypeContainer container = null;

	public boolean canFinish() {
		return true;
	}

	public void addPages() {
		SkypeUserID userID = container.getSkypeUserID();
		page = new SkypeConnectWizardPage(userID.getName());
		addPage(page);
	}

	public void init(IWorkbench workbench, IContainer container) {
		this.workbench = workbench;
		this.container = (SkypeContainer) container;
	}

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

	private IChatMessageSender icms;
	private ITypingMessageSender itms;

	ID localID;

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
					view.openTab(icms, itms, localID, message.getFromID());
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
						view.openTab(icms, itms, localID, message.getFromID());
						view.showMessage(message);
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
		if (container.getConnectedID() != null) {
			try {
				workbench.getActiveWorkbenchWindow().getActivePage().showView(
						MultiRosterView.VIEW_ID);
			} catch (PartInitException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
					.getAdapter(IPresenceContainerAdapter.class);

			container.addListener(new IContainerListener() {
				public void handleEvent(final IContainerEvent event) {
					if (event instanceof IContainerConnectedEvent) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								localID = ((IContainerConnectedEvent) event)
										.getTargetID();
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

			ICallSessionContainerAdapter callAdapter = (ICallSessionContainerAdapter) container
					.getAdapter(ICallSessionContainerAdapter.class);
			callAdapter
					.addCallSessionRequestListener(createCallSessionRequestListener());

			new AsynchContainerConnectAction(container, null, null,
					new IExceptionHandler() {
						public IStatus handleException(final Throwable exception) {
							if (exception != null) {
								exception.printStackTrace();
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										new ContainerConnectErrorDialog(
												workbench
														.getActiveWorkbenchWindow()
														.getShell(),
												1,
												Messages.SkypeConnectWizard_EXCEPTION_SEE_DETAILS,
												Messages.SkypeConnectWizard_EXCEPTION_SKYPE_EXCEPTION,
												exception).open();
									}
								});
							}
							return Status.OK_STATUS;
						}

					}).run(null);

		}
		return true;
	}

	/**
	 * @return
	 */
	private ICallSessionRequestListener createCallSessionRequestListener() {
		return new ICallSessionRequestListener() {

			public void handleCallSessionRequest(ICallSessionRequestEvent event) {
				System.out.println("handleCallSessionRequest(" + event + ")");
			}
		};
	}

}
