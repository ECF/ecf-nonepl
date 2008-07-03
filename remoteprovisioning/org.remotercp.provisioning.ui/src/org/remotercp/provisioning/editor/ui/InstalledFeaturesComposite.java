package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.DifferentFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTableLabelProvider;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.ui.tree.FeaturesTreeLabelProvider;

public class InstalledFeaturesComposite {

	private TreeViewer commonFeaturesViewer;

	private TreeViewer differentFeaturesViewer;

	private Group commonFeaturesGroup;

	private Group differentFeaturesGroup;

	private Button checkForUpdates;

	private Button uninstall;

	private Button options;

	private Composite main;

	public static enum Buttons {
		CHECK_FOR_UPDATES, UNINSTALL, OPTIONS
	};

	public InstalledFeaturesComposite(SashForm parent, int style) {

		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {
		main = new Composite(parent, SWT.None);
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
					this.commonFeaturesViewer = new TreeViewer(commonFeaturesGroup,
							SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
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
			Composite installedFeaturesButtonsComposite = new Composite(parent,
					SWT.None);
			installedFeaturesButtonsComposite
					.setLayout(new GridLayout(1, false));

			checkForUpdates = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			checkForUpdates.setText("Check for updates...");

			uninstall = new Button(installedFeaturesButtonsComposite, SWT.PUSH);
			uninstall.setText("Uninstall");

			options = new Button(installedFeaturesButtonsComposite, SWT.PUSH);
			options.setText("Options");
		}
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

	protected Collection<SerializedFeatureWrapper> getSelectedFeatures() {
		IStructuredSelection selection = (IStructuredSelection) this.commonFeaturesViewer
				.getSelection();
		Object[] selectedElements = selection.toArray();

		Collection<SerializedFeatureWrapper> selectedFeatures = new ArrayList<SerializedFeatureWrapper>();
		if (selectedElements.length > 0) {
			for (int feature = 0; feature < selectedElements.length; feature++) {
				selectedFeatures
						.add((SerializedFeatureWrapper) selectedElements[feature]);
			}
		}

		return selectedFeatures;
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
		return main;
	}
}
