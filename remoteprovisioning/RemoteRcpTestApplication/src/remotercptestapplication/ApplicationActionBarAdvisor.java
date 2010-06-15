package remotercptestapplication;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private Action uninstallAction;
	private Action installAction;
	private Action updateAction;
	private IContributionItem viewsAction;

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		
	}
	
	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		super.makeActions(window);
		
		viewsAction = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		
		installAction = new Action() {
			public void run() {
				IStatus result = Activator.getDefault().install();
				System.out.println("	Install "+result.getMessage());
				if (result.isOK()) {
					showMessage(window, "Installation successful");
				} else {
					showError(window, "Installation failed", result);
				}
			};
		};
		installAction.setText("Install");
		installAction.setId("de.c1wps.InstallAction");
		
		updateAction = new Action() {
			public void run() {
				IStatus result = Activator.getDefault().update();
				System.out.println("	Update "+result.getMessage());
				if (result.isOK()) {
					showMessage(window, "Update successful");
				} else {
					showError(window, "Update failed", result);
				}
			};
		};
		updateAction.setText("Update");
		updateAction.setId("de.c1wps.UpdateAction");
		
		uninstallAction = new Action() {
			public void run() {
				IStatus result = Activator.getDefault().uninstall();
				System.out.println("	Uninstall "+result.getMessage());
				if (result.isOK()) {
					showMessage(window, "Uninstallation successful");
				} else {
					showError(window, "Uninstallation failed", result);
				}
			};
		};
		uninstallAction.setText("Uninstall");
		uninstallAction.setId("de.c1wps.UninstallAction");
		
		register(updateAction);
	}
	
	private void showMessage(IWorkbenchWindow window, String title) {
		MessageDialog.openInformation(window.getShell(), title, "Please restart the application");
	}
	
	private void showError(IWorkbenchWindow window, String title, IStatus status) {
		MessageDialog.openError(window.getShell(), title, status.getMessage());
	}
	
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		fileMenu.add(installAction);
		fileMenu.add(new Separator());
		fileMenu.add(updateAction);
		fileMenu.add(new Separator());
		fileMenu.add(uninstallAction);
		fileMenu.add(new Separator());
		fileMenu.add(viewsAction);
	}

}
