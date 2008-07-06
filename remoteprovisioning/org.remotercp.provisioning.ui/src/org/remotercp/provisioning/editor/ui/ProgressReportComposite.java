package org.remotercp.provisioning.editor.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressReportComposite {

	private Composite main;

	public ProgressReportComposite(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			getProgressPart(main, SWT.None);
		}

	}

	protected Composite getProgressPart(Composite parent, int style) {
		Composite progress = new Composite(parent, style);
		progress.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(progress);

		{
			Label user = new Label(progress, SWT.READ_ONLY);
			user.setText("Test");

			ProgressBar progressBar = new ProgressBar(progress,
					SWT.INDETERMINATE);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(
					progressBar);
		}

		return progress;
	}

	// public static void main(String[] args) {
	// Display display = new Display();
	// Shell shell = new Shell(display);
	// shell.setLayout(new GridLayout(1, false));
	// GridDataFactory.fillDefaults().grab(true, true).applyTo(shell);
	// shell.setSize(400, 400);
	//
	// ProgressReportComposite comp = new ProgressReportComposite(shell,
	// SWT.None);
	//
	// shell.open();
	// // Set up the event loop.
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch()) {
	// // If no more entries in event queue
	// display.sleep();
	// }
	// }
	//
	// display.dispose();
	//	}

	protected Composite getMainControl() {
		return main;
	}

}
