package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.provider.skype.ui.Messages;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.ecf.provider.skype.SkypeContainer;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.osgi.util.NLS;

public class SkypeActionContributionItems extends AbstractRosterEntryContributionItem {

	private static final IContributionItem[] EMPTY_ARRAY = new IContributionItem[0];

	public SkypeActionContributionItems() {
	}

	public SkypeActionContributionItems(String id) {
		super(id);
	}

	private IAction[] makeActions() {
		IRosterEntry entry = getSelectedRosterEntry();
		IContainer c = getContainerForRosterEntry(entry);
		if (entry != null && c != null && c instanceof SkypeContainer) {
			IAction[] actions = new IAction[1];
			actions[0] = new SkypeCallAction(
						entry.getUser().getID(),
						NLS
								.bind(
										Messages.SkypeActionContributionItems_Call_User,
										entry.getName()),
						NLS
								.bind(
										Messages.SkypeActionContributionItems_Call_User_Tooltip,
										entry.getName()));
				return actions;
			}
		return null;
	}

	protected IContributionItem[] getContributionItems() {
		IAction[] actions = makeActions();
		if (actions == null)
			return EMPTY_ARRAY;
		IContributionItem[] items = new IContributionItem[actions.length];
		for (int i = 0; i < actions.length; i++)
			items[i] = new ActionContributionItem(actions[i]);
		return items;
	}

}
