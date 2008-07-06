package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesUserTreeNode;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.images.ImageKeys;

public class FeaturesVersionsComposite {

	private CheckboxTreeViewer featureVersionsViewer;

	private static final int COLUMN_NAME = 0;

	private static final int COLUMN_VERSION = 1;

	public static enum Buttons {
		UPDATE, PROPERTIES, BACK
	};

	private Composite main;

	private Button update;

	private Button properties;

	private Button back;

	private Set<CommonFeaturesTreeNode> selectedFeatures;

	public FeaturesVersionsComposite(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	protected void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		{

			SashForm sash = new SashForm(main, SWT.HORIZONTAL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(sash);
			{
				this.featureVersionsViewer = new CheckboxTreeViewer(sash,
						SWT.V_SCROLL | SWT.H_SCROLL);

				Tree tree = this.featureVersionsViewer.getTree();
				GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);

				this.featureVersionsViewer
						.setContentProvider(new FeaturesTreeContentProvider());
				// ILabelDecorator decorator = PlatformUI.getWorkbench()
				// .getDecoratorManager().getLabelDecorator();
				// this.featureVersionsViewer
				// .setLabelProvider(new FeatureVersionsLabelProvider(
				// new FeaturesVersionTableLabelProvider(), decorator));
				this.featureVersionsViewer
						.setLabelProvider(new FeatureVersionsLabelProvider());

				// If a user checks a checkbox in the tree, check also the
				// children
				this.featureVersionsViewer
						.addCheckStateListener(new ICheckStateListener() {

							public void checkStateChanged(
									CheckStateChangedEvent event) {
								if (event.getChecked()) {
									featureVersionsViewer.setSubtreeChecked(
											event.getElement(), true);
								}
							}

						});
			}

			{
				Composite buttonComp = new Composite(sash, SWT.None);
				buttonComp.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						buttonComp);

				update = new Button(buttonComp, SWT.PUSH);
				update.setText("Update");

				properties = new Button(buttonComp, SWT.PUSH);
				properties.setText("Properties");

				// space label
				new Label(buttonComp, SWT.None);

				back = new Button(buttonComp, SWT.PUSH);
				back.setText("<< Back");

			}

			sash.setWeights(new int[] { 80, 20 });
		}
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

		// search for updates in a job
		Job searchForUpdatesJob = new Job("Search for updates") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Searching...", selectedFeatures.size());

				List<FeatureTreeNode> featuresToUpdate = getFeaturesToUpdate(
						selectedFeatures, monitor);

				setViewerInput(featuresToUpdate);

				return Status.OK_STATUS;
			}
		};
		searchForUpdatesJob.setUser(true);
		searchForUpdatesJob.schedule();

	}

	private void setViewerInput(final List<FeatureTreeNode> featuresToUpdate) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				FeaturesVersionsComposite.this.featureVersionsViewer
						.setInput(featuresToUpdate);
				FeaturesVersionsComposite.this.featureVersionsViewer
						.expandAll();
			}
		});
	}

	private Display getDisplay() {
		if (Display.getCurrent() == null) {
			return Display.getDefault();
		} else {
			return Display.getCurrent();
		}
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

					PluginVersionIdentifier remoteFeatureVersion = remoteFeature.getVersionedIdentifier()
							.getVersion();

					PluginVersionIdentifier installedFeaturesVersion = new PluginVersionIdentifier(
							greatestFeatureVersion);

					if (remoteFeatureVersion
							.isGreaterThan(installedFeaturesVersion)) {
						remoteFeatures.add(siteFeatureReference);
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

	private List<FeatureTreeNode> getFeaturesToUpdate(
			Set<CommonFeaturesTreeNode> selectedFeatures,
			final IProgressMonitor monitor) {

		Map<SerializedFeatureWrapper, List<ISiteFeatureReference>> featuresWithNewerVersions = getFeaturesWithNewerVersions(
				selectedFeatures, monitor);

		List<FeatureTreeNode> treeElements = new ArrayList<FeatureTreeNode>();

		// create tree elements
		for (SerializedFeatureWrapper node : featuresWithNewerVersions.keySet()) {
			List<ISiteFeatureReference> featureReferences = featuresWithNewerVersions
					.get(node);
			if (!featureReferences.isEmpty()) {
				try {

					ISiteFeatureReference reference = featureReferences.get(0);

					// create update site node
					String updateSiteLabel = reference.getFeature(null)
							.getDiscoverySiteEntries()[0].getAnnotation();
					FeatureTreeNode updateSiteNode = new FeatureTreeNode(
							updateSiteLabel);
					updateSiteNode.setUpdateSiteLabel(true);

					// create category node
					String categoryLabel = reference.getCategories()[0]
							.getLabel();
					FeatureTreeNode categoryNode = new FeatureTreeNode(
							categoryLabel);
					categoryNode.setCategoryLabel(true);

					updateSiteNode.addChild(categoryNode);

					for (ISiteFeatureReference ref : featureReferences) {
						IFeature feature;

						feature = ref.getFeature(null);

						String featureVersion = feature
								.getVersionedIdentifier().getVersion()
								.toString();
						String featureLabel = feature.getLabel();

						FeatureTreeNode featureNode = new FeatureTreeNode(
								featureLabel + " " + featureVersion);
						featureNode.setFeatureLabel(true);

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
			}
		}

		return treeElements;
	}

	// protected SerializedFeatureWrapper getFeature(TreeNode node) {
	// if (node instanceof CommonFeaturesTreeNode) {
	// CommonFeaturesTreeNode commonNode = (CommonFeaturesTreeNode) node;
	// return (SerializedFeatureWrapper) commonNode.getValue();
	// }
	// if (node instanceof CommonFeaturesUserTreeNode) {
	// CommonFeaturesUserTreeNode commonUserNode = (CommonFeaturesUserTreeNode)
	// node;
	// return (SerializedFeatureWrapper) commonUserNode.getValue();
	// }
	//
	// return null;
	// }

	protected FeatureTreeNode getFeature(List<FeatureTreeNode> nodes,
			String identyfier) {
		for (FeatureTreeNode node : nodes) {
			Object value = node.getValue();
			if (value instanceof String) {
				String id = (String) value;
				if (id.equals(identyfier)) {
					return node;
				}
			}
		}
		return null;
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
			FeatureTreeNode node = (FeatureTreeNode) element;

			if (node.isUpdateSiteLabel) {
				image = updatesite;
			}
			if (node.isCategoryLabel) {
				image = category;
			}
			if (node.isFeatureLabel) {
				image = feature;
			}
			return image;
		}

		@Override
		public String getText(Object element) {
			FeatureTreeNode node = (FeatureTreeNode) element;
			String label = (String) node.getValue();
			return label;
		}

		// private final ITableLabelProvider provider;
		// private final ILabelDecorator decorator;
		//
		// public FeatureVersionsLabelProvider(ILabelProvider provider,
		// ILabelDecorator decorator) {
		// super(provider, decorator);
		// this.decorator = decorator;
		// this.provider = (ITableLabelProvider) provider;
		// }
		//
		// public Image getColumnImage(Object element, int columnIndex) {
		// Image image = provider.getColumnImage(element, columnIndex);
		// if (decorator != null) {
		// Image decorated = decorator.decorateImage(image, element);
		// if (decorated != null) {
		// return decorated;
		// }
		// }
		// return image;
		//
		// }
		//
		// public String getColumnText(Object element, int columnIndex) {
		// String text = provider.getColumnText(element, columnIndex);
		// if (decorator != null) {
		// String decorated = decorator.decorateText(text, element);
		// if (decorated != null) {
		// return decorated;
		// }
		// }
		// return text;
		//
		// }

	}

	/**
	 * Table label provider in order to display columns in a treeviewer.
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class FeaturesVersionTableLabelProvider implements
			ITableLabelProvider, ILabelProvider {

		private Image checked = ProvisioningActivator.getImageDescriptor(
				ImageKeys.CHECKED).createImage();

		private Image unchecked = ProvisioningActivator.getImageDescriptor(
				ImageKeys.UNCHECKED).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			switch (columnIndex) {
			case COLUMN_NAME:
				if (element instanceof FeatureTreeNode) {
					FeatureTreeNode node = (FeatureTreeNode) element;

					/*
					 * Display checkboxes only for child elements
					 */
					if (node.getParent() != null
							&& (!(node.getValue() instanceof String))) {
						if (node.isChecked()) {
							image = checked;
						} else {
							image = unchecked;
						}
					}
				}
				break;
			default:
				break;
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			String text = "";
			switch (columnIndex) {
			case COLUMN_NAME:
				if (element instanceof FeatureTreeNode) {
					FeatureTreeNode node = (FeatureTreeNode) element;
					if (node.getValue() instanceof SerializedFeatureWrapper) {
						SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
								.getValue();
						text = feature.getLabel();
					}

					if (node.getValue() instanceof String) {
						text = (String) node.getValue();
					}
				}

				break;
			case COLUMN_VERSION:
				// if (element instanceof FeaturesTreeNode) {
				// FeaturesTreeNode node = (FeaturesTreeNode) element;
				// if (node.getValue() instanceof SerializedFeatureWrapper) {
				// SerializedFeatureWrapper feature = (SerializedFeatureWrapper)
				// node
				// .getValue();
				// text = feature.getVersion();
				// }
				// }
			default:
				break;
			}
			return text;
		}

		public void addListener(ILabelProviderListener listener) {
			// do nothing

		}

		public void dispose() {
			// do nothing

		}

		public boolean isLabelProperty(Object element, String property) {
			// do nothing
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// do nothing

		}

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			return null;
		}

	}

	/**
	 * This class is used to display checked and unched boxes for a features.
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class FeatureTreeNode extends TreeNode {

		private boolean checked = false;

		private boolean isUpdateSiteLabel = false;

		private boolean isCategoryLabel = false;

		private boolean isFeatureLabel = false;

		public boolean isUpdateSiteLabel() {
			return isUpdateSiteLabel;
		}

		public void setUpdateSiteLabel(boolean isUpdateSiteLabel) {
			this.isUpdateSiteLabel = isUpdateSiteLabel;
		}

		public boolean isCategoryLabel() {
			return isCategoryLabel;
		}

		public void setCategoryLabel(boolean isCategoryLabel) {
			this.isCategoryLabel = isCategoryLabel;
		}

		public boolean isFeatureLabel() {
			return isFeatureLabel;
		}

		public void setFeatureLabel(boolean isFeatureLabel) {
			this.isFeatureLabel = isFeatureLabel;
		}

		public FeatureTreeNode(Object value) {
			super(value);
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public void addChild(TreeNode child) {
			TreeNode[] children = super.getChildren();
			TreeNode[] newChildren = null;
			if (children != null) {
				newChildren = new TreeNode[children.length + 1];
				int i;
				for (i = 0; i < children.length; i++) {
					newChildren[i] = children[i];
				}
				newChildren[i] = child;
			} else {
				newChildren = new TreeNode[1];
				newChildren[0] = child;
			}

			setChildren(newChildren);
		}
	}
}
