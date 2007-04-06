package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.call.CallException;
import org.eclipse.ecf.call.ICallContainerAdapter;
import org.eclipse.ecf.call.ICallSession;
import org.eclipse.ecf.call.ICallSessionListener;
import org.eclipse.ecf.call.events.ICallSessionEvent;
import org.eclipse.ecf.call.events.ICallSessionFailedEvent;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.internal.provider.skype.ui.Activator;
import org.eclipse.ecf.internal.provider.skype.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SkypeCallAction extends Action {

	static ImageDescriptor skypeIcon = AbstractUIPlugin
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					Messages.SkypeCallAction_Call_Image_Icon_Name);

	ID skypeReceiver = null;

	public SkypeCallAction(ID skypeReceiver, String text, String tooltip) {
		this.skypeReceiver = skypeReceiver;
		this.setText(text);
		this.setToolTipText(tooltip);
		this.setImageDescriptor(skypeIcon);
	}

	protected ICallContainerAdapter getCallContainerAdapter() {
		IContainer c = SkypeOpenAction.getContainer();
		if (c == null)
			return null;
		return (ICallContainerAdapter) c
				.getAdapter(ICallContainerAdapter.class);
	}

	protected ICallSessionListener getListener() {
		return new ICallSessionListener() {
			public void handleCallSessionEvent(final ICallSessionEvent event) {
				if (event instanceof ICallSessionFailedEvent) {
					Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									ICallSession callSession = event
											.getCallSession();
									MessageDialog
											.openInformation(
													null,
													Messages.SkypeOpenAction_Message_Title_Call_Failed,
													Messages.SkypeOpenAction_3
															+ callSession
																	.getReceiver()
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

	protected ID getReceiverFromInputDialog(ICallContainerAdapter adapter)
			throws IDCreateException {
		InputDialog id = new InputDialog(Display.getDefault().getActiveShell(),
				Messages.SkypeOpenAction_Initiate_Skype_Call_Title,
				Messages.SkypeOpenAction_Initiate_Skype_Call_Message, "", null); //$NON-NLS-3$ //$NON-NLS-1$
		id.setBlockOnOpen(true);
		int res = id.open();
		String receiver = null;
		if (res == InputDialog.OK)
			receiver = id.getValue();
		if (receiver == null || receiver.equals("")) //$NON-NLS-1$
			return null;
		else
			return IDFactory.getDefault().createID(
					adapter.getReceiverNamespace(), receiver);
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run() {
		try {
			ICallContainerAdapter adapter = getCallContainerAdapter();
			// If we haven't been given a skypeReceiver then show input dialog
			if (skypeReceiver == null)
				skypeReceiver = getReceiverFromInputDialog(adapter);
			// If the skypeReceiver now has a value...ring them up
			if (skypeReceiver != null)
				adapter.sendCallRequest(skypeReceiver, getListener(), null);
		} catch (IDCreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}