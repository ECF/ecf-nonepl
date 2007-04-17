package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
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
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.ContainerConnectErrorDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SkypeOpenAction implements IWorkbenchWindowActionDelegate {

	protected static IContainer container;

	private IWorkbenchWindow window;

	private static final String DEFAULT_CLIENT = "ecf.call.skype"; //$NON-NLS-1$

	protected static IContainer getContainer() {
		return container;
	}

	/**
	 * The constructor.
	 */
	public SkypeOpenAction() {
	}

	private void openView() {
		try {
			MultiRosterView view = (MultiRosterView) window.getActivePage()
					.showView(MultiRosterView.VIEW_ID);
			view.addContainer(container);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private IChatMessageSender icms;
	private ITypingMessageSender itms;

	private void displayMessage(IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) window.getActivePage()
						.findView(MessagesView.VIEW_ID);
				if (view != null) {
					IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view
							.getSite().getAdapter(
									IWorkbenchSiteProgressService.class);
					view.openTab(icms, itms, localID, message.getFromID());
					view.showMessage(message.getFromID(), message.getBody());
					service.warnOfContentChange();
				} else {
					try {

						IWorkbenchPage page = window.getActivePage();
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
						view
								.showMessage(message.getFromID(), message
										.getBody());
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
				MessagesView view = (MessagesView) window.getActivePage()
						.findView(MessagesView.VIEW_ID);
				if (view != null) {
					view.displayTypingNotification(e);
				}
			}
		});
	}

	private ID localID;

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public synchronized void run(final IAction action) {
		try {

			container = ContainerFactory.getDefault().createContainer(
					DEFAULT_CLIENT);

			final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
					.getAdapter(IPresenceContainerAdapter.class);
			
			container.addListener(new IContainerListener() {
				public void handleEvent(final IContainerEvent event) {
					if (event instanceof IContainerConnectedEvent) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								localID = ((IContainerConnectedEvent) event).getTargetID();
								openView();
								action.setEnabled(false);
							}
						});
					} else if (event instanceof IContainerDisconnectedEvent) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								action.setEnabled(true);
								if (container != null) {
									container.dispose();
									container = null;
								}
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

			new AsynchContainerConnectAction(container, null, null,
					new IExceptionHandler() {
						public IStatus handleException(final Throwable exception) {
							if (exception != null) {
								exception.printStackTrace();
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										new ContainerConnectErrorDialog(window.getShell(), 1, "See Details",
												"Skype", exception)
												.open();
									}
								});
							}
							return Status.OK_STATUS;
						}

					}).run(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		if (container != null) {
			container.dispose();
			container = null;
		}
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}