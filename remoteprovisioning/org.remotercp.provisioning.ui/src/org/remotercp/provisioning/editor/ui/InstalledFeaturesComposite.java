package org.remotercp.provisioning.editor.ui;

import java.util.Collection;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.provisioning.editor.FeaturesLabelProvider;
import org.remotercp.provisioning.editor.UserLabelProvider;

public class InstalledFeaturesComposite {

	private TableViewer artifactsViewer;

	private TableViewer differentFeaturesViewer;

	private TableViewer userWithDifferentFeaturesViewer;

	private Map<SerializedBundleWrapper, Collection<ID>> userWithDifferentFeaturesInput;

	public InstalledFeaturesComposite(SashForm parent, int style) {

		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {
		Composite main = new Composite(parent, SWT.None);
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
				Group commonFeaturesGroup = new Group(installedFeaturesSash,
						SWT.None);
				commonFeaturesGroup.setText("Common features");
				commonFeaturesGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						commonFeaturesGroup);
				{
					/*
					 * installed features tree viewer
					 */
					this.artifactsViewer = createArtifactTableViewer(commonFeaturesGroup);
				}
			}
			{
				/*
				 * composite vor diverse features and user with this features
				 */
				Composite diverseFeaturesAndUser = new Composite(
						installedFeaturesSash, SWT.None);
				diverseFeaturesAndUser.setLayout(new GridLayout(2, true));
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						diverseFeaturesAndUser);

				{
					Group differentFeaturesGroup = new Group(
							diverseFeaturesAndUser, SWT.None);
					differentFeaturesGroup.setText("Different features");
					differentFeaturesGroup.setLayout(new GridLayout(1, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							differentFeaturesGroup);
					{

						/*
						 * different features of selected features will be shown
						 * here
						 */
						this.differentFeaturesViewer = createArtifactTableViewer(differentFeaturesGroup);
						this.differentFeaturesViewer
								.addSelectionChangedListener(new ISelectionChangedListener() {

									public void selectionChanged(
											SelectionChangedEvent event) {
										InstalledFeaturesComposite.this
												.handleDifferentFeaturesSelection();
									}

								});
					}
				}
				{
					Group userFordifferentFeaturesGroup = new Group(
							diverseFeaturesAndUser, SWT.None);
					userFordifferentFeaturesGroup
							.setText("User with different features");
					userFordifferentFeaturesGroup.setLayout(new GridLayout(1,
							false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							userFordifferentFeaturesGroup);
					{
						/*
						 * tree that displays user/user groups with different
						 * features installed
						 */
						this.userWithDifferentFeaturesViewer = new TableViewer(
								userFordifferentFeaturesGroup, SWT.H_SCROLL
										| SWT.V_SCROLL | SWT.SINGLE);
						GridDataFactory.fillDefaults().grab(true, true)
								.applyTo(
										this.userWithDifferentFeaturesViewer
												.getControl());

						this.userWithDifferentFeaturesViewer
								.setContentProvider(new ArrayContentProvider());
						this.userWithDifferentFeaturesViewer
								.setLabelProvider(new UserLabelProvider());
					}
				}

			}
			installedFeaturesSash.setWeights(new int[] { 2, 1 });
		}

		{
			Composite installedFeaturesButtonsComposite = new Composite(parent,
					SWT.None);
			installedFeaturesButtonsComposite
					.setLayout(new GridLayout(1, false));

			Button checkForUpdates = new Button(
					installedFeaturesButtonsComposite, SWT.PUSH);
			checkForUpdates.setText("Check for updates...");

			Button uninstall = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			uninstall.setText("Uninstall");

			// hier können die URLs der Update site und ä. eingesehen und
			// geändert werden
			Button options = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			options.setText("Options");
		}
	}

	/*
	 * creates a table viewer for a given composite
	 */
	private TableViewer createArtifactTableViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				viewer.getControl());
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new FeaturesLabelProvider());

		Table table = viewer.getTable();

		TableColumn artifactName = new TableColumn(table, SWT.LEFT);
		artifactName.setText("Name");
		artifactName.setWidth(300);

		TableColumn artifactVersion = new TableColumn(table, SWT.LEFT);
		artifactVersion.setText("Version");
		artifactVersion.setWidth(150);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		return viewer;
	}

	/**
	 * Sets the table viewer input
	 * 
	 * @param input
	 */
	public void setInstalledInput(Object input) {
		this.artifactsViewer.setInput(input);
	}

	public void setDifferentInput(Object input) {
		this.differentFeaturesViewer.setInput(input);
	}

	public void setUserInput(Map<SerializedBundleWrapper, Collection<ID>> input) {
		this.userWithDifferentFeaturesInput = input;
	}

	private void handleDifferentFeaturesSelection() {
		/*
		 * depending on which bundle has been selected from the "different
		 * bundles group" the user list does change for this bundle
		 */
		IStructuredSelection selection = (IStructuredSelection) this.differentFeaturesViewer
				.getSelection();

		SerializedBundleWrapper bundle = (SerializedBundleWrapper) selection
				.getFirstElement();

		Collection<ID> user = this.userWithDifferentFeaturesInput.get(bundle);

		this.userWithDifferentFeaturesViewer.setInput(user);
	}
}
