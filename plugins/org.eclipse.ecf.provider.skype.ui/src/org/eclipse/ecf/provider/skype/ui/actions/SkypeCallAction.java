package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.call.CallException;
import org.eclipse.ecf.call.ICallContainerAdapter;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.events.ICallSessionEvent;
import org.eclipse.ecf.call.events.ICallSessionFailedEvent;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.internal.provider.skype.ui.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SkypeCallAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	protected static IContainer getContainer() {
		return SkypeOpenAction.getContainer();
	}
	
	protected ICallContainerAdapter getCallContainerAdapter() {
		IContainer c = getContainer();
		if (c == null) return null;
		return (ICallContainerAdapter) c
				.getAdapter(ICallContainerAdapter.class);
	}

	protected ICallSessionListener getListener() {
		return new ICallSessionListener() {
			public void handleCallSessionEvent(final ICallSessionEvent event) {
				System.out.println("handleCallSessionEvent(" + event + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				if (event instanceof ICallSessionFailedEvent) {
					window.getShell().getDisplay().syncExec(new Runnable() {
						public void run() {
							ICallSession callSession = event.getCallSession();
							MessageDialog
									.openInformation(
											window.getShell(),
											Messages.SkypeOpenAction_Message_Title_Call_Failed,
											Messages.SkypeOpenAction_3
													+ callSession.getReceiver()
															.getName()
													+ Messages.SkypeOpenAction_4
													+ callSession
															.getFailureReason()
															.getReason());
						}
					});
				}
			}
		};
	}

	/**
	 * The constructor.
	 */
	public SkypeCallAction() {
	}

	protected void call(String receiver) throws IDCreateException,
			CallException {
		ICallContainerAdapter adapter = getCallContainerAdapter();
		adapter.sendCallRequest(IDFactory.getDefault().createID(
				adapter.getReceiverNamespace(), receiver), getListener(), null);
	}

	protected String getReceiverFromInputDialog() {
		InputDialog id = new InputDialog(window.getShell(),
				Messages.SkypeOpenAction_Initiate_Skype_Call_Title,
				Messages.SkypeOpenAction_Initiate_Skype_Call_Message, "", null); //$NON-NLS-3$
		id.setBlockOnOpen(true);
		int res = id.open();
		String receiver = null;
		if (res == InputDialog.OK)
			receiver = id.getValue();
		return receiver;
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public synchronized void run(IAction action) {
		try {
			call(getReceiverFromInputDialog());
		} catch (IDCreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CallException e) {
			// TODO Auto-generated catch block
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
		action.setEnabled(false);
		System.out.println("SkypeCallAction.selectionChanged("+action+","+selection+")");
		action.setEnabled(getContainer() != null);
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
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