package org.remotercp.login.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChatLoginWizardDialog extends WizardDialog {

	public ChatLoginWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(400, 350);

		// center the Dialog
		Rectangle bounds = Display.getCurrent().getBounds();
		Point size = newShell.getSize();
		int xPosition = (bounds.width - size.x) / 2;
		int yPosition = (bounds.height - size.y) / 2;
		newShell.setLocation(xPosition, yPosition);
	}

}
