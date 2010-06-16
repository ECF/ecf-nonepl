package remotercptestapplication;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.remotercp.provisioning.domain.exception.RemoteOperationException;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;

public class View extends ViewPart implements IInstallServiceListener {
	public static final String ID = "ScottTestApplication.view";

	private TableViewer viewer;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		// Provide the input to the ContentProvider
		viewer.setInput(new String[] {"Nothing to show"});
		
		Activator.getDefault().registerServiceListener(this);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void bindInstallService(IInstallFeaturesService service) {
		System.out.println("View.bindInstallService()");
		try {
			viewer.setInput(service.getInstalledFeatures(Activator.ADMIN_ID));
		} catch (RemoteOperationException e) {
			viewer.setInput(new String[] {"Error occured"});
			e.printStackTrace();
		}
	}

	public void unbindInstallService() {
		if (!viewer.getTable().isDisposed() && viewer.getContentProvider() != null) {
			viewer.setInput(new String[] {"Nothing to show"});
		}
	}

}