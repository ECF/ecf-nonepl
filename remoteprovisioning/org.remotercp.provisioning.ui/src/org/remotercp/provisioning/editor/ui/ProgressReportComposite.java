package org.remotercp.provisioning.editor.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.dialogs.OperationReportWizard;
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
						SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

				this.resultTreeViewer
						.addDoubleClickListener(new IDoubleClickListener() {
							public void doubleClick(DoubleClickEvent event) {
								showOperationResult(event);
							}
						});

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

				this.resultTreeViewer
						.addDoubleClickListener(new IDoubleClickListener() {

							public void doubleClick(DoubleClickEvent event) {
								showStatus(event);
							}
						});
			}
		}

	}

	private void showStatus(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof ResultUserTreeNode) {
			ResultUserTreeNode userNode = (ResultUserTreeNode) element;
			List<IStatus> updateResults = userNode.getUpdateResults();

			// open a dialog for each status message
			for (IStatus status : updateResults) {
				if (status.isOK()) {
					MessageBox okMessage = new MessageBox(this.main.getShell(),
							SWT.ICON_INFORMATION);
					okMessage.setMessage(status.getMessage());
					okMessage.open();
				} else if (status.getSeverity() == Status.ERROR
						|| status.getSeverity() == Status.CANCEL) {
					MessageBox errorBox = new MessageBox(this.main.getShell(),
							SWT.ICON_ERROR);
					errorBox.setMessage(status.getMessage());
					errorBox.open();
				} else {
					MessageBox unknown = new MessageBox(this.main.getShell(),
							SWT.ICON_INFORMATION);
					unknown.setMessage(status.getMessage());
					unknown.open();
				}
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
	 * This method opens a dialog where the result of an operation will be
	 * displayed.
	 * 
	 * @param event The object that has been selected. This object is a column
	 * in the result table.
	 */
	private void showOperationResult(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();

		if (selection.getFirstElement() instanceof ResultUserTreeNode) {
			ResultUserTreeNode node = (ResultUserTreeNode) selection
					.getFirstElement();
			List<IStatus> updateResults = node.getUpdateResults();

			Display display = this.resultTreeViewer.getTree().getDisplay();
			Shell shell = new Shell(display);

			OperationReportWizard wizard = new OperationReportWizard(
					updateResults);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			// dialog.create();
			dialog.open();
		}
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
						image = error;
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
						text = "SUCCESSFUL";
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
