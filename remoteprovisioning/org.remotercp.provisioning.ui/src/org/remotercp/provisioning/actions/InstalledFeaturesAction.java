package org.remotercp.provisioning.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.remotercp.provisioning.editor.ProvisioningEditorInput;
import org.remotercp.provisioning.editor.ui.ProvisioningEditor;
import org.remotercp.util.roster.RosterUtil;

public class InstalledFeaturesAction implements IViewActionDelegate {

	private IViewPart view;
	private IRoster roster;
	private IAction action;

	public void init(IViewPart view) {
		this.view = view;

		// register listener for changes in view.
		PropertyChangeSupport pcs = (PropertyChangeSupport) this.view
				.getAdapter(IPropertyChangeListener.class);
		pcs.addPropertyChangeListener(getPropertyChangeListener());

		this.roster = (IRoster) this.view.getAdapter(IRoster.class);

	}

	private PropertyChangeListener getPropertyChangeListener() {
		return new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				InstalledFeaturesAction.this.roster = (IRoster) event
						.getNewValue();

				if (InstalledFeaturesAction.this.roster == null) {
					InstalledFeaturesAction.this.action.setEnabled(false);
				} else {
					InstalledFeaturesAction.this.action.setEnabled(true);
				}
			}

		};
	}

	@SuppressWarnings("unchecked")
	public void run(IAction action) {

		ID[] userIDs = RosterUtil.getUserIDs(this.roster);
		assert userIDs != null : "userIDs != null";

		ProvisioningEditorInput editorInput = new ProvisioningEditorInput(
				userIDs);
		editorInput.setArtifactToShow(ProvisioningEditorInput.FEATURE);

		IWorkbenchPage activePage = this.view.getSite().getWorkbenchWindow()
				.getActivePage();
		try {
			// open editor
			activePage.openEditor(editorInput, ProvisioningEditor.ID);

		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
	}

}
