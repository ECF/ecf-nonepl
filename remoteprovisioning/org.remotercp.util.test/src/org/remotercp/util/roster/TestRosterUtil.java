package org.remotercp.util.roster;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

	@Test
	public void testHasRosterItem() {
		RosterGroup rosterGroup1 = new RosterGroup(null, "group1");
		RosterGroup rosterGroup2 = new RosterGroup(null, "groupNew");

		assertEquals(true, RosterUtil.hasRosterItem(roster, rosterGroup1));
		assertEquals(false, RosterUtil.hasRosterItem(roster, rosterGroup2));

		IRosterEntry rosterEntry1 = super.createRosterEntry("Mary",
				rosterGroup1, IPresence.Type.AVAILABLE);

		IRosterEntry rosterEntry2 = super.createRosterEntry("NewPerson",
				rosterGroup1, IPresence.Type.AVAILABLE);

		assertEquals(true, RosterUtil.hasRosterItem(roster, rosterEntry1));
		assertEquals(false, RosterUtil.hasRosterItem(roster, rosterEntry2));
	}

	@Test
	public void testFilterOnlineUserForRosterItem() {
		IRoster roster = mock(IRoster.class);
		IRosterGroup group1 = mock(IRosterGroup.class);
		when(group1.getName()).thenReturn("group1");
		IRosterGroup group2 = mock(IRosterGroup.class);
		when(group2.getName()).thenReturn("group2");

		IRosterEntry sandra = mock(IRosterEntry.class);
		IRosterEntry klaus = mock(IRosterEntry.class);
		IRosterEntry dima = mock(IRosterEntry.class);
		IRosterEntry peter = mock(IRosterEntry.class);
		IRosterEntry spiderman = mock(IRosterEntry.class);

		Collection<IRosterEntry> rosterEntries1 = new ArrayList<IRosterEntry>();
		rosterEntries1.add(sandra);
		rosterEntries1.add(klaus);
		rosterEntries1.add(dima);

		Collection<IRosterEntry> rosterEntries2 = new ArrayList<IRosterEntry>();
		rosterEntries2.add(peter);
		rosterEntries2.add(spiderman);

		Collection<IRosterGroup> rosterGroups = new ArrayList<IRosterGroup>();
		rosterGroups.add(group1);
		rosterGroups.add(group2);

		// build roster object
		when(group1.getEntries()).thenReturn(rosterEntries1);
		when(group2.getEntries()).thenReturn(rosterEntries2);
		when(roster.getItems()).thenReturn(rosterGroups);

		// scenario 1: sandra=online; klaus & dima = offline
		IPresence sandraPresence = mock(IPresence.class);
		when(sandra.getPresence()).thenReturn(sandraPresence);
		when(sandraPresence.getType()).thenReturn(IPresence.Type.AVAILABLE);

		IPresence klausPresence = mock(IPresence.class);
		when(klaus.getPresence()).thenReturn(klausPresence);
		when(klausPresence.getType()).thenReturn(IPresence.Type.UNAVAILABLE);

		IPresence dimaPresence = mock(IPresence.class);
		when(dima.getPresence()).thenReturn(dimaPresence);
		when(dimaPresence.getType()).thenReturn(IPresence.Type.UNAVAILABLE);

		IPresence peterPresence = mock(IPresence.class);
		when(peter.getPresence()).thenReturn(peterPresence);
		when(peterPresence.getType()).thenReturn(IPresence.Type.UNAVAILABLE);

		IPresence spidermanPresence = mock(IPresence.class);
		when(spiderman.getPresence()).thenReturn(spidermanPresence);
		when(spidermanPresence.getType())
				.thenReturn(IPresence.Type.UNAVAILABLE);

		IRoster onlineUserForRosterItem = (IRoster) RosterUtil
				.filterOnlineUserForRosterItem(roster);
		Collection<?> groups = onlineUserForRosterItem.getItems();
		assertEquals(2, groups.size());

		for (Object obj : groups) {
			IRosterGroup group = (IRosterGroup) obj;
			if (group.getName().equals("group1")) {
				assertEquals(1, group.getEntries().size());
			} else if (group.getName().equals("group2")) {
				assertEquals(0, group.getEntries().size());
			}
		}

		assertEquals(1, RosterUtil.getRosterEntries(onlineUserForRosterItem)
				.size());
	}

	@Test
	public void testGetRosterEntries() {
		IRoster roster = mock(IRoster.class);
		IRosterGroup group1 = mock(IRosterGroup.class);
		when(group1.getName()).thenReturn("group1");
		IRosterGroup group2 = mock(IRosterGroup.class);
		when(group2.getName()).thenReturn("group2");

		IRosterEntry sandra = mock(IRosterEntry.class);
		when(sandra.getName()).thenReturn("Sandra");

		IRosterEntry klaus = mock(IRosterEntry.class);
		when(klaus.getName()).thenReturn("Klaus");

		IRosterEntry dima = mock(IRosterEntry.class);
		when(dima.getName()).thenReturn("Dima");

		IRosterEntry peter = mock(IRosterEntry.class);
		when(peter.getName()).thenReturn("Peter");

		IRosterEntry spiderman = mock(IRosterEntry.class);
		when(spiderman.getName()).thenReturn("Spiderman");

		Collection<IRosterEntry> rosterEntries1 = new ArrayList<IRosterEntry>();
		Collection<IRosterEntry> rosterEntries2 = new ArrayList<IRosterEntry>();
		Collection<IRosterGroup> rosterGroups = new ArrayList<IRosterGroup>();
		when(roster.getItems()).thenReturn(rosterGroups);

		// test set up: 1 group 2 entries
		rosterGroups.add(group1);
		rosterEntries1.add(sandra);
		rosterEntries1.add(klaus);
		when(group1.getEntries()).thenReturn(rosterEntries1);

		List<IRosterEntry> entries = RosterUtil.getRosterEntries(roster);
		assertEquals(2, entries.size());
		boolean sandraFound = false;
		boolean klausFound = false;
		for (IRosterEntry entry : entries) {
			if (entry.getName().equals("Sandra")) {
				sandraFound = true;
			} else if (entry.getName().equals("Klaus")) {
				klausFound = true;
			}
		}
		assertEquals(true, sandraFound && klausFound);

		// test set up: 2 groups : 5 entries
		rosterGroups.add(group2);
		rosterEntries2.add(dima);
		rosterEntries2.add(peter);
		rosterEntries2.add(spiderman);
		when(group2.getEntries()).thenReturn(rosterEntries2);

		entries = RosterUtil.getRosterEntries(roster);
		assertEquals(5, entries.size());
	}
}
