package org.remotercp.provisioning.editor.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ArtifactsSetOperationHelper;
import org.remotercp.provisioning.editor.ProvisioningEditorInput;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class ProvisioningEditor extends EditorPart {

	public static final String ID = "org.remotercp.provisioning.editor";

	private TabItem installedFeatureTabItem;

	private TabItem availableFeaturesTabItem;

	private TabFolder featuresFolder;

	private static final Logger logger = Logger
			.getLogger(ProvisioningEditor.class.getName());

	private List<IInstalledFeaturesService> installedFeaturesServiceList;

	// private ISessionService sessionService;

	private static Logger LOGGER = Logger.getLogger(ProvisioningEditor.class
			.getName());

	private InstalledFeaturesComposite installedFeaturesComposite;

	private AvailableFeaturesComposite availableFeaturesComposite;

	public ProvisioningEditor() {
		// nothing to do yet
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// nothing to do

	}

	@Override
	public void doSaveAs() {
		// nothing to do

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);

		try {
			ISessionService sessionService = OsgiServiceLocatorUtil
					.getOSGiService(ProvisioningActivator.getBundleContext(),
							ISessionService.class);

			// get selected userIDs which are going to be remote managed
			ID[] userIDs = ((ProvisioningEditorInput) getEditorInput())
					.getUserIDs();

			/*
			 * get the remote osgi services for selected users
			 */
			this.installedFeaturesServiceList = sessionService
					.getRemoteService(IInstalledFeaturesService.class, userIDs,
							null);
			Assert.isNotNull(this.installedFeaturesServiceList);

			LOGGER.log(Level.INFO, "Remote Services: "
					+ IInstalledFeaturesService.class.getName()
					+ " established");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to establish remote service: "
					+ IInstalledFeaturesService.class.getName());
			e.printStackTrace();
		}

	}

	/**
	 * This method is supposed to inform about changes on a remote service
	 * 
	 * TODO: what to do with events?
	 * 
	 * @return
	 */
	protected IRemoteServiceListener createRemoteServiceListener() {
		return new IRemoteServiceListener() {
			public void handleServiceEvent(IRemoteServiceEvent event) {
				LOGGER.log(Level.INFO, "RemoteService Event : " + event);
			}
		};
	}

	@Override
	public boolean isDirty() {
		// nothing to do yet
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// nothing to do yet
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			this.featuresFolder = new TabFolder(main, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.featuresFolder);

			{
				/*
				 * Installed Features
				 */
				this.installedFeatureTabItem = new TabItem(this.featuresFolder,
						SWT.BORDER);
				this.installedFeatureTabItem.setText("Installed Features");
				{

					SashForm installedFeaturesSash = new SashForm(
							this.featuresFolder, SWT.HORIZONTAL);
					this.installedFeatureTabItem
							.setControl(installedFeaturesSash);
					installedFeaturesSash.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							installedFeaturesSash);

					{
						installedFeaturesComposite = new InstalledFeaturesComposite(
								installedFeaturesSash, SWT.None);
					}

					installedFeaturesSash.setWeights(new int[] { 2, 1 });
				}
			}

			{
				/*
				 * Available Features
				 */
				this.availableFeaturesTabItem = new TabItem(
						this.featuresFolder, SWT.BORDER);
				this.availableFeaturesTabItem.setText("Available Features");

				{
					SashForm availableFeaturesSash = new SashForm(
							this.featuresFolder, SWT.HORIZONTAL);
					this.availableFeaturesTabItem
							.setControl(availableFeaturesSash);
					availableFeaturesSash.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							availableFeaturesSash);

					{
						availableFeaturesComposite = new AvailableFeaturesComposite(
								availableFeaturesSash, SWT.None);
					}
					availableFeaturesSash.setWeights(new int[] { 2, 1 });

				}

			}
		}

		initEditorInput();
	}

	protected void initEditorInput() {

		ProvisioningEditorInput editorInput = (ProvisioningEditorInput) getEditorInput();
		switch (editorInput.getArtifactToShow()) {
		case ProvisioningEditorInput.BUNDLE:

			Job handleBundlesJob = new Job("Retrieve remote components") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					handleInstalledBundles(installedFeaturesServiceList,
							monitor);
					return Status.OK_STATUS;
				}
			};
			handleBundlesJob.setUser(true);
			handleBundlesJob.schedule();

			break;
		case ProvisioningEditorInput.FEATURE:

			Job handleFeaturesJob = new Job("Retrieve remote  components") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					handleInstalledFeatures(installedFeaturesServiceList,
							monitor);
					return Status.OK_STATUS;
				}
			};
			handleFeaturesJob.setUser(true);
			handleFeaturesJob.schedule();
			break;
		default:
			break;
		}

	}

	/*
	 * Collects the installed bundle from selected users rcp applications and
	 * does:
	 * 
	 * 1. displays common bundles (intersection)
	 * 
	 * 2. diplays different bundles (difference)
	 * 
	 * 3. displays users for different bundles
	 */
	protected void handleInstalledBundles(
			List<IInstalledFeaturesService> serviceList,
			IProgressMonitor monitor) {

		monitor.beginTask("Receive remote installed bundles", serviceList
				.size());

		ArtifactsSetOperationHelper<SerializedBundleWrapper> bundleHelper = new ArtifactsSetOperationHelper<SerializedBundleWrapper>();
		bundleHelper.handleInstalledArtifacts(serviceList,
				SerializedBundleWrapper.class, monitor);

		final Set<SerializedBundleWrapper> commonBundles = bundleHelper
				.getCommonArtifacts();
		final Set<SerializedBundleWrapper> differentBundles = bundleHelper
				.getDifferentArtifacts();
		final Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser = bundleHelper
				.getDifferentArtifactToUser();

		// set table input
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				installedFeaturesComposite.setInstalledInput(commonBundles);
				installedFeaturesComposite.setDifferentInput(differentBundles);
				installedFeaturesComposite
						.setUserBundleInput(differentBundleToUser);
			}
		});

	}

	protected void handleInstalledFeatures(
			List<IInstalledFeaturesService> serviceList,
			IProgressMonitor monitor) {

		monitor.beginTask("Receive remote installed features", serviceList
				.size());

		ArtifactsSetOperationHelper<SerializedFeatureWrapper> featuresHelper = new ArtifactsSetOperationHelper<SerializedFeatureWrapper>();
		featuresHelper.handleInstalledArtifacts(serviceList,
				SerializedFeatureWrapper.class, monitor);

		final Set<SerializedFeatureWrapper> commonFeatures = featuresHelper
				.getCommonArtifacts();
		final Set<SerializedFeatureWrapper> differentFeatures = featuresHelper
				.getDifferentArtifacts();
		final Map<SerializedFeatureWrapper, Collection<ID>> differentFeaturesToUser = featuresHelper
				.getDifferentArtifactToUser();

		// set table input
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				installedFeaturesComposite.setInstalledInput(commonFeatures);
				installedFeaturesComposite.setDifferentInput(differentFeatures);
				installedFeaturesComposite
						.setUserFeaturesInput(differentFeaturesToUser);
			}
		});

	}

	@Override
	public void setFocus() {
		this.featuresFolder.setFocus();

	}

}
