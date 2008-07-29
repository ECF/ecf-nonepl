package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.ui.tree.nodes.ResultFeatureTreeNode;
import org.remotercp.provisioning.editor.ui.tree.nodes.ResultUserTreeNode;
import org.remotercp.provisioning.images.ImageKeys;
import org.remotercp.util.status.StatusUtil;

public class ProgressReportComposite {

	private Composite main;

	private TreeViewer resultTreeViewer;

	public ProgressReportComposite(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			Group resultGroup = new Group(main, SWT.None);
			resultGroup.setText("Update summary");
			resultGroup.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true)
					.applyTo(resultGroup);

			{

				this.resultTreeViewer = new TreeViewer(resultGroup,
						SWT.H_SCROLL | SWT.V_SCROLL);
				Tree tree = resultTreeViewer.getTree();

				GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);
				this.resultTreeViewer
						.setContentProvider(new FeaturesTreeContentProvider());
				ILabelDecorator decorator = PlatformUI.getWorkbench()
						.getDecoratorManager().getLabelDecorator();
				this.resultTreeViewer
						.setLabelProvider(new ResultDecoratingLabelProvider(
								new ResultTableLabelProvider(), decorator));

				TreeColumn feature = new TreeColumn(tree, SWT.LEFT);
				feature.setText("Feature");
				feature.setWidth(200);

				TreeColumn version = new TreeColumn(tree, SWT.LEFT);
				version.setText("Version");
				version.setWidth(200);

				TreeColumn status = new TreeColumn(tree, SWT.LEFT);
				status.setText("Status");
				status.setWidth(200);

				tree.setHeaderVisible(true);
				tree.setLinesVisible(true);

				// XXX For Test only
				// this.resultTreeViewer.setInput(getDummyData());
				// this.resultTreeViewer.expandAll();
			}
		}

	}

	protected Composite getMainControl() {
		return main;
	}

	/*
	 * This method will be called by other composites to provide results, which
	 * are going to be displayed in this composite
	 */
	protected void setInput(final Object input) {
		this.resultTreeViewer.getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						ProgressReportComposite.this.resultTreeViewer
								.setInput(input);
						ProgressReportComposite.this.resultTreeViewer
								.expandAll();
					}
				});
	}

	/*
	 * XXX: for tests only used
	 */
	private List<TreeNode> getDummyData() {
		InstalledFeaturesCompositeTest helper = new InstalledFeaturesCompositeTest();
		helper.setUp();
		ID klaus = helper.getKlaus();
		ID sandra = helper.getSandra();
		ID john = helper.getJohn();

		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		SerializedFeatureWrapper feature1 = new SerializedFeatureWrapper();
		feature1.setIdentifier("org.eclipse.feature12");
		feature1.setLabel("Fature 12");
		feature1.setVersion("1.2.0");

		ResultFeatureTreeNode node1 = new ResultFeatureTreeNode(feature1);

		ResultUserTreeNode child1Node1 = new ResultUserTreeNode(klaus);
		child1Node1.setParent(node1);
		ResultUserTreeNode child2Node1 = new ResultUserTreeNode(sandra);
		child2Node1.setParent(node1);
		ResultUserTreeNode child3Node1 = new ResultUserTreeNode(john);
		child3Node1.setParent(node1);

		TreeNode[] children1 = new TreeNode[3];
		children1[0] = child1Node1;
		children1[1] = child2Node1;
		children1[2] = child3Node1;
		node1.setChildren(children1);

		SerializedFeatureWrapper feature2 = new SerializedFeatureWrapper();
		feature2.setIdentifier("org.eclipse.feature13");
		feature2.setLabel("Feature 13");
		feature2.setVersion("1.1.0");

		ResultFeatureTreeNode node2 = new ResultFeatureTreeNode(feature2);
		ResultUserTreeNode child1Node2 = new ResultUserTreeNode(sandra);
		child1Node2.setParent(node2);
		ResultUserTreeNode child2Node2 = new ResultUserTreeNode(klaus);
		child2Node2.setParent(node2);
		ResultUserTreeNode child3Node2 = new ResultUserTreeNode(john);
		child3Node2.setParent(node2);

		TreeNode[] children2 = new TreeNode[3];
		children2[0] = child1Node2;
		children2[1] = child2Node2;
		children2[2] = child3Node2;
		node2.setChildren(children2);

		SerializedFeatureWrapper feature3 = new SerializedFeatureWrapper();
		feature3.setIdentifier("org.eclipse.feature15");
		feature3.setLabel("Feature 15");
		feature3.setVersion("1.3.0");

		ResultFeatureTreeNode node3 = new ResultFeatureTreeNode(feature3);
		ResultUserTreeNode child1Node3 = new ResultUserTreeNode(john);
		child1Node3.setParent(node3);
		ResultUserTreeNode child2Node3 = new ResultUserTreeNode(sandra);
		child2Node3.setParent(node3);
		ResultUserTreeNode child3Node3 = new ResultUserTreeNode(klaus);
		child3Node3.setParent(node3);

		TreeNode[] children3 = new TreeNode[3];
		children3[0] = child1Node3;
		children3[1] = child2Node3;
		children3[2] = child3Node3;
		node3.setChildren(children3);

		treeNodes.add(node1);
		treeNodes.add(node2);
		treeNodes.add(node3);

		return treeNodes;
	}

	private class ResultDecoratingLabelProvider extends DecoratingLabelProvider
			implements ITableLabelProvider {

		private final ITableLabelProvider provider;
		private final ILabelDecorator decorator;

		public ResultDecoratingLabelProvider(ILabelProvider provider,
				ILabelDecorator decorator) {
			super(provider, decorator);
			this.decorator = decorator;
			this.provider = (ITableLabelProvider) provider;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = provider.getColumnImage(element, columnIndex);
			if (decorator != null) {
				Image decorated = decorator.decorateImage(image, element);
				if (decorated != null) {
					return decorated;
				}
			}
			return image;

		}

		public String getColumnText(Object element, int columnIndex) {
			String text = provider.getColumnText(element, columnIndex);
			if (decorator != null) {
				String decorated = decorator.decorateText(text, element);
				if (decorated != null) {
					return decorated;
				}
			}
			return text;

		}

	}

	private class ResultTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		private final static int COLUMN_NAME = 0;
		private final static int COLUMN_VERSION = 1;
		private final static int COLUMN_STATUS = 2;

		private Image feature = ProvisioningActivator.getImageDescriptor(
				ImageKeys.FEATURE).createImage();

		private Image user = ProvisioningActivator.getImageDescriptor(
				ImageKeys.USER).createImage();

		private Image error = ProvisioningActivator.getImageDescriptor(
				ImageKeys.ERROR).createImage();

		private Image ok = ProvisioningActivator.getImageDescriptor(
				ImageKeys.OK).createImage();

		private Image warn = ProvisioningActivator.getImageDescriptor(
				ImageKeys.WARN).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			switch (columnIndex) {
			case COLUMN_NAME:
				if (element instanceof ResultFeatureTreeNode) {
					image = feature;
				}
				if (element instanceof ResultUserTreeNode) {
					image = user;
				}
				break;
			case COLUMN_STATUS:
				if (element instanceof ResultUserTreeNode) {
					ResultUserTreeNode node = (ResultUserTreeNode) element;
					int result = StatusUtil
							.checkStatus(node.getUpdateResults());

					if (result == Status.OK) {
						image = ok;
					} else if (result == Status.WARNING) {
						image = warn;
					} else if (result == Status.ERROR) {
						image = error;
					} else if (result == Status.CANCEL) {
						image = warn;
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
				if (element instanceof ResultFeatureTreeNode) {
					ResultFeatureTreeNode node = (ResultFeatureTreeNode) element;
					SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
							.getValue();
					text = feature.getLabel();
				}
				if (element instanceof ResultUserTreeNode) {
					ResultUserTreeNode node = (ResultUserTreeNode) element;
					ID userId = (ID) node.getValue();
					text = userId.getName();
				}
				break;
			case COLUMN_VERSION:
				if (element instanceof ResultUserTreeNode) {
					ResultUserTreeNode node = (ResultUserTreeNode) element;
					ResultFeatureTreeNode parent = (ResultFeatureTreeNode) node
							.getParent();
					SerializedFeatureWrapper feature = (SerializedFeatureWrapper) parent
							.getValue();
					text = feature.getVersion();
				}
				break;
			case COLUMN_STATUS:
				if (element instanceof ResultUserTreeNode) {

					ResultUserTreeNode node = (ResultUserTreeNode) element;
					int result = StatusUtil
							.checkStatus(node.getUpdateResults());

					if (result == Status.OK) {
						text = "SUCCESSFULL";
					} else if (result == Status.WARNING) {
						text = "WARNING";
					} else if (result == Status.ERROR) {
						text = "FAILED";
					} else if (result == Status.CANCEL) {
						text = "ABORTED";
					} else {
						text = "UNKNOWN";
					}
				}
				break;
			default:
				break;
			}
			return text;
		}

	}

}
