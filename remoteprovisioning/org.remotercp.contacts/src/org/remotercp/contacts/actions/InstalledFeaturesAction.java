package org.remotercp.contacts.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.remotercp.contacts.ui.ContactsView;
import org.remotercp.provisioning.editor.ProvisioningEditorInput;
import org.remotercp.provisioning.editor.ui.ProvisioningEditor;
import org.remotercp.util.roster.RosterUtil;

public class InstalledFeaturesAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	private IWorkbenchPart targetPart;

	private ContactsView contactsView;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
		if (targetPart instanceof ContactsView) {
			this.contactsView = (ContactsView) targetPart;
		}
	}

	public void run(IAction action) {

		this.contactsView.doMarkSelectedRosterItems();

		List<IRosterEntry> onlineUser = new ArrayList<IRosterEntry>();

		Iterator iterator = this.selection.iterator();
		while (iterator.hasNext()) {
			IRosterItem next = (IRosterItem) iterator.next();
			List<IRosterEntry> tempUser = RosterUtil.filterOnlineUser(next);

			onlineUser.addAll(tempUser);
		}

		// get all selected user
		// onlineUser = RosterUtil.filterOnlineUser((IRosterItem) this.selection
		// .getFirstElement());
		Assert.isNotNull(onlineUser);

		ID[] userIDs = RosterUtil.getUserIDs(onlineUser);
		Assert.isNotNull(userIDs);

		ProvisioningEditorInput editorInput = new ProvisioningEditorInput(
				userIDs);

		IWorkbenchPage activePage = this.targetPart.getSite()
				.getWorkbenchWindow().getActivePage();
		try {
			// open editor
			IEditorPart openEditor = activePage.openEditor(editorInput,
					ProvisioningEditor.ID);
			openEditor
					.addPropertyListener(this.contactsView.new EditorClosedListener());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;

			boolean isItemOnline = RosterUtil
					.isRosterItemOnline((IRosterItem) this.selection
							.getFirstElement());

			action.setEnabled(isItemOnline);

		}
	}
}
