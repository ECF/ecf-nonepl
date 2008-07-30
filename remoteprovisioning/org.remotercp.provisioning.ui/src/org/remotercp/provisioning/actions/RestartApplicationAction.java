package org.remotercp.provisioning.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.provisioning.IInstallFeaturesService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.provisioning.ProvisioningActivator;
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
		Assert.isNotNull(sessionService);

		ID[] userIDs = RosterUtil.getUserIDs(this.roster);
		Assert.isNotNull(userIDs);

		try {
			List<IInstallFeaturesService> remoteService = sessionService
					.getRemoteService(IInstallFeaturesService.class, userIDs,
							null);

			for (IInstallFeaturesService featuresService : remoteService) {
				featuresService.restartApplication();
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
