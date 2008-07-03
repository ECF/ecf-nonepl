package org.remotercp.provisioning.editor.ui;

import java.util.Collection;
import java.util.List;
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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ProvisioningEditorInput;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.DifferentFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeCreator;
import org.remotercp.provisioning.editor.ui.tree.InstalledFeaturesTest;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class ProvisioningEditor extends EditorPart {

	public static final String ID = "org.remotercp.provisioning.editor";

	private TabItem installedFeatureTabItem;

	private TabItem availableFeaturesTabItem;

	private TabFolder featuresFolder;

	private StackLayout stackLayout;

	private static final Logger logger = Logger
			.getLogger(ProvisioningEditor.class.getName());

	private List<IInstalledFeaturesService> installedFeaturesServiceList;

	// private ISessionService sessionService;

	private static Logger LOGGER = Logger.getLogger(ProvisioningEditor.class
			.getName());

	private InstalledFeaturesComposite installedFeaturesComposite;

	private FeaturesVersionsComposite featuresVersionsComposite;

	private AvailableFeaturesComposite availableFeaturesComposite;

	private SashForm installedFeaturesSash;

	private Composite installedFeaturesMainComposite;

	public ProvisioningEditor() {
		// nothing to do yet
		this.stackLayout = new StackLayout();
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
		// main.setLayout(stackLayout);
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

				installedFeaturesMainComposite = new Composite(
						this.featuresFolder, SWT.None);
				installedFeaturesMainComposite.setLayout(stackLayout);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						installedFeaturesMainComposite);

				{

					installedFeaturesSash = new SashForm(
							installedFeaturesMainComposite, SWT.HORIZONTAL);
					installedFeaturesSash.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							installedFeaturesSash);

					{
						installedFeaturesComposite = new InstalledFeaturesComposite(
								installedFeaturesSash, SWT.None);
						// Listener
						installedFeaturesComposite.addButtonListener(
								new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										ProvisioningEditor.this
												.handleCheckUpdates();
									}
								},
								InstalledFeaturesComposite.Buttons.CHECK_FOR_UPDATES);

					}

					installedFeaturesSash.setWeights(new int[] { 2, 1 });
				}
				this.installedFeatureTabItem
						.setControl(installedFeaturesMainComposite);
				stackLayout.topControl = installedFeaturesSash;
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

			// Job handleBundlesJob = new Job("Retrieve remote components") {
			// @Override
			// protected IStatus run(IProgressMonitor monitor) {
			// handleInstalledBundles(installedFeaturesServiceList,
			// monitor);
			// return Status.OK_STATUS;
			// }
			// };
			// handleBundlesJob.setUser(true);
			// handleBundlesJob.schedule();

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

	// protected void handleInstalledBundles(
	// List<IInstalledFeaturesService> serviceList,
	// IProgressMonitor monitor) {
	//
	// monitor.beginTask("Receive remote installed bundles", serviceList
	// .size());
	//
	// FeaturesSetOperationHelper<SerializedBundleWrapper> bundleHelper = new
	// FeaturesSetOperationHelper<SerializedBundleWrapper>();
	// bundleHelper.handleInstalledArtifacts(serviceList,
	// SerializedBundleWrapper.class, monitor);
	//
	// final Set<SerializedBundleWrapper> commonBundles = bundleHelper
	// .getCommonArtifacts();
	// final Set<SerializedBundleWrapper> differentBundles = bundleHelper
	// .getDifferentArtifacts();
	// final Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser
	// = bundleHelper
	// .getDifferentArtifactToUser();
	//
	// // set table input
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// installedFeaturesComposite.setInstalledInput(commonBundles);
	// installedFeaturesComposite.setDifferentInput(differentBundles);
	// installedFeaturesComposite
	// .setUserBundleInput(differentBundleToUser);
	// }
	// });
	//
	// }

	private void handleCheckUpdates() {
		Collection<SerializedFeatureWrapper> selectedFeatures = this.installedFeaturesComposite
				.getSelectedFeatures();

		if (this.featuresVersionsComposite == null) {
			this.featuresVersionsComposite = new FeaturesVersionsComposite(
					this.installedFeaturesMainComposite, SWT.None);
		}

		this.featuresVersionsComposite.setInput(selectedFeatures);
		this.stackLayout.topControl = this.featuresVersionsComposite
				.getMainControl();
		this.installedFeaturesMainComposite.layout();
	}

	/*
	 * Collects the installed features from selected users rcp applications and
	 * does:
	 * 
	 * 1. displays common features (intersection)
	 * 
	 * 2. diplays different features (difference)
	 * 
	 * 3. displays users for different features
	 */
	protected void handleInstalledFeatures(
			List<IInstalledFeaturesService> serviceList,
			IProgressMonitor monitor) {

		monitor.beginTask("Receive remote installed features", serviceList
				.size());

		FeaturesTreeCreator featuresHelper = new FeaturesTreeCreator();
		Collection<IStatus> errors = featuresHelper.handleInstalledFeatures(
				serviceList, monitor);

		if (!errors.isEmpty()) {
			ErrorView.addError(errors);
		}

		// final Collection<CommonFeaturesTreeNode> commonFeaturesNodes =
		// featuresHelper
		// .getCommonFeaturesNodes();
		// final Collection<DifferentFeaturesTreeNode> differentFeaturesNodes =
		// featuresHelper
		// .getDifferentFeaturesNodes();

		// XXX for Tests only
		InstalledFeaturesCompositeTest test = new InstalledFeaturesCompositeTest();
		test.setUp();

		final Collection<CommonFeaturesTreeNode> commonFeaturesNodes = test
				.getDummyCommonFeatures();
		final Collection<DifferentFeaturesTreeNode> differentFeaturesNodes = test
				.getDummyDifferentFeatures();

		// set table input
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				installedFeaturesComposite
						.setCommonFeaturesInput(commonFeaturesNodes);
				installedFeaturesComposite
						.setDifferentFeaturesInput(differentFeaturesNodes);
			}
		});

	}

	@Override
	public void setFocus() {
		this.featuresFolder.setFocus();

	}

}
