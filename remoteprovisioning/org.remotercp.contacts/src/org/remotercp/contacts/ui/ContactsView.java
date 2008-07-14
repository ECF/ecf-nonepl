package org.remotercp.contacts.ui;

import java.util.logging.Logger;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterListener;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.remotercp.contacts.ContactsActivator;
import org.remotercp.contacts.ContactsContentProvider;
import org.remotercp.contacts.ContactsLabelProvider;
import org.remotercp.contacts.images.ImageKeys;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class ContactsView extends ViewPart {

	// ID must correspond to that one in the plugin xml
	public static final String VIEW_ID = "contacts.ui.contactsview";

	private TreeViewer treeViewer;

	private IPresenceListener presenceListener;

	private ISessionService session;

	private final static Logger logger = Logger.getLogger(ContactsView.class
			.getName());

	public ContactsView() {
		this.initServices();
	}

	protected void initServices() {
		this.session = OsgiServiceLocatorUtil.getOSGiService(ContactsActivator
				.getBundleContext(), ISessionService.class);
	}

	/*
	 * Add listener that handles new user/remove user and update user events.
	 * The listener will update the treeViewer and call refresh.
	 */
	protected void initListener() {
		this.session.getRosterManager().addRosterListener(
				new IRosterListener() {

					public void handleRosterEntryAdd(IRosterEntry entry) {
						updateTreeViewer();

					}

					public void handleRosterEntryRemove(IRosterEntry entry) {
						updateTreeViewer();
					}

					public void handleRosterUpdate(IRoster roster,
							IRosterItem changedValue) {
						updateTreeViewer();
					}

				});
	}

	protected void updateTreeViewer() {
		this.treeViewer.setInput(this.session.getRoster());
	}

	@Override
	public void createPartControl(Composite parent) {
		// initializeSession(); // temporary tweak to build a fake model
		this.treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);

		/*
		 * Register Treeviewer as Selection provider
		 */
		getSite().setSelectionProvider(treeViewer);

		treeViewer.setLabelProvider(new ContactsLabelProvider());
		treeViewer.setContentProvider(new ContactsContentProvider());

		treeViewer.setInput(this.session.getRoster());
		treeViewer.expandAll();
		this.presenceListener = new IPresenceListener() {
			public void handlePresence(ID fromID, IPresence presence) {
				refresh();
			}
		};

		// register presence listener
		this.session.getRosterManager().addPresenceListener(
				this.presenceListener);

		// hook context menu
		this.hookContextMenu();

		// show online user in status line
		this.setStatuslineInfo();

		this.initDragAndDrop();
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		Menu contextMenu = menuManager.createContextMenu(treeViewer
				.getControl());

		treeViewer.getControl().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, treeViewer);
	}

	/*
	 * Displays the current user in the status line
	 */
	private void setStatuslineInfo() {
		IStatusLineManager statusLineManager = getViewSite().getActionBars()
				.getStatusLineManager();
		ImageDescriptor imageDescriptorFromPlugin = AbstractUIPlugin
				.imageDescriptorFromPlugin(ContactsActivator.PLUGIN_ID,
						ImageKeys.ONLINE);
		Image image = imageDescriptorFromPlugin.createImage();

		IUser user = this.session.getRoster().getUser();

		statusLineManager.setMessage(image, "Online user: " + user.getName());
	}

	private void refresh() {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh();
			}
		});
	}

	private void initDragAndDrop() {
		final int dragOperations = DND.DROP_MOVE | DND.DROP_COPY;
		final Transfer[] types = new Transfer[] { TreeObjectTransfer
				.getInstance() };

		this.treeViewer.addDragSupport(dragOperations, types,
				new DragSourceListener() {

					IRosterItem selection = null;

					public void dragFinished(DragSourceEvent event) {
						if (event.detail == DND.DROP_MOVE) {
							/*
							 * think of coloring the draged files gray in order
							 * to show which items have already been selected
							 */
							logger.info("drag finished");
						}

					}

					public void dragSetData(DragSourceEvent event) {
						if (TreeObjectTransfer.getInstance().isSupportedType(
								event.dataType)) {

							// event.data = selection;
							DragAndDropSupport.getInstance().setDragItem(
									selection);
							logger.info("drag set data");
						}

					}

					public void dragStart(DragSourceEvent event) {
						/*
						 * only start the drag if the selected IRosterItem is
						 * online
						 */

						selection = getTreeViewerSelection();

						if (RosterUtil.isRosterItemOnline(selection)) {
							event.doit = true;
						}else{
							event.doit = false;
						}

						logger.info("Drag start = " + event.doit);
					}

				});
	}

	private IRosterItem getTreeViewerSelection() {
		IStructuredSelection selection = (IStructuredSelection) ContactsView.this.treeViewer
				.getSelection();

		IRosterItem item = (IRosterItem) selection.getFirstElement();
		return item;
	}

	@Override
	public void setFocus() {
		this.treeViewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		this.presenceListener = null;
		this.treeViewer = null;
	}
}
