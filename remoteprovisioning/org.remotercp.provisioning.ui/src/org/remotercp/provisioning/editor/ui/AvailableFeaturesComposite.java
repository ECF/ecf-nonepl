package org.remotercp.provisioning.editor.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.internal.ui.UpdateUI;
import org.eclipse.update.internal.ui.model.DiscoveryFolder;
import org.eclipse.update.internal.ui.model.SiteBookmark;
import org.eclipse.update.internal.ui.model.UpdateModel;
import org.remotercp.provisioning.ProvisioningActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class AvailableFeaturesComposite {

	private CheckboxTreeViewer checkboxTreeViewer;

	public AvailableFeaturesComposite(SashForm parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {

		Composite featuresComp = new Composite(parent, SWT.None);
		featuresComp.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(featuresComp);

		{
			this.checkboxTreeViewer = new CheckboxTreeViewer(featuresComp,
					SWT.H_SCROLL | SWT.V_SCROLL);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.checkboxTreeViewer.getControl());
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
	}

	private SiteBookmark[] getAllSiteBookmarks() {
		DiscoveryFolder discoveryFolder = new DiscoveryFolder();
		UpdateModel model = UpdateUI.getDefault().getUpdateModel();
		Object[] bookmarks = model.getBookmarkLeafs();
		Object[] sitesToVisit = discoveryFolder.getChildren(discoveryFolder);
		SiteBookmark[] all = new SiteBookmark[bookmarks.length
				+ sitesToVisit.length];
		System.arraycopy(bookmarks, 0, all, 0, bookmarks.length);
		System.arraycopy(sitesToVisit, 0, all, bookmarks.length,
				sitesToVisit.length);
		return all;
	}

	private class TreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {

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
			// do nothing

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing

		}

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
