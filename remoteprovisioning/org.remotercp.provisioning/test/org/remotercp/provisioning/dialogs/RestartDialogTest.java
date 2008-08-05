package org.remotercp.provisioning.dialogs;

import org.eclipse.swt.SWT;
import org.junit.Test;
import org.remotercp.provisioning.dialogs.AcceptUpdateDialog;

public class RestartDialogTest {

	@Test
	public void restartDialogTest() {

		AcceptUpdateDialog dialog = new AcceptUpdateDialog();
		int open = dialog.open();
		if (open == SWT.OK) {
			System.out.println("OK pressed");
		} else {
			System.out.println("Cancel pressed");
		}
	}
}
