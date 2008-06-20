package org.remotercp.provisioning.editor.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.update.core.IFeature;
import org.osgi.framework.Bundle;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class InstalledFeaturesComposite {

	private TableViewer tableViewer;

	private TableViewer diverseFeaturesViewer;

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
							.setLabelProvider(new InstalledFeaturesLabelProvider());
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
						this.diverseFeaturesViewer = new TableViewer(
								differentFeaturesGroup, SWT.H_SCROLL
										| SWT.V_SCROLL | SWT.SINGLE);
						GridDataFactory
								.fillDefaults()
								.grab(true, true)
								.applyTo(
										this.diverseFeaturesViewer.getControl());
						this.diverseFeaturesViewer
								.setContentProvider(new ArrayContentProvider());
						this.diverseFeaturesViewer
								.setLabelProvider(new InstalledFeaturesLabelProvider());
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
										| SWT.V_SCROLL);
						GridDataFactory.fillDefaults().grab(true, true)
								.applyTo(
										this.userWithDifferentFeaturesViewer
												.getControl());
						this.userWithDifferentFeaturesViewer
								.setContentProvider(new DifferentFeaturesContentprovider());
						this.userWithDifferentFeaturesViewer
								.setLabelProvider(new DifferenFeaturesLabelProvider());
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
	public void setInput(Object input) {
		this.tableViewer.setInput(input);
	}

	/**
	 * Label Provider for the installed features table
	 * 
	 * @author eugrei
	 * 
	 */
	private class InstalledFeaturesLabelProvider implements ITableLabelProvider {

		 private Image plugin = ProvisioningActivator.getImageDescriptor(
				ImageKeys.PLUGIN).createImage();

		private Image feature = ProvisioningActivator.getImageDescriptor(
				ImageKeys.FEATURE).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			Image image = null;
			 if (element instanceof IFeature) {
			 image = feature;
			 }
			 if (element instanceof Bundle) {
			 image = plugin;
			 }
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IFeature) {
				IFeature feature = (IFeature) element;
				return feature.getVersionedIdentifier().getIdentifier();
			}
			if (element instanceof Bundle) {
				Bundle bundle = (Bundle) element;
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

	private class DifferentFeaturesContentprovider implements
			ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}

		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			return null;
		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}
	}

	private class DifferenFeaturesLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getText(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
