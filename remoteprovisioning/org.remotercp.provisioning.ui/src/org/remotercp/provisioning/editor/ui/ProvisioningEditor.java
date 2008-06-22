package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
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
			handleInstalledBundles(installedFeaturesServiceList);
			break;
		case ProvisioningEditorInput.FEATURE:
			handleInstalledFeatures();
			break;
		default:
			break;
		}

	}

	/*
	 * Collects the bundle list from selected user and does:
	 * 
	 * 1. display common bundles (inersection)
	 * 
	 * 2. diplay different bundles (difference)
	 * 
	 * 3. display user for different bundles, that means you can see which users
	 * have different bundles
	 */
	protected void handleInstalledBundles(
			List<IInstalledFeaturesService> serviceList) {

		/*
		 * TODO: perform a remote call on all selected user and ask for
		 * installed features. Display the features in the editor part. To
		 * display common features a set of all common features has to be
		 * defined as well as the intersection of different features/bundles
		 */
		final Set<SerializedBundleWrapper> commonBundles = new TreeSet<SerializedBundleWrapper>();
		final Set<SerializedBundleWrapper> differentBundles = new TreeSet<SerializedBundleWrapper>();
		final Set<SerializedBundleWrapper> allBundles = new TreeSet<SerializedBundleWrapper>();

		final Map<ID, Collection<SerializedBundleWrapper>> userBundles = new HashMap<ID, Collection<SerializedBundleWrapper>>();

		for (final IInstalledFeaturesService featureService : serviceList) {

			try {
				Collection<SerializedBundleWrapper> installedBundles = featureService
						.getInstalledBundles();

				/*
				 * store the relationship between user and bundles
				 */
				ID userID = featureService.getUserID();
				userBundles.put(userID, installedBundles);
				allBundles.addAll(installedBundles);

				logger.info("Remote installed bundles received");

				if (commonBundles.isEmpty()) {
					// start with any collection
					commonBundles.addAll(installedBundles);

				} else {
					// get the intersection of bundles
					commonBundles.retainAll(installedBundles);
				}

			} catch (Exception e) {
				IStatus error = new Status(
						IStatus.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"Unable to get installed bundles on the remote rpc application",
						e);
				ErrorView.addError(error);
			}
		}

		// the difference between allbundles and interception
		allBundles.removeAll(commonBundles);
		differentBundles.addAll(allBundles);

		final Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser = getRelationshipDifferentBundleToUser(
				userBundles, differentBundles);
		// set table input
		 Display.getDefault().asyncExec(new Runnable() {
			public void run() {
		setCommonBundlesInput(commonBundles);
		setDifferentBundlesInput(differentBundles);
		setDifferentBundlesToUserRelationship(differentBundleToUser);
		 }
		 });
	}

	/*
	 * In oder to see which user have one or more different bundles we have to
	 * map now bundles to user. The result should be like bundle
	 * org.eclipse.example is used by user John and Sandy but not by Peter.
	 */
	protected Map<SerializedBundleWrapper, Collection<ID>> getRelationshipDifferentBundleToUser(
			Map<ID, Collection<SerializedBundleWrapper>> userBundles,
			Set<SerializedBundleWrapper> differentBundles) {
		Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser = new HashMap<SerializedBundleWrapper, Collection<ID>>();
		// Map<String, Collection<ID>> differentBundleToUser = new HashMap<String, Collection<ID>>();

		for (SerializedBundleWrapper differentBundle : differentBundles) {
			for (ID userID : userBundles.keySet()) {
				/*
				 * check whether user has a different bundle installed
				 */
				Collection<SerializedBundleWrapper> userBundleCollection = userBundles
						.get(userID);

				if (userBundleCollection.contains(differentBundle)) {
					/*
					 * Check if there is already a key for the given bundle. If
					 * this is the case add additional user ID
					 */
					if (differentBundleToUser.containsKey(differentBundle)) {
						Collection<ID> collection = differentBundleToUser
								.get(differentBundle);
						collection.add(userID);
					} else {
						// create new key and collection
						Collection<ID> user = new ArrayList<ID>();
						user.add(userID);
						differentBundleToUser.put(differentBundle, user);
					}
				}
			}
		}

		return differentBundleToUser;
	}

	protected void handleInstalledFeatures() {
		throw new UnsupportedOperationException("MEthod not implemented yet");
	}

	@Override
	public void setFocus() {
		this.featuresFolder.setFocus();

	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 
	 * In order to be able to test the above methods extra setInput methods have
	 * been created
	 * 
	 */

	protected void setCommonBundlesInput(
			final Set<SerializedBundleWrapper> commonBundles) {
		installedFeaturesComposite.setInstalledInput(commonBundles);
	}

	protected void setDifferentBundlesInput(
			final Set<SerializedBundleWrapper> differentBundles) {
		installedFeaturesComposite.setDifferentInput(differentBundles);
	}

	protected void setDifferentBundlesToUserRelationship(
			final Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser) {
		installedFeaturesComposite.setUserInput(differentBundleToUser);
	}

}
