package org.remotercp.provisioning.editor.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.internal.ui.UpdateUI;
import org.eclipse.update.internal.ui.model.DiscoveryFolder;
import org.eclipse.update.internal.ui.model.SiteBookmark;
import org.eclipse.update.internal.ui.model.UpdateModel;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.ui.tree.nodes.AbstractTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.CategoryTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.DummyTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.FeatureTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.UpdateSiteTreeNode;
import org.remotercp.provisioning.images.ImageKeys;

public class AvailableFeaturesComposite {

	private CheckboxTreeViewer checkboxTreeViewer;

	public AvailableFeaturesComposite(SashForm parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {

		Composite featuresComp = new Composite(parent, SWT.None);
		featuresComp.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(featuresComp);

		{
			this.checkboxTreeViewer = new CheckboxTreeViewer(featuresComp,
					SWT.H_SCROLL | SWT.V_SCROLL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.checkboxTreeViewer.getControl());

			this.checkboxTreeViewer
					.setContentProvider(new FeaturesTreeContentProvider());
			this.checkboxTreeViewer
					.setLabelProvider(new FeatureVersionsLabelProvider());

			// this.checkboxTreeViewer.addSelectionChangedListener(this
			// .getSelectionChangedListener());
			//			
			this.checkboxTreeViewer.addTreeListener(getTreeViewerListener());

		}

		Composite buttonsComp = new Composite(parent, SWT.None);
		buttonsComp.setLayout(new GridLayout(1, false));

		{
			Button install = new Button(buttonsComp, SWT.PUSH);
			install.setText("Install...");

			Button properties = new Button(buttonsComp, SWT.PUSH);
			properties.setText("Properties...");

			// dummy label to make space
			new Label(buttonsComp, SWT.None);

			Button refresh = new Button(buttonsComp, SWT.PUSH);
			refresh.setText("Refresh");

			// dummy label to make space
			new Label(buttonsComp, SWT.None);

			Button manageSites = new Button(buttonsComp, SWT.PUSH);
			manageSites.setText("Manage sites...");

			Button addSite = new Button(buttonsComp, SWT.PUSH);
			addSite.setText("Add site...");

			Button removeSites = new Button(buttonsComp, SWT.PUSH);
			removeSites.setText("Remote sites...");

		}

		// this.initTreeElements();
	}

	/**
	 * This method does create initial tree elements which are basically nothing
	 * than descriptions for bookmarked remote sites
	 */
	protected void initTreeElements() {
		List<UpdateSiteTreeNode> nodes = new ArrayList<UpdateSiteTreeNode>();
		SiteBookmark[] allSiteBookmarks = getAllSiteBookmarks();

		for (SiteBookmark bookmark : allSiteBookmarks) {
			UpdateSiteTreeNode node = new UpdateSiteTreeNode(bookmark);

			/* children for an update site will be added on demand (lazy) */
			DummyTreeNode dummyCategory = new DummyTreeNode(bookmark,
					DummyTreeNode.FEATURE, "Pending...");
			node.addChild(dummyCategory);

			nodes.add(node);
		}

		this.checkboxTreeViewer.setInput(nodes);

	}

	@SuppressWarnings( { "restriction", "unchecked" })
	protected void createTreeElements(
			final ISiteFeatureReference[] searchForFeatures,
			final SiteBookmark bookmark, final UpdateSiteTreeNode node) {
		final List<IStatus> stateCollector = new ArrayList<IStatus>();

		List<CategoryTreeNode> categoryNodes = createCategoryNodes(
				stateCollector, searchForFeatures);

		node.setChildren((TreeNode[]) categoryNodes
				.toArray(new TreeNode[categoryNodes.size()]));

		refreshViewer(node);

		if (!stateCollector.isEmpty()) {
			ErrorView.addError(stateCollector);
		}
	}

	private void refreshViewer(final UpdateSiteTreeNode node) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				checkboxTreeViewer.refresh();
			}
		});
	}

	/*
	 * This method creates category nodes and its children.
	 */
	protected List<CategoryTreeNode> createCategoryNodes(
			List<IStatus> stateCollector,
			ISiteFeatureReference[] searchForFeatures) {
		/* map categories and features */
		List<CategoryTreeNode> categoryNodes = new ArrayList<CategoryTreeNode>();

		for (ISiteFeatureReference siteFeatureReference : searchForFeatures) {

			try {
				IFeature feature = siteFeatureReference.getFeature(null);

				CategoryTreeNode categoryNode = new CategoryTreeNode(feature);
				if (categoryNodes.contains(categoryNode)) {
					// get the category node object from list
					categoryNode = getCategoryNode(categoryNodes, categoryNode);
					FeatureTreeNode featureTreeNode = new FeatureTreeNode(
							feature);
					categoryNode.addChild(featureTreeNode);

				} else {
					FeatureTreeNode featureNode = new FeatureTreeNode(feature);
					categoryNode.addChild(featureNode);

					categoryNodes.add(categoryNode);
				}

			} catch (CoreException e) {
				IStatus error = new Status(Status.ERROR,
						ProvisioningActivator.PLUGIN_ID,
						"Unable to retrieve feature from siteFeatureReference",
						e);
				stateCollector.add(error);
			}

		}

		return categoryNodes;
	}

	protected CategoryTreeNode getCategoryNode(
			List<CategoryTreeNode> categoryNodes, CategoryTreeNode categoryNode) {
		for (CategoryTreeNode node : categoryNodes) {
			if (node.equals(categoryNode)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * TODO: This method a real performance killer, try to find another way to
	 * get local bookmarks for update sites.
	 * 
	 * @return
	 */
	@SuppressWarnings("restriction")
	private SiteBookmark[] getAllSiteBookmarks() {
		DiscoveryFolder discoveryFolder = new DiscoveryFolder();
		Object[] sitesToVisit = discoveryFolder.getChildren(discoveryFolder);
		UpdateModel model = UpdateUI.getDefault().getUpdateModel();
		Object[] bookmarks = model.getBookmarkLeafs();
		SiteBookmark[] all = new SiteBookmark[bookmarks.length
				+ sitesToVisit.length];
		System.arraycopy(bookmarks, 0, all, 0, bookmarks.length);
		System.arraycopy(sitesToVisit, 0, all, bookmarks.length,
				sitesToVisit.length);
		return all;
	}

	protected ITreeViewerListener getTreeViewerListener() {
		return new ITreeViewerListener() {

			public void treeCollapsed(TreeExpansionEvent event) {
				// do nothing
			}

			public void treeExpanded(TreeExpansionEvent event) {
				if (event.getElement() instanceof UpdateSiteTreeNode) {
					final UpdateSiteTreeNode node = (UpdateSiteTreeNode) event
							.getElement();
					final SiteBookmark bookmark = (SiteBookmark) node
							.getValue();

					Job createCategoryNodesJob = new Job(
							"Create category nodes") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {

							ISiteFeatureReference[] searchForFeatures = searchForFeatures(bookmark);

							createTreeElements(searchForFeatures, bookmark,
									node);
							return Status.OK_STATUS;
						}
					};
					createCategoryNodesJob.setSystem(true);
					createCategoryNodesJob.schedule();
				}
			}

		};
	}

	protected ISiteFeatureReference[] searchForFeatures(
			final SiteBookmark bookmark) {
		ISiteFeatureReference[] featureReferences = null;
		final URL remoteSiteURL = bookmark.getURL();
		try {
			featureReferences = SiteManager.getSite(remoteSiteURL, null)
					.getFeatureReferences();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return featureReferences;
	}

	/**
	 * Label Provider for the installed features table
	 * 
	 * @author eugrei
	 * 
	 */
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

	private Display getDisplay() {
		if (Display.getCurrent() == null) {
			return Display.getDefault();
		} else {
			return Display.getCurrent();
		}
	}
}
