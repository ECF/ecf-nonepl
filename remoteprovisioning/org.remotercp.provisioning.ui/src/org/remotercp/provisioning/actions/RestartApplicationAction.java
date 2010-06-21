package org.remotercp.provisioning.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class RestartApplicationAction implements IViewActionDelegate {

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
				RestartApplicationAction.this.roster = (IRoster) event
						.getNewValue();

				if (RestartApplicationAction.this.roster == null) {
					RestartApplicationAction.this.action.setEnabled(false);
				} else {
					RestartApplicationAction.this.action.setEnabled(true);
				}
			}

		};
	}

	public void run(IAction action) {
		ISessionService sessionService = OsgiServiceLocatorUtil
				.getOSGiService(ProvisioningActivator.getBundleContext(),
						ISessionService.class);
		final ID fromId = sessionService.getConnectedID();
		assert sessionService != null : "sessionService != null";

		ID[] userIDs = RosterUtil.getUserIDs(this.roster);
		assert userIDs != null : "userIDs != null";

		try {
			List<IInstallFeaturesService> remoteService = sessionService
					.getRemoteService(IInstallFeaturesService.class, userIDs,
							null);

			for (final IInstallFeaturesService featuresService : remoteService) {
				/*
				 * as each user can independently decide to restart his
				 * application perform the request in a job and don't wait for
				 * the commitment of each user.
				 */
				Job restartJob = new Job("Restart remote application") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						boolean result = featuresService
								.acceptUpdate(fromId);
						if (result) {
							IStatus restartResults = featuresService
									.restartApplication(fromId);
							ErrorView.addError(restartResults);
						} else {
							ErrorView.addError(new Status(IStatus.CANCEL, "Anonym", "User denies remote administration"));
						}
						return Status.OK_STATUS;
					}
				};
				restartJob.setUser(false);
				restartJob.schedule();
			}

		} catch (ECFException e) {
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
	}

}
