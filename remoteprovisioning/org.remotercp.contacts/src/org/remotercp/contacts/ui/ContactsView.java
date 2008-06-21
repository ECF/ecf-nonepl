package org.remotercp.contacts.ui;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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

public class ContactsView extends ViewPart {

	// ID must correspond to that one in the plugin xml
	public static final String VIEW_ID = "contacts.ui.contactsview";

	private TreeViewer treeViewer;

	// private IAdapterFactory adapterFactory = new ContactsAdapterFactory();

	private IPresenceListener presenceListener;

	private ISessionService session;

	private ContactsLabelProvider contactsLabelProvider;

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

		treeViewer
				.setLabelProvider(contactsLabelProvider = new ContactsLabelProvider());
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
				// treeViewer.setInput(Session.getInstance().getRoster());
				treeViewer.refresh();
			}
		});
	}

	/**
	 * This method does set the selected items in the label provider in order
	 * that the label provider paints the selected items in a different
	 * foreground color.
	 * 
	 * XXX somehow this approach doesn't make me happy. think of another way to
	 * show the admin which contacts he is going to manage
	 */
	public void doMarkSelectedRosterItems() {
		ISelection selection = this.treeViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			this.contactsLabelProvider
					.setSelection((IRosterItem) structuredSelection
							.getFirstElement());

			this.treeViewer.refresh();
		}
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

	// public class EditorClosedListener implements IPropertyListener {
	//
	// public void propertyChanged(Object source, int propId) {
	// Logger.getAnonymousLogger().info(
	// "PropertyEvent: " + source + " id: " + propId);
	//
	// }
	//
	// }

}
