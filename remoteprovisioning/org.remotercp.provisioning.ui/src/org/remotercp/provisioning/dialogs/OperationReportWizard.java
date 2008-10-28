package org.remotercp.provisioning.dialogs;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This wizard shows the remote operation results if a user double clicks on a
 * particular result in the progress report composite.
 * 
 * @author Eugen Reiswich
 * 
 */
public class OperationReportWizard extends Wizard {

	private ReportPage reportPage;

	public OperationReportWizard(List<IStatus> operationReports) {
		this.reportPage = new ReportPage("Operation Report", operationReports);
		this.addPage(reportPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	private class ReportPage extends WizardPage {
		private List<IStatus> operationReports;

		protected ReportPage(String pageName, List<IStatus> operationReports) {
			super(pageName);
			setTitle("Remote operation results");
			setDescription("This page lists the results of the remote performed operations");
			this.operationReports = operationReports;
		}

		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.None);
			main.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

			{
				Text report = new Text(main, SWT.BORDER | SWT.MULTI);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(report);

				for (IStatus operationReport : operationReports) {
					int severity = operationReport.getSeverity();
					String text = null;

					if (severity == Status.OK) {
						text = "SUCCESSFUL";
					} else if (severity == Status.WARNING) {
						text = "WARNING";
					} else if (severity == Status.ERROR) {
						text = "FAILED";
					} else if (severity == Status.CANCEL) {
						text = "ABORTED";
					} else {
						text = "UNKNOWN";
					}
					report.append(text + ": " + operationReport.getMessage());
					report.append("\n");
				}
			}
			setControl(main);
		}
	}

}
