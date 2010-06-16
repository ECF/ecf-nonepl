package org.remotercp.administration.ui.service;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.remotercp.administration.ui.dialog.AcceptUpdateDialog;
import org.remotercp.provisioning.domain.service.IAdministrationService;

public class AdministrationServiceImpl implements IAdministrationService {

	@Override
	public void restartApplication() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				PlatformUI.getWorkbench().restart();

			}
		});

	}

	@Override
	public boolean acceptUpdate() {
		final boolean[] result = new boolean[1];

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				AcceptUpdateDialog restartDialog = new AcceptUpdateDialog();
				int dialogOpen = restartDialog.open();

				result[0] = dialogOpen == SWT.OK;

			}
		});

		return result[0];
	}

}
