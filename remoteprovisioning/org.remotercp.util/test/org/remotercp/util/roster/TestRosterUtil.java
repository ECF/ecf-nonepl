package org.remotercp.util.roster;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.junit.Before;
import org.junit.Test;

public class TestRosterUtil extends AbstractRosterGenerator {

	private IRoster roster;

	private IRosterGroup group1;

	private IRosterGroup group2;

	private List<IRosterEntry> rosterEntries;

	@Test
	public void testUserPresenceInContextMenu() {

		List<IRosterEntry> rosterEntries = RosterUtil.getRosterEntries(roster);
		assertEquals(7, rosterEntries.size());

		List<IRosterEntry> group1Entries = RosterUtil.getRosterEntries(group1);
		assertEquals(2, group1Entries.size());

		List<IRosterEntry> group2Entries = RosterUtil.getRosterEntries(group2);
		assertEquals(5, group2Entries.size());
	}

	@Test
	public void testGetUserIDs() {
		ID[] userIDs = RosterUtil.getUserIDs(rosterEntries);

		assertEquals(7, userIDs.length);
		for (int id = 0; id < userIDs.length; id++) {
			assertNotNull(userIDs[id]);
		}
	}

	@SuppressWarnings("unchecked")
	@Before
	public void initRoster() {

		roster = new Roster(null);

		group1 = new RosterGroup(roster, "group1");
		group2 = new RosterGroup(roster, "group2");

		roster.getItems().add(group1);
		roster.getItems().add(group2);

		IRosterEntry rosterEntry1 = super.createRosterEntry("Klaus", group1,
				IPresence.Type.AVAILABLE);
		IRosterEntry rosterEntry2 = super.createRosterEntry("Susi", group1,
				IPresence.Type.UNAVAILABLE);

		IRosterEntry rosterEntry3 = super.createRosterEntry("Peter", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry rosterEntry4 = super.createRosterEntry("Sandra", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry rosterEntry5 = super.createRosterEntry("Jack", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry rosterEntry6 = super.createRosterEntry("Mary", group2,
				IPresence.Type.UNAVAILABLE);

		IRosterEntry rosterEntry7 = super.createRosterEntry("Susan", group2,
				IPresence.Type.AVAILABLE);

		this.rosterEntries = new ArrayList<IRosterEntry>();
		rosterEntries.add(rosterEntry1);
		rosterEntries.add(rosterEntry2);
		rosterEntries.add(rosterEntry3);
		rosterEntries.add(rosterEntry4);
		rosterEntries.add(rosterEntry5);
		rosterEntries.add(rosterEntry6);
		rosterEntries.add(rosterEntry7);

		assertNotNull(roster.getItems());
		// roster must contain 2 groups
		assertEquals(2, roster.getItems().size());

		Collection items = roster.getItems();
		for (Object rosterItem : items) {
			IRosterGroup group = (IRosterGroup) rosterItem;
			assertNotNull(group.getEntries());
			assertFalse(group.getEntries().isEmpty());
		}
	}
}
