package org.remotercp.provisioning.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.constants.UpdateConstants;
import org.remotercp.common.provisioning.IInstallFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.ecf.RemoteMethodConstants;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesUserTreeNode;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.ui.tree.nodes.AbstractTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.CategoryTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.DummyTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.FeatureTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.ResultFeatureTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.ResultUserTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.UpdateSiteTreeNode;
import org.remotercp.provisioning.images.ImageKeys;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class FeaturesVersionsComposite {

	private CheckboxTreeViewer featureVersionsViewer;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public static enum Buttons {
		UPDATE, PROPERTIES, BACK
	};

	private Composite main;

	private Button update;

	private Button properties;

	private Button back;

	private Image backImage;

	private Image propertiesImage;

	private Image updateImage;

	private List<FeatureTreeNode> selectedFeaturesForUpdate;

	private Set<CommonFeaturesTreeNode> selectedFeatures;

	public FeaturesVersionsComposite(Composite parent, int style) {
		this.selectedFeaturesForUpdate = new ArrayList<FeatureTreeNode>();

		this.backImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.BACK).createImage();
		this.propertiesImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.PROPERTIES).createImage();
		this.updateImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.UPDATE2).createImage();

		this.createPartControl(parent, style);
	}

	protected void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	protected void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	protected void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		{

			SashForm sash = new SashForm(main, SWT.HORIZONTAL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(sash);
			{
				Group searchResultGroup = new Group(sash, SWT.None);
				searchResultGroup.setText("Update search results");
				searchResultGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						searchResultGroup);
				{

					this.featureVersionsViewer = new CheckboxTreeViewer(
							searchResultGroup, SWT.V_SCROLL | SWT.H_SCROLL);

					Tree tree = this.featureVersionsViewer.getTree();
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tree);

					this.featureVersionsViewer
							.setContentProvider(new FeaturesTreeContentProvider());
					// ILabelDecorator decorator = PlatformUI.getWorkbench()
					// .getDecoratorManager().getLabelDecorator();
					// this.featureVersionsViewer
					// .setLabelProvider(new FeatureVersionsLabelProvider(
					// new FeaturesVersionTableLabelProvider(), decorator));
					this.featureVersionsViewer
							.setLabelProvider(new FeatureVersionsLabelProvider());

					this.featureVersionsViewer.addCheckStateListener(this
							.getCheckStateListener());

					// If a user checks a checkbox in the tree, check also the
					// children
					// this.featureVersionsViewer
					// .addCheckStateListener(new ICheckStateListener() {
					//
					// public void checkStateChanged(
					// CheckStateChangedEvent event) {
					// if (event.getChecked()) {
					// featureVersionsViewer.setSubtreeChecked(
					// event.getElement(), true);
					// }
					// }
					//
					// });
				}

			}

			{
				Composite buttonComp = new Composite(sash, SWT.None);
				buttonComp.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						buttonComp);

				update = new Button(buttonComp, SWT.PUSH);
				update.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						performUpdate();
					}
				});
				update.setText("Update");
				update.setImage(updateImage);

				properties = new Button(buttonComp, SWT.PUSH);
				properties.setText("Properties");
				properties.setImage(propertiesImage);

				// space label
				new Label(buttonComp, SWT.None);

				back = new Button(buttonComp, SWT.PUSH);
				back.setText("Back");
				back.setImage(backImage);

			}

			sash.setWeights(new int[] { 80, 20 });
		}
	}

	/*
	 * Retruns a listener for check box state changes.
	 */
	private ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if (element instanceof FeatureTreeNode) {
					FeatureTreeNode node = (FeatureTreeNode) element;
					/* has feature been added or removed? */
					if (selectedFeaturesForUpdate.contains(node)) {
						if (event.getChecked() == false) {
							selectedFeaturesForUpdate.remove(node);
						}
					} else {
						selectedFeaturesForUpdate.add(node);
					}
				}
			}
		};
	}

	public List<FeatureTreeNode> getSelectedFeaturesForUpdate() {
		return this.selectedFeaturesForUpdate;
	}

	protected void addButtonListener(SelectionAdapter listener, Buttons button) {
		switch (button) {
		case UPDATE:
			this.update.addSelectionListener(listener);
			break;
		case PROPERTIES:
			this.properties.addSelectionListener(listener);
			break;
		case BACK:
			this.back.addSelectionListener(listener);
			break;
		default:
			break;
		}
	}

	private void performUpdate() {
		if (!this.selectedFeaturesForUpdate.isEmpty()) {
			Job updateFeaturesJob = new Job("Update features...") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Search for new feature versions",
							selectedFeaturesForUpdate.size());

					updateFeatures(
							FeaturesVersionsComposite.this.selectedFeaturesForUpdate,
							monitor);

					return Status.OK_STATUS;
				}
			};
			updateFeaturesJob.setUser(true);
			updateFeaturesJob.schedule();
		} else {
			IStatus info = new Status(Status.INFO,
					ProvisioningActivator.PLUGIN_ID,
					"No features for update selected");
			ErrorView.addError(info);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateFeatures(List<FeatureTreeNode> features,
			IProgressMonitor monitor) {
		List<IStatus> stateCollector = new ArrayList<IStatus>();

		ISessionService sessionService = OsgiServiceLocatorUtil
				.getOSGiService(ProvisioningActivator.getBundleContext(),
						ISessionService.class);

		final List<ResultFeatureTreeNode> resultNodes = new ArrayList<ResultFeatureTreeNode>();

		for (FeatureTreeNode featureNode : features) {
			monitor.subTask("Update feature : " + featureNode.getLabel());

			/* retrieve feature from node */
			final IFeature feature = (IFeature) featureNode.getValue();

			/* create result root node with feature */
			ResultFeatureTreeNode resultFeatureNode = new ResultFeatureTreeNode(
					SerializedFeatureWrapper.createFeatureWrapper(feature));

			resultNodes.add(resultFeatureNode);

			/* determine users to update */
			List<ID> userToUpdate = getUserToUpdate(featureNode);
			ID[] filterIDs = userToUpdate.toArray(new ID[userToUpdate.size()]);

			try {
				for (ID userId : filterIDs) {
					/*
					 * As we need to track update results we have to call the
					 * remote service for each user separatly
					 */
					ID[] filterIds = new ID[1];
					filterIds[0] = userId;

					/* retrieve remote update service for user */
					IRemoteService[] remoteServiceReference = sessionService
							.getRemoteServiceReference(
									IInstallFeaturesService.class, filterIds,
									null);
					Assert.isTrue(remoteServiceReference.length == 1);
					IRemoteService remoteUpdateService = remoteServiceReference[0];

					/* as IFeature is not serializable we have to wrap them */
					final SerializedFeatureWrapper[] featuresToUpdate = new SerializedFeatureWrapper[1];
					featuresToUpdate[0] = SerializedFeatureWrapper
							.createFeatureWrapper(feature);

					IRemoteCall updateFeaturesCall = new IRemoteCall() {

						public String getMethod() {
							return RemoteMethodConstants.UPDATE_METHOD;
						}

						public Object[] getParameters() {
							return new Object[] { featuresToUpdate };
						}

						public long getTimeout() {
							/*
							 * The timeout depends on the feature download size.
							 * Assume a slow download rate of 10kB/sec
							 */
							long kBSize = feature.getDownloadSize() / 1024;
							long timeout = kBSize / 10;

							if (timeout == 0) {
								// feature size not available, set timeout
								// to 5 min - is just a guess
								timeout = 300000;
							}
							return timeout;
						}

					};

					/* perform remote call */
					List<IStatus> updateResults = (List<IStatus>) remoteUpdateService
							.callSynch(updateFeaturesCall);

					ResultUserTreeNode resultUserNode = new ResultUserTreeNode(
							userId);
					resultUserNode.setParent(resultFeatureNode);
					resultUserNode.setUpdateResults(updateResults);

					// add child to parent
					resultFeatureNode.addChild(resultUserNode);
				}

			} catch (ECFException e) {
				IStatus error = new Status(Status.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"Unable to retrieve remote service: "
								+ IInstallFeaturesService.class.getName(), e);
				stateCollector.add(error);
			} catch (InvalidSyntaxException e) {
				IStatus error = new Status(Status.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"The provided user filter is invalid", e);
				stateCollector.add(error);
			}

			monitor.worked(1);
		}
		monitor.done();

		/*
		 * as we are in the job thread we have do asyncExec the property fire
		 * operation
		 */
		featureVersionsViewer.getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						pcs.firePropertyChange(UpdateConstants.UPDATE, null,
								resultNodes);
					}
				});
	}

	/*
	 * This method retrieves all user assigned to a feature
	 */
	private List<ID> getUserToUpdate(FeatureTreeNode node) {
		List<ID> userIDs = new ArrayList<ID>();
		for (CommonFeaturesTreeNode commonNode : this.selectedFeatures) {
			IFeature selectedFeature = (IFeature) node.getValue();
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) commonNode
					.getValue();

			if (selectedFeature.getVersionedIdentifier().getIdentifier()
					.equals(feature.getIdentifier())) {
				CommonFeaturesUserTreeNode[] children = commonNode
						.getChildren();

				for (CommonFeaturesUserTreeNode userNode : children) {
					userIDs.add(userNode.getUserId());
				}
			}
		}
		return userIDs;
	}

	/**
	 * This method is used for stacked Layout
	 * 
	 * @return
	 */
	protected Control getMainControl() {
		return main;
	}

	@SuppressWarnings("unchecked")
	protected void setSelectedFeatures(
			final Set<CommonFeaturesTreeNode> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;

		// search for updates
		Job searchForUpdatesJob = new Job("Search for updates...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Searching...", selectedFeatures.size());

				List<TreeNode> featuresToUpdate = getFeaturesToUpdate(
						selectedFeatures, monitor);

				setViewerInput(featuresToUpdate);

				return Status.OK_STATUS;
			}
		};
		searchForUpdatesJob.setUser(true);
		searchForUpdatesJob.schedule();

	}

	private void setViewerInput(final List<TreeNode> featuresToUpdate) {
		this.featureVersionsViewer.getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						FeaturesVersionsComposite.this.featureVersionsViewer
								.setInput(featuresToUpdate);
						FeaturesVersionsComposite.this.featureVersionsViewer
								.expandAll();
					}
				});
	}

	private Map<SerializedFeatureWrapper, List<ISiteFeatureReference>> getFeaturesWithNewerVersions(
			Set<CommonFeaturesTreeNode> selectedFeatures,
			final IProgressMonitor monitor) {

		/*
		 * This map is used to track which newer versions of a feature are
		 * available and CommonFeaturesTreeNode is used to store which user are
		 * going to receive the new available updates.
		 */
		Map<SerializedFeatureWrapper, List<ISiteFeatureReference>> featuresWithNewerVersions = new HashMap<SerializedFeatureWrapper, List<ISiteFeatureReference>>();

		for (CommonFeaturesTreeNode node : selectedFeatures) {
			String greatestFeatureVersion = getGreatestFeatureVersion(node);
			SerializedFeatureWrapper installedFeature = (SerializedFeatureWrapper) node
					.getValue();

			monitor.subTask("Searching for new updates for feature "
					+ installedFeature.getLabel());

			try {
				ISite site = SiteManager.getSite(installedFeature
						.getUpdateUrl(), false, null);

				ISiteFeatureReference[] featureReferences = site
						.getFeatureReferences();

				List<ISiteFeatureReference> remoteFeatures = new ArrayList<ISiteFeatureReference>();

				for (int count = 0; count < featureReferences.length; count++) {
					ISiteFeatureReference siteFeatureReference = featureReferences[count];

					IFeature remoteFeature = siteFeatureReference
							.getFeature(null);

					PluginVersionIdentifier remoteFeatureVersion = remoteFeature
							.getVersionedIdentifier().getVersion();

					PluginVersionIdentifier installedFeaturesVersion = new PluginVersionIdentifier(
							greatestFeatureVersion);

					/*
					 * consider only selected features for updates
					 */
					if (installedFeature.getIdentifier().equals(
							remoteFeature.getVersionedIdentifier()
									.getIdentifier())) {

						// do not consider older versions
						if (remoteFeatureVersion
								.isGreaterThan(installedFeaturesVersion)) {
							remoteFeatures.add(siteFeatureReference);
						}
					}
				}

				featuresWithNewerVersions.put(installedFeature, remoteFeatures);
			} catch (CoreException e) {
				Status status = new Status(Status.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"Unable to retrieve update site information for feature "
								+ installedFeature.getLabel(), e);
				ErrorView.addError(status);
			}
			monitor.worked(1);
		}

		monitor.done();

		return featuresWithNewerVersions;
	}

	private List<TreeNode> getFeaturesToUpdate(
			Set<CommonFeaturesTreeNode> selectedFeatures,
			final IProgressMonitor monitor) {

		Map<SerializedFeatureWrapper, List<ISiteFeatureReference>> featuresWithNewerVersions = getFeaturesWithNewerVersions(
				selectedFeatures, monitor);

		List<TreeNode> treeElements = new ArrayList<TreeNode>();

		// create tree elements
		for (SerializedFeatureWrapper node : featuresWithNewerVersions.keySet()) {
			List<ISiteFeatureReference> featureReferences = featuresWithNewerVersions
					.get(node);

			if (!featureReferences.isEmpty()) {
				try {
					// get basic information which are equal for all features -
					// therefore featureReferences.get(0);
					ISiteFeatureReference reference = featureReferences.get(0);
					IFeature representativeFeature = reference.getFeature(null);

					// create update site node
					UpdateSiteTreeNode updateSiteNode = new UpdateSiteTreeNode(
							representativeFeature);

					// create category node
					CategoryTreeNode categoryNode = new CategoryTreeNode(
							representativeFeature);

					updateSiteNode.addChild(categoryNode);

					for (ISiteFeatureReference ref : featureReferences) {
						IFeature feature = ref.getFeature(null);
						FeatureTreeNode featureNode = new FeatureTreeNode(
								feature);

						// add child
						categoryNode.addChild(featureNode);
					}

					treeElements.add(updateSiteNode);
				} catch (CoreException e) {
					IStatus error = new Status(Status.ERROR,
							ProvisioningActivator.PLUGIN_ID,
							"Unable to retrieve feature information", e);
					ErrorView.addError(error);
				}
			} else {
				treeElements = this.createNoNewerVersionsAvailableTree(node);
			}
		}

		return treeElements;
	}

	private List<TreeNode> createNoNewerVersionsAvailableTree(
			SerializedFeatureWrapper node) {
		DummyTreeNode dummyUpdateSiteNode = new DummyTreeNode(node,
				DummyTreeNode.UPDATESITE, node.getUpdateUrl().toString());
		DummyTreeNode dummyCategoryNode = new DummyTreeNode(node,
				DummyTreeNode.CATEGORY, node.getLabel());
		DummyTreeNode dummyFeatureNode = new DummyTreeNode(node,
				DummyTreeNode.FEATURE, "no newer versions found");

		dummyCategoryNode.addChild(dummyFeatureNode);
		dummyUpdateSiteNode.addChild(dummyCategoryNode);

		List<TreeNode> dummyNodes = new ArrayList<TreeNode>();
		dummyNodes.add(dummyUpdateSiteNode);
		return dummyNodes;
	}

	/**
	 * TODO: Support for single user selection has not been implemented yet. So
	 * far only selected features are supported as a whole.
	 * 
	 * @param nodes
	 * @return
	 */
	protected String getGreatestFeatureVersion(TreeNode node) {

		SortedSet<String> versions = new TreeSet<String>();
		if (node instanceof CommonFeaturesTreeNode) {
			CommonFeaturesTreeNode commonNode = (CommonFeaturesTreeNode) node;
			CommonFeaturesUserTreeNode[] children = commonNode.getChildren();
			for (int child = 0; child < children.length; child++) {
				CommonFeaturesUserTreeNode commonFeaturesUserTreeNode = children[child];
				SerializedFeatureWrapper feature = (SerializedFeatureWrapper) commonFeaturesUserTreeNode
						.getValue();
				versions.add(feature.getVersion());
			}

			return versions.last();
		}

		return null;
	}

	/**
	 * Label provider for remote features.
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class FeatureVersionsLabelProvider extends LabelProvider {

		private Image category = ProvisioningActivator.getImageDescriptor(
				ImageKeys.CATEGORY).createImage();

		private Image feature = ProvisioningActivator.getImageDescriptor(
				ImageKeys.FEATURE).createImage();

		private Image updatesite = ProvisioningActivator.getImageDescriptor(
				ImageKeys.UPDATESITE).createImage();

		@Override
		public Image getImage(Object element) {
			Image image = null;

			if (element instanceof UpdateSiteTreeNode) {
				image = updatesite;
			}
			if (element instanceof CategoryTreeNode) {
				image = category;
			}
			if (element instanceof FeatureTreeNode) {
				image = feature;
			}

			if (element instanceof DummyTreeNode) {
				DummyTreeNode dummyNode = (DummyTreeNode) element;
				switch (dummyNode.getNodeType()) {
				case DummyTreeNode.UPDATESITE:
					image = updatesite;
					break;
				case DummyTreeNode.CATEGORY:
					image = category;
					break;
				default:
					break;
				}
			}
			return image;
		}

		@Override
		public String getText(Object element) {
			AbstractTreeNode node = (AbstractTreeNode) element;
			return node.getLabel();
		}
	}
}
