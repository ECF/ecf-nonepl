package org.remotercp.provisioning.dialogs;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

public class OperationReportWizardTest {

	private ArrayList<IStatus> operationReports;

	@Before
	public void createOperationReports() {
		this.operationReports = new ArrayList<IStatus>();

		IStatus ok = new SerializableStatus(IStatus.OK, "example.plugin",
				"Everything is perfekt");

		IStatus warning = new SerializableStatus(IStatus.WARNING,
				"example.plugin",
				"Warnings occured while performing an operation");

		IStatus error = new SerializableStatus(IStatus.ERROR, "example.plugin",
				"Problems occured while performing remote operation");

		this.operationReports.add(ok);
		this.operationReports.add(warning);
		this.operationReports.add(error);
	}

	@Test
	public void testOperationReport() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		OperationReportWizard wizard = new OperationReportWizard(
				this.operationReports);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		int open = dialog.open();
		if (open == Dialog.OK) {
			wizard.dispose();
			dialog = null;
		}
	}

}
