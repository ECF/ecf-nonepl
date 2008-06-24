package org.remotercp.ecf.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.remotercp.ecf.ECFActivator;
import org.remotercp.ecf.images.ImageKeys;

public class ConnectionAction implements IWorkbenchWindowPulldownDelegate {

	private Menu connection;
	private MenuItem connect, disconnect;
	private Image connectImage, disconnectImage;

	public Menu getMenu(Control parent) {
		connection = new Menu(parent);

		connect = new MenuItem(connection, SWT.PUSH);
		connect.setText("Connect");
		connectImage = ECFActivator.imageDescriptorFromPlugin(
				ECFActivator.PLUGIN_ID, ImageKeys.CONNECT).createImage();
		connect.setImage(connectImage);

		// at startup user is already connected
		connect.setEnabled(true);

		connect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Connect selected");
			}
		});

		disconnect = new MenuItem(connection, SWT.PUSH);
		disconnect.setText("Disconnect");
		disconnectImage = ECFActivator.imageDescriptorFromPlugin(
				ECFActivator.PLUGIN_ID, ImageKeys.DISCONNECT).createImage();
		disconnect.setImage(disconnectImage);
		disconnect.setEnabled(true);

		disconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Disconnect selected");
			}
		});

		return connection;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		
		System.out.println("Action : " + action);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("Action : " + action + " Selection: " + selection);
	}

}
