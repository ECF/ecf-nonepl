package org.remotercp.login.ui;

import java.util.logging.Logger;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChatLoginWizardDialog extends WizardDialog {

	private final static Logger logger = Logger
			.getLogger(ChatLoginWizardDialog.class.getName());

	public ChatLoginWizardDialog() {
		super(Display.getDefault().getActiveShell(), new ChatLoginWizard());

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

	// @Override
	// public int open() {
	// int result = super.open();
	//
	// if (result == Window.OK) {
	// this.startRemoteServices();
	// }
	// return result;
	// }
	//
	// private void startRemoteServices() {
	// IExtension extension = Platform.getExtensionRegistry().getExtension(
	// "org.remotercp.remoteService");
	// IConfigurationElement[] configurationElements = extension
	// .getConfigurationElements();
	//
	// for (IConfigurationElement element : configurationElements) {
	// try {
	// Object executableExtension = element
	// .createExecutableExtension("class");
	// Assert.isNotNull(executableExtension);
	//
	// if (executableExtension instanceof IRemoteServiceLauncher) {
	// IRemoteServiceLauncher launcher = (IRemoteServiceLauncher)
	// executableExtension;
	// launcher.startService();
	// }
	// } catch (CoreException e) {
	// logger
	// .severe("Unable to create executable extension for element: "
	// + element.toString());
	// e.printStackTrace();
	// }
	// }
	// }

}