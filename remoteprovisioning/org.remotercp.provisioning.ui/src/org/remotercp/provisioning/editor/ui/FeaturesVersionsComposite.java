package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.editor.FeaturesTreeContentProvider;
import org.remotercp.provisioning.images.ImageKeys;

public class FeaturesVersionsComposite {

	private TreeViewer featureVersionsViewer;

	private static final int COLUMN_NAME = 0;

	private static final int COLUMN_VERSION = 1;

	private Composite main;

	public FeaturesVersionsComposite(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		{
			this.featureVersionsViewer = new TreeViewer(main, SWT.V_SCROLL
					| SWT.H_SCROLL);

			Tree tree = this.featureVersionsViewer.getTree();
			tree.setHeaderVisible(true);
			tree.setLinesVisible(true);

			GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);

			TreeColumn featureName = new TreeColumn(tree, SWT.LEFT);
			featureName.setText("Name");
			featureName.setWidth(300);

			TreeColumn featureVersion = new TreeColumn(tree, SWT.LEFT);
			featureVersion.setText("Version");
			featureVersion.setWidth(200);

			this.featureVersionsViewer
					.setContentProvider(new FeaturesTreeContentProvider());
			ILabelDecorator decorator = PlatformUI.getWorkbench()
					.getDecoratorManager().getLabelDecorator();
			this.featureVersionsViewer
					.setLabelProvider(new FeatureVersionsLabelProvider(
							new FeaturesVersionTableLabelProvider(), decorator));
		}
	}

	protected Control getMainControl() {
		return main;
	}

	protected void setInput(
			Collection<SerializedFeatureWrapper> selectedFeatures) {

		Collection<TreeNode> featuresNodes = new ArrayList<TreeNode>();
		for (SerializedFeatureWrapper feature : selectedFeatures) {
			FeaturesTreeNode node = new FeaturesTreeNode(feature);

			FeaturesTreeNode child = new FeaturesTreeNode("Pending...");
			child.setParent(node);

			node.setChildren(new FeaturesTreeNode[] { child });

			featuresNodes.add(node);
		}
		this.featureVersionsViewer.setInput(featuresNodes);

		this.featureVersionsViewer.expandAll();
	}

	private class FeaturesTreeNode extends TreeNode {

		private boolean checked = false;

		public FeaturesTreeNode(Object value) {
			super(value);
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

	}

	private class FeatureVersionsLabelProvider extends DecoratingLabelProvider
			implements ITableLabelProvider {

		private final ITableLabelProvider provider;
		private final ILabelDecorator decorator;

		public FeatureVersionsLabelProvider(ILabelProvider provider,
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
				if (element instanceof FeaturesTreeNode) {
					FeaturesTreeNode node = (FeaturesTreeNode) element;

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
				if (element instanceof FeaturesTreeNode) {
					FeaturesTreeNode node = (FeaturesTreeNode) element;
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
}
