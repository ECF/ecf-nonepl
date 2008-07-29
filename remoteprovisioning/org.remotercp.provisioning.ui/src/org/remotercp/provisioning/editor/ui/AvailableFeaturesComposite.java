package org.remotercp.provisioning.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.update.core.Feature;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.internal.ui.UpdateUI;
import org.eclipse.update.internal.ui.UpdateUIMessages;
import org.eclipse.update.internal.ui.model.DiscoveryFolder;
import org.eclipse.update.internal.ui.model.SiteBookmark;
import org.eclipse.update.internal.ui.model.UpdateModel;
import org.eclipse.update.internal.ui.wizards.NewUpdateSiteDialog;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.constants.UpdateConstants;
import org.remotercp.common.provisioning.IInstallFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.progress.handler.ProgressViewHandler;
import org.remotercp.provisioning.ProvisioningActivator;
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

public class AvailableFeaturesComposite {

	private CheckboxTreeViewer bookmarksTreeViewer;
	private Shell shell;
	private final ID[] userIDs;
	private ISessionService sessionService;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public AvailableFeaturesComposite(SashForm parent, int style, ID[] userIDs) {
		this.userIDs = userIDs;
		this.init();

		this.createPartControl(parent, style);
		this.shell = parent.getShell();
	}

	private void init() {
		sessionService = OsgiServiceLocatorUtil
				.getOSGiService(ProvisioningActivator.getBundleContext(),
						ISessionService.class);
		Assert.isNotNull(sessionService);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/*
	 * Creates all GUI elements
	 */
	private void createPartControl(SashForm parent, int style) {

		Composite featuresComp = new Composite(parent, SWT.None);
		featuresComp.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(featuresComp);

		{
			this.bookmarksTreeViewer = new CheckboxTreeViewer(featuresComp,
					SWT.H_SCROLL | SWT.V_SCROLL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.bookmarksTreeViewer.getControl());

			this.bookmarksTreeViewer
					.setContentProvider(new FeaturesTreeContentProvider());
			this.bookmarksTreeViewer
					.setLabelProvider(new FeatureVersionsLabelProvider());

			this.bookmarksTreeViewer.addTreeListener(getTreeViewerListener());

		}

		Composite buttonsComp = new Composite(parent, SWT.None);
		buttonsComp.setLayout(new GridLayout(1, false));

		{
			Button install = new Button(buttonsComp, SWT.PUSH);
			install.setText("Install...");
			install.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					installFeature();
				}
			});

			Button properties = new Button(buttonsComp, SWT.PUSH);
			properties.setText("Properties...");

			// dummy label to make space
			new Label(buttonsComp, SWT.None);

			Button refresh = new Button(buttonsComp, SWT.PUSH);
			refresh.setText("Refresh");
			refresh.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					initTreeElements();
				}
			});

			// dummy label to make space
			new Label(buttonsComp, SWT.None);

			Button addSite = new Button(buttonsComp, SWT.PUSH);
			addSite.setText("Add site...");
			addSite.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					addBookmarkSite();
				}

			});

			Button removeSites = new Button(buttonsComp, SWT.PUSH);
			removeSites.setText("Remove site...");
			removeSites.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeBookmarkSite();
				}

			});

		}

		// this.initTreeElements();
	}

	/**
	 * This method does create initial tree elements which are basically nothing
	 * than descriptions for bookmarked remote sites.
	 */
	@SuppressWarnings("restriction")
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

		this.bookmarksTreeViewer.setInput(nodes);

	}

	@SuppressWarnings( { "restriction", "unchecked" })
	protected void createCategoryTreeElements(
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

				bookmarksTreeViewer.refresh();
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

	@SuppressWarnings("restriction")
	private void addBookmarkSite() {
		NewUpdateSiteDialog updateSiteDialog = new NewUpdateSiteDialog(shell,
				getAllSiteBookmarks());
		updateSiteDialog.create();
		updateSiteDialog.getShell().setText("Create new update site");
		updateSiteDialog.open();

		// add the new bookmark to treeviewer
		this.initTreeElements();
	}

	/*
	 * XXX: this code has been copied from
	 * org.eclipse.update.internal.ui.wizards.SitePage
	 */
	private void removeBookmarkSite() {
		BusyIndicator.showWhile(bookmarksTreeViewer.getControl().getDisplay(),
				new Runnable() {
					@SuppressWarnings("restriction")
					public void run() {
						UpdateModel updateModel = UpdateUI.getDefault()
								.getUpdateModel();
						Object[] checkedElements = bookmarksTreeViewer
								.getCheckedElements();

						// remove all selected bookmarks
						for (Object checkedElement : checkedElements) {
							if (checkedElement instanceof UpdateSiteTreeNode) {
								UpdateSiteTreeNode node = (UpdateSiteTreeNode) checkedElement;

								SiteBookmark bookmark = (SiteBookmark) node
										.getValue();
								String selName = bookmark.getLabel();
								boolean answer = MessageDialog
										.openQuestion(
												bookmarksTreeViewer
														.getControl()
														.getShell(),
												UpdateUIMessages.SitePage_remove_location_conf_title,
												UpdateUIMessages.SitePage_remove_location_conf
														+ " " + selName); //$NON-NLS-1$

								if (answer && !bookmark.isReadOnly()) {
									updateModel.removeBookmark(bookmark);
								}
							}
						}
					}
				});

		// refresh the treeviewer
		this.initTreeElements();
	}

	private void installFeature() {
		final Shell shell = bookmarksTreeViewer.getControl().getShell();
		final Object[] checkedElements = bookmarksTreeViewer
				.getCheckedElements();

		if (checkedElements.length > 1) {
			MessageBox error = new MessageBox(shell, SWT.ICON_WARNING);
			error
					.setMessage("The installation of several features is not supported yet");
			error.open();
		} else if (!(checkedElements[0] instanceof FeatureTreeNode)) {
			MessageBox error = new MessageBox(shell, SWT.ICON_WARNING);
			error.setMessage("You have not selected a feature to install.");
			error.open();
		} else {
			// get the feature to install
			final FeatureTreeNode featureNode = (FeatureTreeNode) checkedElements[0];
			final Feature feature = (Feature) featureNode.getValue();

			ResultFeatureTreeNode resultFeatureNode = new ResultFeatureTreeNode(
					SerializedFeatureWrapper.createFeatureWrapper(feature));
			for (ID userId : userIDs) {
				performInstallation(feature, userId, resultFeatureNode);
			}

			// XXX: this method will support more than one feature installation.
			// results have to be collected in a list
			List<ResultFeatureTreeNode> resultNodes = new ArrayList<ResultFeatureTreeNode>();
			resultNodes.add(resultFeatureNode);

			// send result to listener
			pcs.firePropertyChange(UpdateConstants.INSTALL, null, resultNodes);
		}
	}

	/*
	 * This method will install one feature on a remote user's machine
	 */
	private void performInstallation(final Feature feature, final ID userId,
			final ResultFeatureTreeNode resultFeatureNode) {
		try {
			List<IInstallFeaturesService> remoteServices = sessionService
					.getRemoteService(IInstallFeaturesService.class,
							new ID[] { userId }, null);

			// there should be only one service per user available
			Assert.isTrue(remoteServices.size() == 1);

			final IInstallFeaturesService installFeatureService = remoteServices
					.get(0);

			Job installJob = new Job("Install feature") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// TODO: show real progress if possible
					monitor.beginTask(
							"Perform install operation on remote user machine: "
									+ userId.getName(),
							IProgressMonitor.UNKNOWN);

					/* as IFeature is not serializable we have to wrap them */
					SerializedFeatureWrapper[] featuresToInstall = new SerializedFeatureWrapper[1];
					featuresToInstall[0] = SerializedFeatureWrapper
							.createFeatureWrapper(feature);

					List<IStatus> results = installFeatureService
							.installFeatures(featuresToInstall);

					ResultUserTreeNode userNode = new ResultUserTreeNode(userId);
					userNode.setUpdateResults(results);

					// add node to parent
					resultFeatureNode.addChild(userNode);

					monitor.done();

					// always return ok, errors will be handled in the
					// ProgressReportComposite
					return Status.OK_STATUS;

				}
			};
			// display progress in progress bar
			installJob.setUser(false);
			installJob.schedule();

			// set focus
			ProgressViewHandler.setFocus();
		} catch (ECFException e) {
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
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
							createCategoryTreeElements(searchForFeatures,
									bookmark, node);
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
