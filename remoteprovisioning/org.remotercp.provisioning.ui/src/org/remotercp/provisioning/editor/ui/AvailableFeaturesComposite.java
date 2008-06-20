package org.remotercp.provisioning.editor.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.update.core.IFeature;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class AvailableFeaturesComposite {

	private TableViewer tableViewer;

	public AvailableFeaturesComposite(SashForm parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {

		Composite featuresComp = new Composite(parent, SWT.None);
		featuresComp.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(featuresComp);

		{
			this.tableViewer = new TableViewer(featuresComp, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.MULTI);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.tableViewer.getControl());
			this.tableViewer.setContentProvider(new ArrayContentProvider());
			this.tableViewer
					.setLabelProvider(new AvailableFeaturesLabelProvider());
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

	}

	protected void setInput(Object input) {
		this.tableViewer.setInput(input);
	}

	/**
	 * Label Provider for the installed features table
	 * 
	 * @author eugrei
	 * 
	 */
	private class AvailableFeaturesLabelProvider implements ITableLabelProvider {

		private Image pluginImage = ProvisioningActivator.getImageDescriptor(
				ImageKeys.PLUGIN).createImage();

		public Image getColumnImage(Object element, int columnIndex) {
			return pluginImage;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IFeature) {
				IFeature feature = (IFeature) element;
				return feature.getVersionedIdentifier().getIdentifier();
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// ignore

		}

		public void dispose() {
			if (this.pluginImage != null && !this.pluginImage.isDisposed()) {
				this.pluginImage.dispose();
				this.pluginImage = null;
			}

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
