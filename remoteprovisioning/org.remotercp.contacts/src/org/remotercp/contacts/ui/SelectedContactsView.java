package org.remotercp.contacts.ui;

import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.remotercp.contacts.ContactsActivator;
import org.remotercp.contacts.ContactsContentProvider;
import org.remotercp.contacts.ContactsLabelProvider;
import org.remotercp.contacts.images.ImageKeys;
import org.remotercp.util.roster.RosterUtil;

public class SelectedContactsView extends ViewPart {

	private TreeViewer selectedContactsViewer;

	private Action installedBundlesAction;

	public SelectedContactsView() {
		// nothing to do
	}

	@Override
	public void createPartControl(Composite parent) {
		this.selectedContactsViewer = new TreeViewer(parent, SWT.BORDER
				| SWT.MULTI | SWT.V_SCROLL);

		/*
		 * Register Treeviewer as Selection provider
		 */
		getSite().setSelectionProvider(this.selectedContactsViewer);

		this.selectedContactsViewer
				.setContentProvider(new ContactsContentProvider());
		this.selectedContactsViewer
				.setLabelProvider(new ContactsLabelProvider());
		this.selectedContactsViewer
				.setFilters(new ViewerFilter[] { new SelectedContactsFilter() });

		this.hookActions();

		this.hookToolbarActions();

		this.hookKeyListener();

		this.initDragAndDropSupport();

	}

	private void hookActions() {
		this.installedBundlesAction = new Action("Get remote installed bundles") {
			@Override
			public void run() {
			}
		};

		this.installedBundlesAction.setImageDescriptor(ContactsActivator
				.imageDescriptorFromPlugin(ContactsActivator.PLUGIN_ID,
						ImageKeys.BUNDLE));
		this.installedBundlesAction
				.setToolTipText("Get remote installed bundles");
	}

	private void hookToolbarActions() {
		IToolBarManager toolBarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarManager.add(this.installedBundlesAction);
	}

	private void hookKeyListener() {
		this.selectedContactsViewer.getControl().addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.character == SWT.DEL) {
							IStructuredSelection selection = (IStructuredSelection) selectedContactsViewer
									.getSelection();
							removeTreeItem(selection);
						}
					}
				});
	}

	private void removeTreeItem(IStructuredSelection selection) {
		Roster roster = (Roster) this.selectedContactsViewer.getInput();
		IRosterItem item = (IRosterItem) selection.getFirstElement();

		roster.removeItem(item);

		/*
		 * TODO: selected item might be in a group and not directly belonging to
		 * roster. Walk through the groups and delete the item in the according
		 * group
		 */
		if (selection instanceof IRosterEntry) {
			for (Object rosterItem : roster.getItems()) {
				if (rosterItem instanceof IRosterGroup) {
					RosterGroup group = (RosterGroup) rosterItem;
					group.remove(item);
				}
			}
		}

		this.selectedContactsViewer.setInput(roster);

	}

	private void initDragAndDropSupport() {
		int dropOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] transfers = new Transfer[] { TreeObjectTransfer
				.getInstance() };

		this.selectedContactsViewer.addDropSupport(dropOperations, transfers,
				new DropTargetAdapter() {
					@Override
					public void drop(DropTargetEvent event) {
						final IRosterItem dragItem = DragAndDropSupport
								.getInstance().getDragItem();

						if (dragItem != null) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									SelectedContactsView.this
											.setInput(dragItem);
								}
							});
						}
					}

					@Override
					public void dragOver(DropTargetEvent event) {
						/*
						 * Display symbols as hints whether drop is supported
						 */
						event.detail = DND.DROP_COPY;
					}
				});
	}

	/**
	 * As a user can drag and drop items in the selected contacts view the old
	 * input must me kept in the content provider. But at the same time one has
	 * to check whether same items will be droped in and filter them out
	 * 
	 * @param input
	 */
	@SuppressWarnings("unchecked")
	protected void setInput(Object input) {
		IRoster roster = new Roster(null);

		// put old input to roster
		IRoster oldInput = (IRoster) this.selectedContactsViewer.getInput();
		if (oldInput != null) {
			roster.getItems().addAll(oldInput.getItems());
		}

		// check if roster contains already the given input
		if (!RosterUtil.hasRosterItem(roster, (IRosterItem) input)) {
			roster.getItems().add(input);
		}
		/*
		 * now roster has old and new input
		 */
		this.selectedContactsViewer.setInput(roster);
		this.selectedContactsViewer.expandAll();
	}

	@Override
	public void setFocus() {
		this.selectedContactsViewer.getControl().setFocus();

	}

	/**
	 * This filter is responsible to filter online user from all IRorsterItems.
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class SelectedContactsFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			boolean isOnline = true;
			if (element instanceof IRosterEntry) {
				IRosterEntry entry = (IRosterEntry) element;
				if (entry.getPresence().getType() != IPresence.Type.AVAILABLE) {
					isOnline = false;
				}
			}
			return isOnline;
		}

	}

}
