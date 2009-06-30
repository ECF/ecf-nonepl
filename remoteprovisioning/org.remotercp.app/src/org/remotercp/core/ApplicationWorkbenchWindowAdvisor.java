package org.remotercp.core;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.login.ui.ChatLoginWizardDialog;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1000, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowMenuBar(true);

		// Show jobs in a progress dialog!!!
		configurer.setShowProgressIndicator(true);
		configurer.setTitle("Remote Eclipse RCP Management");

		/*
		 * open the user login dialog
		 */
		ChatLoginWizardDialog wizardDialog = new ChatLoginWizardDialog();
		if (wizardDialog.open() == Window.CANCEL) {
			// close application
			getWindowConfigurer().getWindow().getShell().close();
		} else {
			// start remote services
			ServiceLauncher.startRemoteServices();
		}
		
	}
}
