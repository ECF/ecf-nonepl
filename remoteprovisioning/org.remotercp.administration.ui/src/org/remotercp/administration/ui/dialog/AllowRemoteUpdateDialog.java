package org.remotercp.administration.ui.dialog;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AllowRemoteUpdateDialog extends Dialog {

	private final String _adminName;
	private Timer _timer;
	private TimerTask _task;

	public AllowRemoteUpdateDialog(Shell parentShell, String adminName) {
		super(parentShell);
		_adminName = adminName;
		_timer = new Timer("remote Update Timer");
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Remote update allowed?");
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);
		
		Label label = new Label(main, SWT.WRAP);
		label.setText(_adminName + " wants to update your system.\nAfter the update a restart is necessary.\n\nDo you allow this process?");
		
		final Label remainingTimeLabel = new Label(main, SWT.WRAP);
		remainingTimeLabel.setText("This dialog will automatically close and accept the update in 30 seconds.");
		
		_task = new TimerTask() {
			
			private int remainingTime = 30; 
			
			@Override
			public void run() {
				remainingTime--;
				remainingTimeLabel.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						remainingTimeLabel.setText("This dialog will automatically close and accept the update in " + remainingTime + " seconds.");
						if (remainingTime <= 0) {
							okPressed();
						}	
					}
				});
				if (remainingTime <= 0) {
					cancel();
				}
			}
		};
		_timer.scheduleAtFixedRate(_task, 1000, 1000);
		return main;
	}
	
	@Override
	protected void okPressed() {
		_timer.cancel();
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		_timer.cancel();
		super.cancelPressed();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create YES and NO buttons
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.NO_LABEL, false);
	}

}
