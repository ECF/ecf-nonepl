package org.remotercp.contacts.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.remotercp.contacts.ui.SelectedContactsView;
import org.remotercp.provisioning.editor.ProvisioningEditorInput;
import org.remotercp.provisioning.editor.ui.ProvisioningEditor;
import org.remotercp.util.roster.RosterUtil;

public class InstalledFeaturesAction implements IViewActionDelegate {

	private SelectedContactsView selectedContactsView;

	private List<IRosterEntry> onlineUser;

	private IAction action;

	public void init(IViewPart view) {
		if (view instanceof SelectedContactsView) {
			this.selectedContactsView = (SelectedContactsView) view;
			this.selectedContactsView
					.addPropertyChangeListener(getPropertyChangeListener());
		}

	}

	public void run(IAction action) {

		ID[] userIDs = RosterUtil.getUserIDs(onlineUser);
		Assert.isNotNull(userIDs);

		ProvisioningEditorInput editorInput = new ProvisioningEditorInput(
				userIDs);
		editorInput.setArtifactToShow(ProvisioningEditorInput.FEATURE);

		IWorkbenchPage activePage = this.selectedContactsView.getSite()
				.getWorkbenchWindow().getActivePage();
		try {
			// open editor
			activePage.openEditor(editorInput, ProvisioningEditor.ID);

		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;

		if (this.onlineUser == null || this.onlineUser.isEmpty()) {
			this.action.setEnabled(false);
		}
	}

	public PropertyChangeListener getPropertyChangeListener() {
		return new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				System.out.println("property changed");
				IRoster input = (IRoster) event.getNewValue();
				InstalledFeaturesAction.this.onlineUser = RosterUtil
						.filterOnlineUser(input);
				if (!InstalledFeaturesAction.this.onlineUser.isEmpty()) {
					InstalledFeaturesAction.this.action.setEnabled(true);
				} else {
					InstalledFeaturesAction.this.action.setEnabled(false);
				}

			}

		};
	}
}
