package org.remotercp.core;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;

	private IWorkbenchAction aboutAction;

	private IWorkbenchWindow window;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		this.window = window;

		this.exitAction = ActionFactory.QUIT.create(window);
		this.aboutAction = ActionFactory.ABOUT.create(window);

		register(this.exitAction);
		register(this.aboutAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager hyperbolaMenu = new MenuManager("&Remote RCP", "remotercp");
		hyperbolaMenu.add(exitAction);

		MenuManager helpMenu = new MenuManager("&Help", "help");
		helpMenu.add(aboutAction);

		MenuManager windowMenu = new MenuManager("&Window",
				IWorkbenchActionConstants.M_WINDOW);

		MenuManager showViewMenu = new MenuManager("&Show View", "showview");
		showViewMenu.add(ContributionItemFactory.VIEWS_SHORTLIST
				.create(this.window));
		windowMenu.add(showViewMenu);

		menuBar.add(hyperbolaMenu);
		menuBar.add(helpMenu);
		menuBar.add(windowMenu);
	}
}
