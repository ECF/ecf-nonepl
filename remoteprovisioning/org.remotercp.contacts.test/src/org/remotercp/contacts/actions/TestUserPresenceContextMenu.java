package org.remotercp.contacts.actions;

import java.util.Collection;
import java.util.List;

import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.junit.Test;
import org.remotercp.util.roster.AbstractRosterGenerator;
import org.remotercp.util.roster.RosterUtil;
import static org.junit.Assert.*;

public class TestUserPresenceContextMenu extends AbstractRosterGenerator {

	@Test
	public void testUserPresenceInContextMenu() {

		IRoster rosterItem = this.getOnlineRoster();

		List<IRosterEntry> rosterEntries = RosterUtil
				.getRosterEntries(rosterItem);

		assertEquals(6, rosterEntries.size());
		assertEquals(true, RosterUtil.isRosterItemOnline(rosterItem));

		IRoster offlineRoster = this.getOfflineRoster();
		rosterEntries = RosterUtil.getRosterEntries(offlineRoster);
		assertEquals(6, rosterEntries.size());

		assertEquals(false, RosterUtil.isRosterItemOnline(offlineRoster));
	}

	@SuppressWarnings("unchecked")
	private IRoster getOnlineRoster() {

		IRoster roster = new Roster(null);

		IRosterGroup group1 = new RosterGroup(roster, "group1");
		IRosterGroup group2 = new RosterGroup(roster, "group2");

		roster.getItems().add(group1);
		roster.getItems().add(group2);

		super.createRosterEntry("Klaus", group1, IPresence.Type.AVAILABLE);
		super.createRosterEntry("Susi", group1, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Peter", group1, IPresence.Type.AVAILABLE);

		super.createRosterEntry("Mike", group2, IPresence.Type.AVAILABLE);
		super.createRosterEntry("Sandra", group2, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Jason", group2, IPresence.Type.AVAILABLE);

		assertNotNull(roster.getItems());
		// roster must contain 2 groups
		assertEquals(2, roster.getItems().size());

		Collection items = roster.getItems();
		for (Object rosterItem : items) {
			IRosterGroup group = (IRosterGroup) rosterItem;
			assertNotNull(group.getEntries());
			assertFalse(group.getEntries().isEmpty());
		}
		return roster;
	}

	@SuppressWarnings("unchecked")
	private IRoster getOfflineRoster() {

		IRoster roster = new Roster(null);

		IRosterGroup group1 = new RosterGroup(roster, "group1");
		IRosterGroup group2 = new RosterGroup(roster, "group2");

		roster.getItems().add(group1);
		roster.getItems().add(group2);

		super.createRosterEntry("Klaus", group1, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Susi", group1, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Peter", group1, IPresence.Type.UNAVAILABLE);

		super.createRosterEntry("Mike", group2, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Sandra", group2, IPresence.Type.UNAVAILABLE);
		super.createRosterEntry("Jason", group2, IPresence.Type.UNAVAILABLE);

		assertNotNull(roster.getItems());
		// roster must contain 2 groups
		assertEquals(2, roster.getItems().size());

		Collection items = roster.getItems();
		for (Object rosterItem : items) {
			IRosterGroup group = (IRosterGroup) rosterItem;
			assertNotNull(group.getEntries());
			assertFalse(group.getEntries().isEmpty());
		}
		return roster;
	}
}
