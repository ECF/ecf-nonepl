package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
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
public class SkypeOpenAction implements IWorkbenchWindowActionDelegate {

	protected static IContainer container;

	private IWorkbenchWindow window;

	private static final String DEFAULT_CLIENT = "ecf.call.skype"; //$NON-NLS-1$

	private MultiRosterView mrv = null;

	protected static IContainer getContainer() {
		return container;
	}
	
	/**
	 * The constructor.
	 */
	public SkypeOpenAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public synchronized void run(IAction action) {
		IWorkbenchPage wp = window.getActivePage();

		try {
			
			if (mrv == null) {
				mrv = (MultiRosterView) wp.showView(MultiRosterView.VIEW_ID);
			    container.connect(null, null);
				mrv.addContainer(container);
				action.setEnabled(false);
			} else
				wp.showView(MultiRosterView.VIEW_ID);
			
			/*
			PresenceUI presenceUI = new PresenceUI(container,(IPresenceContainerAdapter)container.getAdapter(IPresenceContainerAdapter.class));
			presenceUI.showForUser(((SkypeContainer) container).getRosterManager().getRoster().getUser());
			container.connect(null, null);
			*/
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
		try {
			container = ContainerFactory.getDefault().createContainer(
					DEFAULT_CLIENT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}