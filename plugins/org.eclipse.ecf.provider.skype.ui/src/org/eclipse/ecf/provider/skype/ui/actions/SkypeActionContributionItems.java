package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.provider.skype.ui.Messages;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.provider.skype.SkypeContainer;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

public class SkypeActionContributionItems extends CompoundContributionItem {

	private Object selectedModel;

	public SkypeActionContributionItems() {
	}

	public SkypeActionContributionItems(String id) {
		super(id);
	}

	protected void processSelection() {
		selectedModel = null;
		IWorkbenchWindow ww = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (ww != null) {
			IWorkbenchPage p = ww.getActivePage();
			if (p != null) {
				ISelection selection = p.getSelection();
				if (selection != null
						&& selection instanceof IStructuredSelection)
					selectedModel = ((IStructuredSelection) selection)
							.getFirstElement();

			}
		}
	}

	private IAction[] makeActions() {
		if (selectedModel != null && selectedModel instanceof IRosterEntry) {
			IRosterEntry rosterEntry = (IRosterEntry) selectedModel;
			IContainer container = (IContainer) rosterEntry.getRoster()
					.getPresenceContainerAdapter().getAdapter(IContainer.class);
			IAction[] actions = new IAction[1];
			if (container instanceof SkypeContainer) {
				actions[0] = new SkypeCallAction(
						rosterEntry.getUser().getID(),
						NLS
								.bind(
										Messages.SkypeActionContributionItems_Call_User,
										rosterEntry.getName()),
						NLS
								.bind(
										Messages.SkypeActionContributionItems_Call_User_Tooltip,
										rosterEntry.getName()));
				return actions;
			}
		}
		return null;
	}

	protected IContributionItem[] getContributionItems() {
		processSelection();
		IAction[] actions = makeActions();
		if (actions == null)
			return null;
		IContributionItem[] items = new IContributionItem[actions.length];
		for (int i = 0; i < actions.length; i++)
			items[i] = new ActionContributionItem(actions[i]);
		return items;
	}

}
