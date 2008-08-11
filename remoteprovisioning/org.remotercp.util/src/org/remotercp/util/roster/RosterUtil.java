package org.remotercp.util.roster;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.RosterEntry;
import org.eclipse.ecf.presence.roster.RosterItem;

public class RosterUtil {

	/**
	 * Returns an array with user IDs for all user of a given IRoster item
	 * 
	 */
	public static ID[] getUserIDs(IRoster roster) {
		List<IRosterEntry> entries = getRosterEntries(roster);

		ID[] ids = getUserIDs(entries);

		return ids;
	}

	public static ID[] getUserIDs(List<IRosterEntry> rosterEntries) {
		ID[] ids = new ID[rosterEntries.size()];

		for (int entry = 0; entry < rosterEntries.size(); entry++) {
			ids[entry] = rosterEntries.get(entry).getUser().getID();
		}

		return ids;
	}

	/**
	 * Returns a list of {@link RosterEntry} for a given {@link RosterItem} e.g.
	 * returns users for a given user group
	 * 
	 * @param entries
	 *            An empty list of entries which will be filled in this method
	 * @param item
	 *            The {@link IRosterItem} that containes {@link IRosterEntry}
	 * @return A list with {@link IRosterEntry} objects
	 */
	public static List<IRosterEntry> getRosterEntries(IRosterItem item) {

		List<IRosterEntry> entries = new ArrayList<IRosterEntry>();

		entries = searchRecursiveForRosterEntries(entries, item);

		return entries;

	}

	private static List<IRosterEntry> searchRecursiveForRosterEntries(
			List<IRosterEntry> entries, IRosterItem item) {
		if (item instanceof IRoster) {
			IRoster roster = (IRoster) item;
			// iterate over child elements
			for (Object rosterItem : roster.getItems()) {
				searchRecursiveForRosterEntries(entries,
						(IRosterItem) rosterItem);
			}
		}

		if (item instanceof IRosterGroup) {
			IRosterGroup group = (IRosterGroup) item;
			// iterate over child elements
			for (Object rosterItem : group.getEntries()) {
				searchRecursiveForRosterEntries(entries,
						(IRosterItem) rosterItem);
			}
		}

		if (item instanceof IRosterEntry) {
			/*
			 * users can be member in several groups, but user is here needed
			 * only once
			 */
			if (!entries.contains(item)) {
				entries.add((IRosterEntry) item);
			}
		}

		return entries;
	}

	/**
	 * Returns a List with online {@link IRosterEntry} for a given
	 * {@link IRosterItem}
	 * 
	 * @param item
	 *            The {@link IRosterItem} which elements need to be checked for
	 *            presence
	 * @return
	 */
	public static List<IRosterEntry> filterOnlineUser(IRosterItem item) {
		List<IRosterEntry> entries = getRosterEntries(item);
		List<IRosterEntry> filteredEntries = new ArrayList<IRosterEntry>();

		for (IRosterEntry entry : entries) {
			if (entry.getPresence().getType() == IPresence.Type.AVAILABLE) {
				filteredEntries.add(entry);
			}
		}

		return filteredEntries;
	}

	/**
	 * Returns an Array with online {@link IRosterEntry} for a given
	 * {@link IRosterItem}
	 * 
	 * @param item
	 *            The {@link IRosterItem} which elements need to be checked for
	 *            presence
	 * @return
	 */
	public static ID[] filterOnlineUserAsArray(IRosterItem item) {
		List<IRosterEntry> onlineUser = filterOnlineUser(item);
		IRosterEntry[] rosterEntries = (IRosterEntry[]) onlineUser
				.toArray(new IRosterEntry[onlineUser.size()]);
		ID[] userIDs = new ID[onlineUser.size()];

		for (int rosterEntry = 0; rosterEntry < rosterEntries.length; rosterEntry++) {
			userIDs[rosterEntry] = rosterEntries[rosterEntry].getUser().getID();
		}

		return userIDs;
	}

	/**
	 * If the selected item is a user group, check whether at least one user in
	 * the group is online. If the selected item is a user, check if the user is
	 * online
	 */
	public static boolean isRosterItemOnline(IRosterItem item) {
		List<IRosterEntry> entries = getRosterEntries(item);

		boolean userOnline = false;

		for (IRosterEntry entry : entries) {
			if (entry.getPresence().getType() == IPresence.Type.AVAILABLE) {
				userOnline = true;
			}
		}
		return userOnline;
	}

	/**
	 * Returns whether a IRoster contains already a given IRosterItem.
	 * IRosterItem may be either an IRosterGroup or IRosterEntry
	 * 
	 * @param roster
	 * @param item
	 * @return
	 */
	public static boolean hasRosterItem(IRoster roster, IRosterItem item) {
		boolean rosterContainsItem = false;

		// items may be IRosterGroup and/or IRosterEntry
		for (Object rosterItem : roster.getItems()) {
			// compare groups
			if (rosterItem instanceof IRosterGroup
					&& item instanceof IRosterGroup) {
				IRosterGroup tempGroup = (IRosterGroup) rosterItem;
				IRosterGroup parameterGroup = (IRosterGroup) item;

				if (tempGroup.getName().equals(parameterGroup.getName())) {
					rosterContainsItem = true;
					break;
				}
			}

			// compare entries
			if (rosterItem instanceof IRosterEntry
					&& item instanceof IRosterEntry) {
				IRosterEntry tempEntry = (IRosterEntry) rosterItem;
				IRosterEntry parameterEntry = (IRosterEntry) item;

				if (tempEntry.getUser().getID().equals(
						parameterEntry.getUser().getID())) {
					rosterContainsItem = true;
					break;
				}
			}

			// compare roster children with given roster item
			if (rosterItem instanceof IRosterGroup
					&& item instanceof IRosterEntry) {
				IRosterGroup group = (IRosterGroup) rosterItem;
				for (Object groupItem : group.getEntries()) {
					IRosterEntry tempEntry = (IRosterEntry) groupItem;
					if (tempEntry.getUser().getID().equals(
							((IRosterEntry) item).getUser().getID())) {
						rosterContainsItem = true;
						break;
					}
				}
			}
		}

		return rosterContainsItem;
	}
}