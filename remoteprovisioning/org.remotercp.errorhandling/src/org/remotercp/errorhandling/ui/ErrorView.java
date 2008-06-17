package org.remotercp.errorhandling.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.remotercp.errorhandling.ErrorHandlingActivator;
import org.remotercp.errorhandling.images.ImageKeys;

/**
 * The error view will display all possible errors that can occur while remote
 * managing users. Rather than open dozens of Message diaologs the error view
 * collects errors and dipslays them for the admin.
 * 
 * @author Eugen Reiswich
 * @date 16.06.2008
 * 
 */
public class ErrorView extends ViewPart {

	private final static String ID = "org.remotercp.errorhandling.errorview";

	private TableViewer tableViewer;

	private List<ErrorMessage> errorMessageList;

	private Action deleteErrorsAction;

	public static final int COLUMN_ICON = 0;

	public static final int COLUMN_MESSAGE = 1;

	public static final int COLUMN_DATE = 2;

	public ErrorView() {
		this.errorMessageList = new ArrayList<ErrorMessage>();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			this.tableViewer = new TableViewer(main);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.tableViewer.getControl());

			this.tableViewer.setContentProvider(new ArrayContentProvider());
			this.tableViewer.setLabelProvider(new ErrorTableLabelProvider());
			this.tableViewer.setSorter(new ErrorMessageSorter());

			Table table = this.tableViewer.getTable();
			// table.setLayoutData(new GridData(GridData.FILL_BOTH));

			TableColumn columnImage = new TableColumn(table, SWT.LEFT);
			columnImage.setWidth(30);
			columnImage.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					((ErrorMessageSorter) tableViewer.getSorter())
							.doSort(COLUMN_ICON);
					tableViewer.refresh();
				}
			});

			TableColumn columnMessage = new TableColumn(table, SWT.LEFT);
			columnMessage.setText("Message");
			columnMessage.setWidth(500);
			columnMessage.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					((ErrorMessageSorter) tableViewer.getSorter())
							.doSort(COLUMN_MESSAGE);
					tableViewer.refresh();
				}
			});

			TableColumn columnDate = new TableColumn(table, SWT.LEFT);
			columnDate.setText("Date");
			columnDate.setWidth(150);
			columnDate.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					((ErrorMessageSorter) tableViewer.getSorter())
							.doSort(COLUMN_DATE);
					tableViewer.refresh();
				}
			});

			table.setHeaderVisible(true);
			table.setLinesVisible(true);
		}

		// create toolbar actions
		this.createActions();
	}

	/**
	 * This method will add an error to the error view. This method is static in
	 * order that errors can be added from different parts of application.
	 * 
	 * @param errorText
	 *            The error description
	 * @param severity
	 *            Logger.Level severity. Is used to display appropriate images
	 *            like warning or error. Level supported: SEVERE, WARNING, INFO
	 */
	public static void addError(String errorText, Level severity) {

		Image image = getImageBySeverity(severity);
		Assert.isNotNull(image);

		final ErrorMessage message = new ErrorMessage(errorText, image,
				severity);

		// find error view reference
		IViewReference[] viewReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();

		for (IViewReference reference : viewReferences) {
			if (ID.equalsIgnoreCase(reference.getId())) {
				// add error message
				ErrorView errorView = (ErrorView) reference.getView(true);
				errorView.addError(message);

				// bring view to front
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(ErrorView.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public void addError(final ErrorMessage message) {
		this.errorMessageList.add(message);
		this.tableViewer.setInput(this.errorMessageList);
		this.deleteErrorsAction.setEnabled(this.errorMessageList.size() > 0);
	}

	/**
	 * Returns the appropriate image for a severity. This image will be
	 * displayed as an icon in the error table.
	 * 
	 * @param severity
	 *            The {@link Logger} Level severity
	 * @return Image for a severity (ERROR, WARNING, INFO)
	 */
	protected static Image getImageBySeverity(Level severity) {
		Image image = null;
		if (severity == Level.SEVERE) {
			image = ErrorHandlingActivator.getImageDescriptor(ImageKeys.ERROR)
					.createImage();
		}
		if (severity == Level.WARNING) {
			image = ErrorHandlingActivator
					.getImageDescriptor(ImageKeys.WARNING).createImage();
		}

		if (severity == Level.INFO) {
			image = ErrorHandlingActivator.getImageDescriptor(ImageKeys.INFO)
					.createImage();
		}

		return image;
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	protected void createActions() {
		// delete Error logs
		this.deleteErrorsAction = new Action("Delete errors") {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						errorMessageList.clear();
						tableViewer.refresh();
						// disable action because the table doesn't contain
						// errors to be deleted
						deleteErrorsAction.setEnabled(false);
					}
				});
			}
		};

		// at the beginning no erros are in the table hence disable action
		this.deleteErrorsAction.setEnabled(false);

		this.deleteErrorsAction.setImageDescriptor(ErrorHandlingActivator
				.getImageDescriptor(ImageKeys.CLEAR));

		// create tool bar
		IToolBarManager toolBarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarManager.add(this.deleteErrorsAction);
	}

	/**
	 * Label Provider for the error table
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class ErrorTableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == COLUMN_ICON) {
				return ((ErrorMessage) element).getImage();
			} else {
				return null;
			}
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case COLUMN_MESSAGE:
				return ((ErrorMessage) element).getText();
			case COLUMN_DATE:
				return ((ErrorMessage) element).getDate();
			default:
				return null;
			}

		}

		public void addListener(ILabelProviderListener listener) {
			// do nithing

		}

		public void dispose() {
			// do nithing

		}

		public boolean isLabelProperty(Object element, String property) {
			// do nithing
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// do nithing

		}

	}

}
