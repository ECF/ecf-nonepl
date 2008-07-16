package org.remotercp.preferences.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class PreferenceEditor extends EditorPart {

	public static final String EDITOR_ID = "org.remotercp.preferences.ui.preferenceEditor";

	private Map<String, String> preferences;

	private TableViewer preferencesViewer;

	private HashMap<String, String> preferenesClone;

	private final static String configurationScope = "/configuration/";

	private final static String instanceScope = "/instance/";

	private enum TableColumns {

		KEY("Key", 0), LOCAL_VALUE("Local value", 1), REMOTE_VALUE(
				"Remote value", 2);

		private final String label;
		private final int columnIndex;

		TableColumns(String label, int columnIndex) {
			this.label = label;
			this.columnIndex = columnIndex;
		}

		public String getLabel() {
			return label;
		}

		public int getColumnIndex() {
			return columnIndex;
		}
	}

	public PreferenceEditor() {
		// nothing to do yet
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);

		PreferenceEditorInput editorInput = (PreferenceEditorInput) input;
		preferences = editorInput.getPreferences();
		this.preferenesClone = new HashMap<String, String>();
		this.preferenesClone.putAll(this.preferences);
	}

	@Override
	public boolean isDirty() {
		return !this.preferenesClone.equals(this.preferences);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return !this.preferenesClone.equals(this.preferences);
	}

	@Override
	public void createPartControl(Composite parent) {
		Group main = new Group(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			this.preferencesViewer = new TableViewer(main);
			this.preferencesViewer
					.setContentProvider(new ArrayContentProvider());
			this.preferencesViewer
					.setLabelProvider(new PreferencesLabelProvider());
			this.preferencesViewer
					.setCellModifier(new PreferencesCellModifier());
			this.preferencesViewer.setColumnProperties(new String[] {
					TableColumns.KEY.getLabel(),
					TableColumns.REMOTE_VALUE.getLabel(),
					TableColumns.LOCAL_VALUE.getLabel() });

			Table table = this.preferencesViewer.getTable();

			/* only remote value is editable */
			this.preferencesViewer.setCellEditors(new CellEditor[] { null,
					new TextCellEditor(table), null });


			GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			TableColumn localKey = new TableColumn(table, SWT.LEFT);
			localKey.setText(TableColumns.KEY.getLabel());
			localKey.setWidth(250);

			TableColumn remoteValue = new TableColumn(table, SWT.LEFT);
			remoteValue.setText(TableColumns.REMOTE_VALUE.getLabel());
			remoteValue.setWidth(250);

			TableColumn localValue = new TableColumn(table, SWT.LEFT);
			localValue.setText(TableColumns.LOCAL_VALUE.getLabel());
			localValue.setWidth(250);

		}
		this.initViewer();
	}

	/*
	 * At the beginning onyl remote preferences are loaded therefore the table
	 * does only display remote preferences. Afterwards a user can select a file
	 * with local preferences which will be added to the table.
	 */
	private void initViewer() {
		List<EditableTableItem> items = new ArrayList<EditableTableItem>();

		for (String key : this.preferences.keySet()) {
			EditableTableItem item = new EditableTableItem();
			String value = this.preferences.get(key);

			/* remote scopes from keys */
			if (key.startsWith(configurationScope)) {
				key = key.replaceAll(configurationScope, "");
			}
			if (key.startsWith(instanceScope)) {
				key = key.replaceAll(instanceScope, "");
			}
			item.setKey(key);
			item.setRemoteValue(value);

			items.add(item);
		}
		this.preferencesViewer.setInput(items);
	}

	@Override
	public void setFocus() {
		this.preferencesViewer.getControl().setFocus();
	}

	// ##############################
	// Private classes
	// ##############################

	/*
	 * Viewer Input object
	 */
	private class EditableTableItem {
		private String key;
		private String localValue;
		private String remoteValue;

		public String getKey() {
			return key;
		}

		public void setKey(String localKey) {
			this.key = localKey;
		}

		public String getLocalValue() {
			return localValue;
		}

		public void setLocalValue(String localValue) {
			this.localValue = localValue;
		}

		public String getRemoteValue() {
			return remoteValue;
		}

		public void setRemoteValue(String remoteValue) {
			this.remoteValue = remoteValue;
		}
	}

	/*
	 * Viewer label provider
	 */
	private class PreferencesLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			EditableTableItem item = (EditableTableItem) element;
			String columnText = null;
			switch (columnIndex) {
			case 0:
				columnText = item.getKey();
				break;
			case 1:
				columnText = item.getRemoteValue();
				break;
			case 2:
				columnText = item.getLocalValue();
				break;
			default:
				break;
			}
			return columnText;
		}
	}

	/*
	 * Viewer cell modifier
	 */
	private class PreferencesCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			if (property.equals(TableColumns.REMOTE_VALUE.getLabel())) {
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			String value = "";
			EditableTableItem item = (EditableTableItem) element;
			if (property.equals(TableColumns.REMOTE_VALUE.getLabel())) {
				value = item.getRemoteValue();
			}
			return value;
		}

		public void modify(Object element, String property, Object value) {
			TableItem tableItem = (TableItem) element;
			EditableTableItem item = (EditableTableItem) tableItem.getData();
			if (property.equals(TableColumns.REMOTE_VALUE.getLabel())) {
				item.setRemoteValue(value.toString());
			}
			preferencesViewer.refresh();
		}
	}
}
