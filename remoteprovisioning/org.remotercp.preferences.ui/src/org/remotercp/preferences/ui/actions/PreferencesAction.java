package org.remotercp.preferences.ui.actions;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.preferences.ui.PreferencesUIActivator;
import org.remotercp.preferences.ui.editor.PreferenceEditor;
import org.remotercp.preferences.ui.editor.PreferenceEditorInput;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class PreferencesAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// nothing to do yet
	}

	public void run(IAction action) {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				PreferencesUIActivator.getBundleContext(),
				ISessionService.class);

		try {

			// get online user
			ID[] userIDs = RosterUtil
					.filterOnlineUserAsArray((IRosterItem) this.selection
							.getFirstElement());

			List<IRemotePreferenceService> remoteService = sessionService
					.getRemoteService(IRemotePreferenceService.class, userIDs,
							null);

			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();

			// usually only one service should be available per user.
			for (IRemotePreferenceService prefService : remoteService) {
				File preferences = prefService.getPreferences(new String[] {});

				/* open editor */
				PreferenceEditorInput prefInput = new PreferenceEditorInput(
						preferences);
				activePage.openEditor(prefInput, PreferenceEditor.EDITOR_ID);
			}

		} catch (ECFException e) {
			IStatus ecfError = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Error occured while retrieving remote preference service",
					e);
			ErrorView.addError(ecfError);
		} catch (InvalidSyntaxException e) {
			IStatus ecfError = new Status(
					Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Invalid user filter used to retrieve remote preference service",
					e);
			ErrorView.addError(ecfError);
		} catch (PartInitException e) {
			IStatus editorError = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Unable to open preference ediro", e);
			ErrorView.addError(editorError);
		}

	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;

			// preference support is only for one user at time available
			int selectedElements = 0;
			Iterator iter = this.selection.iterator();
			while (iter.hasNext()) {
				iter.next();
				selectedElements += 1;
			}

			boolean isItemOnline = RosterUtil
					.isRosterItemOnline((IRosterItem) this.selection
							.getFirstElement());

			if (selectedElements > 1) {
				action.setEnabled(false);
			} else if (isItemOnline) {
				action.setEnabled(isItemOnline);
			}

		}
	}

}
