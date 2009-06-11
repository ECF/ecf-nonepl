package org.remotercp.contacts.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.junit.Test;
import org.remotercp.util.roster.RosterUtil;

public class TestUserPresenceContextMenu {

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

		IRoster roster = mock(IRoster.class);

		IRosterGroup group1 = mock(IRosterGroup.class);
		when(group1.getName()).thenReturn("group1");

		IRosterGroup group2 = mock(IRosterGroup.class);
		when(group2.getName()).thenReturn("group2");
		Collection<IRosterGroup> groups = new ArrayList<IRosterGroup>();
		groups.add(group1);
		groups.add(group2);

		when(roster.getItems()).thenReturn(groups);

		Collection<IRosterEntry> group1Entries = new ArrayList<IRosterEntry>();
		IRosterEntry klaus = createRosterEntry("Klaus", group1,
				IPresence.Type.AVAILABLE);
		IRosterEntry susi = createRosterEntry("Susi", group1,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry peter = createRosterEntry("Peter", group1,
				IPresence.Type.AVAILABLE);
		group1Entries.add(klaus);
		group1Entries.add(susi);
		group1Entries.add(peter);
		when(group1.getEntries()).thenReturn(group1Entries);

		Collection<IRosterEntry> group2Entries = new ArrayList<IRosterEntry>();
		IRosterEntry mike = createRosterEntry("Mike", group2,
				IPresence.Type.AVAILABLE);
		IRosterEntry sandra = createRosterEntry("Sandra", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry jason = createRosterEntry("Jason", group2,
				IPresence.Type.AVAILABLE);
		group2Entries.add(mike);
		group2Entries.add(sandra);
		group2Entries.add(jason);
		when(group2.getEntries()).thenReturn(group2Entries);

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

		IRoster roster = mock(IRoster.class);

		IRosterGroup group1 = mock(IRosterGroup.class);
		when(group1.getName()).thenReturn("group1");

		IRosterGroup group2 = mock(IRosterGroup.class);
		when(group2.getName()).thenReturn("group2");
		Collection<IRosterGroup> groups = new ArrayList<IRosterGroup>();
		groups.add(group1);
		groups.add(group2);

		when(roster.getItems()).thenReturn(groups);

		Collection<IRosterEntry> group1Entries = new ArrayList<IRosterEntry>();
		IRosterEntry klaus = createRosterEntry("Klaus", group1,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry susi = createRosterEntry("Susi", group1,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry peter = createRosterEntry("Peter", group1,
				IPresence.Type.UNAVAILABLE);
		group1Entries.add(klaus);
		group1Entries.add(susi);
		group1Entries.add(peter);
		when(group1.getEntries()).thenReturn(group1Entries);

		Collection<IRosterEntry> group2Entries = new ArrayList<IRosterEntry>();
		IRosterEntry mike = createRosterEntry("Mike", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry sandra = createRosterEntry("Sandra", group2,
				IPresence.Type.UNAVAILABLE);
		IRosterEntry jason = createRosterEntry("Jason", group2,
				IPresence.Type.UNAVAILABLE);
		group2Entries.add(mike);
		group2Entries.add(sandra);
		group2Entries.add(jason);
		when(group2.getEntries()).thenReturn(group2Entries);

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

	private IRosterEntry createRosterEntry(String name, IRosterGroup group,
			IPresence.Type presence) {
		IRosterEntry entry = mock(IRosterEntry.class);
		IPresence p = mock(IPresence.class);
		when(p.getType()).thenReturn(presence);
		when(entry.getPresence()).thenReturn(p);
		return entry;
	}
}
