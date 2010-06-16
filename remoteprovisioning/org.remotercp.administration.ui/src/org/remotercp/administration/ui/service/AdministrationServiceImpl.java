package org.remotercp.administration.ui.service;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.remotercp.administration.ui.dialog.AllowRemoteUpdateDialog;
import org.remotercp.provisioning.domain.service.IAdministrationService;

public class AdministrationServiceImpl implements IAdministrationService {

	private Boolean userAllowsRemoteAdministration = null;

	@Override
	public void restartApplication() {
		System.out.println("AdministrationServiceImpl.restartApplication()");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				PlatformUI.getWorkbench().restart();

			}
		});
	}

	@Override
	public boolean acceptUpdate(boolean forceAsking) {
		System.out.println("AdministrationServiceImpl.acceptUpdate()");

		if (userAllowsRemoteAdministration == null || forceAsking) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					AllowRemoteUpdateDialog restartDialog = new AllowRemoteUpdateDialog(
							Display.getCurrent().getActiveShell(), "Someone");
					// AcceptUpdateDialog restartDialog = new AcceptUpdateDialog();
					int dialogOpen = restartDialog.open();

					userAllowsRemoteAdministration = dialogOpen == Dialog.OK;
				}
			});

		}
		return userAllowsRemoteAdministration;
	}

}
