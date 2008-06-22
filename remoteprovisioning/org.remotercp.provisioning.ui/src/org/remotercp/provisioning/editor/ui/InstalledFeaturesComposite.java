package org.remotercp.provisioning.editor.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.update.core.IFeature;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class InstalledFeaturesComposite {

	private TableViewer tableViewer;

	private TableViewer differentFeaturesViewer;

	private TreeViewer userWithDifferentFeaturesViewer;

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
					this.tableViewer = new TableViewer(commonFeaturesGroup,
							SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.tableViewer.getControl());
					this.tableViewer
							.setContentProvider(new ArrayContentProvider());
					this.tableViewer
							.setLabelProvider(new FeaturesLabelProvider());
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
						this.differentFeaturesViewer = new TableViewer(
								differentFeaturesGroup, SWT.H_SCROLL
										| SWT.V_SCROLL | SWT.SINGLE);
						GridDataFactory.fillDefaults().grab(true, true)
								.applyTo(
										this.differentFeaturesViewer
												.getControl());
						this.differentFeaturesViewer
								.setContentProvider(new ArrayContentProvider());
						this.differentFeaturesViewer
								.setLabelProvider(new FeaturesLabelProvider());
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
						this.userWithDifferentFeaturesViewer = new TreeViewer(
								userFordifferentFeaturesGroup, SWT.H_SCROLL
										| SWT.V_SCROLL | SWT.SINGLE);
						GridDataFactory.fillDefaults().grab(true, true)
								.applyTo(
										this.userWithDifferentFeaturesViewer
												.getControl());

						// XXX this line causes an error???
						// this.userWithDifferentFeaturesViewer
						// .setContentProvider(new ArrayContentProvider());
						this.userWithDifferentFeaturesViewer
								.setLabelProvider(new FeaturesLabelProvider());
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

	/**
	 * Sets the table viewer input
	 * 
	 * @param input
	 */
	public void setInstalledInput(Object input) {
		this.tableViewer.setInput(input);
	}

	public void setDifferentInput(Object input) {
		this.differentFeaturesViewer.setInput(input);
	}

	public void setUserInput(Object input) {
		this.userWithDifferentFeaturesViewer.setInput(input);
	}

	/**
	 * Label Provider for the installed features table
	 * 
	 * @author eugrei
	 * 
	 */
	private class FeaturesLabelProvider implements ITableLabelProvider {

		private Image plugin = ProvisioningActivator.getImageDescriptor(
				ImageKeys.PLUGIN).createImage();

		private Image feature = ProvisioningActivator.getImageDescriptor(
				ImageKeys.FEATURE).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			if (element instanceof IFeature) {
				image = feature;
			}
			if (element instanceof SerializedBundleWrapper) {
				image = plugin;
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IFeature) {
				IFeature feature = (IFeature) element;
				return feature.getVersionedIdentifier().getIdentifier();
			}
			if (element instanceof SerializedBundleWrapper) {
				SerializedBundleWrapper bundle = (SerializedBundleWrapper) element;
				return bundle.getSymbolicName();
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// ignore

		}

		// free ressources
		public void dispose() {
			this.plugin.dispose();
			this.plugin = null;

			this.feature.dispose();
			this.feature = null;
		}

		public boolean isLabelProperty(Object element, String property) {
			// ignore
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// ignore

		}

	}

}
