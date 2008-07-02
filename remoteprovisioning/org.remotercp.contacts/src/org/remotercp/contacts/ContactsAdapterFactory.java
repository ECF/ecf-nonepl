package org.remotercp.contacts;

import java.util.Collection;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.remotercp.contacts.images.ImageKeys;

public class ContactsAdapterFactory implements IAdapterFactory {

	/**
	 * Adapter for {@link IRosterGroup}
	 */
	private IWorkbenchAdapter groupAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			IRosterGroup group = (IRosterGroup) o;
			return group.getParent();
		}

		public String getLabel(Object o) {
			RosterGroup group = (RosterGroup) o;

			boolean areChildrenRosterEntries = this
					.areChildrenInstanceOfRosterEntry(group);

			if (areChildrenRosterEntries) {
				int available = getNumAvailable(group);

				return group.getName() + " (" + available + "/"
						+ group.getEntries().size() + ")";
			}

			return group.getName();
		}

		/**
		 * Returns the amount of available user to chat with
		 * 
		 * @param group
		 *            The group which online users have to be determined
		 * @return amount of online user
		 */
		@SuppressWarnings("unchecked")
		private int getNumAvailable(IRosterGroup group) {
			int available = 0;
			Collection<IRosterEntry> entries = group.getEntries();

			for (IRosterEntry entry : entries) {
				IPresence presence = ((IRosterEntry) entry).getPresence();
				if (presence != null
						&& presence.getType() != IPresence.Type.UNAVAILABLE) {
					available++;
				}
			}

			return available;
		}

		/**
		 * Groups can contain other groups. This method determines whether the
		 * children of a group are instance of {@link IRosterGroup} or
		 * {@link IRosterEntry}
		 * 
		 * @param child
		 *            The {@link IRosterGroup} to determine children for
		 * @return True if group contains {@link IRosterEntry}, otherwise false
		 */
		private boolean areChildrenInstanceOfRosterEntry(IRosterGroup group) {
			// check whether children are further groups or entries
			if (group.getEntries().iterator().hasNext()) {
				IRosterItem item = (IRosterItem) group.getEntries().iterator()
						.next();

				if (item instanceof IRosterEntry) {
					return true;
				}
			}
			return false;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					ContactsActivator.PLUGIN_ID, ImageKeys.GROUP);
		}

		public Object[] getChildren(Object o) {
			IRosterGroup group = (IRosterGroup) o;
			return group.getEntries().toArray();
		}
	};

	/**
	 * Adapter for {@link IRosterEntry}
	 */
	private IWorkbenchAdapter entryAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			IRosterEntry entry = (IRosterEntry) o;
			return entry.getParent();
		}

		public String getLabel(Object o) {
			IRosterEntry entry = ((IRosterEntry) o);

			// return entry.getName() + " (" + entry.getUser() + ")";
			return entry.getUser().getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			IRosterEntry entry = (IRosterEntry) object;
			String key = presenceToKey(entry.getPresence());
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					ContactsActivator.PLUGIN_ID, key);
		}

		public Object[] getChildren(Object o) {
			// entries are not supposed to have children
			return new Object[0];
		}
	};

	/**
	 * Adapter for {@link IRoster}
	 */
	private IWorkbenchAdapter rosterAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			// roster is not supposed to have a parent
			return null;
		}

		public String getLabel(Object o) {
			return "";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			IRoster roster = (IRoster) o;
			return roster.getItems().toArray();
		}
	};

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRosterGroup)
			return groupAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRosterEntry)
			return entryAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRoster)
			return rosterAdapter;
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	/**
	 * Returns an Image URL for a given Prsence
	 * 
	 * @param presence
	 * @return
	 */
	private String presenceToKey(IPresence p) {
		if (p.getType() == IPresence.Type.AVAILABLE) {
			return ImageKeys.ONLINE16x16;
		}

		if (p.getType() == IPresence.Type.UNAVAILABLE) {
			return ImageKeys.OFFLINE16x16;
		}
		return ImageKeys.OFFLINE16x16;
	}
}
