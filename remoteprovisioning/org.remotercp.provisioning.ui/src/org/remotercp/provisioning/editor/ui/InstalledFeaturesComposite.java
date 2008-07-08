package org.remotercp.provisioning.editor.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesUserTreeNode;
import org.remotercp.provisioning.editor.ui.tree.DifferentFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTableLabelProvider;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeLabelProvider;
import org.remotercp.provisioning.images.ImageKeys;

public class InstalledFeaturesComposite {

	private TreeViewer commonFeaturesViewer;

	private TreeViewer differentFeaturesViewer;

	private Group commonFeaturesGroup;

	private Group differentFeaturesGroup;

	private Button checkForUpdates;

	private Button uninstall;

	private Button options;

	private Composite main;

	private SashForm sashMain;

	private Image checkUpdatesImage;

	private Image uninstallImage;

	private Image propertiesImage;

	public static enum Buttons {
		CHECK_FOR_UPDATES, UNINSTALL, OPTIONS
	};

	public InstalledFeaturesComposite(Composite parent, int style) {
		this.checkUpdatesImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.UPDATE).createImage();
		this.uninstallImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.UNINSTALL).createImage();
		this.propertiesImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.PROPERTIES).createImage();

		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		sashMain = new SashForm(parent, SWT.HORIZONTAL);
		sashMain.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sashMain);

		// left composite
		main = new Composite(sashMain, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			/*
			 * Sash for installed features, different features
			 */
			SashForm installedFeaturesSash = new SashForm(main, SWT.VERTICAL);
			installedFeaturesSash.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					installedFeaturesSash);

			{
				commonFeaturesGroup = new Group(installedFeaturesSash, SWT.None);
				commonFeaturesGroup.setText("Common features");
				commonFeaturesGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						commonFeaturesGroup);
				{
					/*
					 * installed features tree viewer
					 */
					this.commonFeaturesViewer = new TreeViewer(
							commonFeaturesGroup, SWT.H_SCROLL | SWT.V_SCROLL
									| SWT.MULTI);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.commonFeaturesViewer.getControl());
					this.commonFeaturesViewer
							.setContentProvider(new FeaturesTreeContentProvider());
					ILabelDecorator decorator = PlatformUI.getWorkbench()
							.getDecoratorManager().getLabelDecorator();
					ILabelProvider provider = new FeaturesTableLabelProvider();
					this.commonFeaturesViewer
							.setLabelProvider(new FeaturesTreeLabelProvider(
									provider, decorator));

					Tree tree = this.commonFeaturesViewer.getTree();

					TreeColumn name = new TreeColumn(tree, SWT.LEFT);
					name.setText("Feature");
					name.setWidth(200);

					TreeColumn version = new TreeColumn(tree, SWT.LEFT);
					version.setText("Version");
					version.setWidth(200);

					tree.setLinesVisible(true);
					tree.setHeaderVisible(true);

				}
			}
			{

				differentFeaturesGroup = new Group(installedFeaturesSash,
						SWT.None);
				differentFeaturesGroup.setText("Different features");
				differentFeaturesGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						differentFeaturesGroup);
				{

					/*
					 * different features of selected features will be shown
					 * here
					 */
					this.differentFeaturesViewer = new TreeViewer(
							differentFeaturesGroup, SWT.H_SCROLL | SWT.V_SCROLL);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.differentFeaturesViewer.getControl());

					this.differentFeaturesViewer
							.setContentProvider(new FeaturesTreeContentProvider());
					ILabelDecorator decorator = PlatformUI.getWorkbench()
							.getDecoratorManager().getLabelDecorator();
					ILabelProvider provider = new FeaturesTableLabelProvider();
					this.differentFeaturesViewer
							.setLabelProvider(new FeaturesTreeLabelProvider(
									provider, decorator));

					Tree tree = this.differentFeaturesViewer.getTree();

					TreeColumn name = new TreeColumn(tree, SWT.LEFT);
					name.setText("Feature");
					name.setWidth(200);

					TreeColumn version = new TreeColumn(tree, SWT.LEFT);
					version.setText("Version");
					version.setWidth(200);

					tree.setLinesVisible(true);
					tree.setHeaderVisible(true);
				}
			}
			installedFeaturesSash.setWeights(new int[] { 2, 1 });
		}

		{
			Composite installedFeaturesButtonsComposite = new Composite(
					sashMain, SWT.None);
			installedFeaturesButtonsComposite
					.setLayout(new GridLayout(1, false));

			checkForUpdates = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			checkForUpdates.setText("Check for updates...");
			checkForUpdates.setImage(checkUpdatesImage);

			options = new Button(installedFeaturesButtonsComposite, SWT.PUSH);
			options.setText("Options");
			options.setImage(propertiesImage);
			
			// space label
			new Label(installedFeaturesButtonsComposite, SWT.None);
			
			uninstall = new Button(installedFeaturesButtonsComposite, SWT.PUSH);
			uninstall.setText("Uninstall");
			uninstall.setImage(uninstallImage);

		}

		sashMain.setWeights(new int[] { 75, 25 });
	}

	protected void addButtonListener(SelectionAdapter listener, Buttons button) {
		switch (button) {
		case CHECK_FOR_UPDATES:
			this.checkForUpdates.addSelectionListener(listener);
			break;
		case UNINSTALL:
			this.uninstall.addSelectionListener(listener);
			break;
		case OPTIONS:
			this.options.addSelectionListener(listener);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	protected Set<CommonFeaturesTreeNode> getInstallableUnits() {
		IStructuredSelection selection = (IStructuredSelection) this.commonFeaturesViewer
				.getSelection();
		/*
		 * selection can be:
		 * 
		 * 1. one or more CommonFeaturesTreeNodes
		 * 
		 * 1. one or more CommonFeaturesUserTreeNodes
		 */
		Set<CommonFeaturesTreeNode> commonFeatureNodes = new TreeSet<CommonFeaturesTreeNode>();

		Iterator<TreeNode> iter = selection.iterator();
		while (iter.hasNext()) {
			TreeNode treeNode = (TreeNode) iter.next();
			// feature selected for update
			if (treeNode instanceof CommonFeaturesTreeNode) {
				CommonFeaturesTreeNode node = (CommonFeaturesTreeNode) treeNode;
				commonFeatureNodes.add(node);
			}

			/*
			 * single users selected for update. If e.g. only two of tree user
			 * in a CommonFeaturesTreeNode have been selected for an update
			 * create a new CommonFeatureTreeNode and accumulate this users
			 * under the new node.
			 */
			// TODO: this doesn't work properly! Fixe bug
			if (treeNode instanceof CommonFeaturesUserTreeNode) {
				CommonFeaturesUserTreeNode userTreeNode = (CommonFeaturesUserTreeNode) treeNode;
				SerializedFeatureWrapper feature = (SerializedFeatureWrapper) userTreeNode
						.getValue();
				CommonFeaturesTreeNode parent = (CommonFeaturesTreeNode) userTreeNode
						.getParent();
				if (commonFeatureNodes.contains(parent)) {
					parent.addChild(userTreeNode);
				} else {
					CommonFeaturesTreeNode commonNode = new CommonFeaturesTreeNode(
							feature);
					commonNode.addChild(userTreeNode);
					commonFeatureNodes.add(commonNode);
				}
			}
		}

		// for (CommonFeaturesTreeNode node : commonFeatureNodes) {
		// System.out.println("Tree node: "
		// + ((SerializedFeatureWrapper) node.getValue()).getLabel());
		// System.out.println("Children: ");
		// for (CommonFeaturesUserTreeNode child : node.getChildren()) {
		// System.out.println(child.getUserId().getName());
		// }
		// }

		return commonFeatureNodes;
	}

	/**
	 * Sets the table viewer input
	 * 
	 * @param input
	 */
	public void setCommonFeaturesInput(Collection<CommonFeaturesTreeNode> input) {
		this.commonFeaturesGroup.setText("Common features: " + input.size());
		this.commonFeaturesViewer.setInput(input);
	}

	public void setDifferentFeaturesInput(
			Collection<DifferentFeaturesTreeNode> input) {

		this.differentFeaturesGroup.setText("Different features:"
				+ input.size());
		this.differentFeaturesViewer.setInput(input);
	}

	protected Control getMainControl() {
		return sashMain;
	}
}
