package org.remotercp.contacts.actions;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterEntry;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.junit.Test;
import org.remotercp.util.roster.RosterUtil;

public class TestUserPresenceContextMenu {

	@Test
	public void testUserPresenceInContextMenu() {

		IRoster rosterItem = this.getOnlineRoster();

		List<IRosterEntry> rosterEntries = RosterUtil
				.getRosterEntries(rosterItem);

		Assert.assertEquals(5, rosterEntries.size());
		Assert.assertEquals(true, RosterUtil.isRosterItemOnline(rosterItem));

		IRoster offlineRoster = this.getOfflineRoster();
		Assert
				.assertEquals(false, RosterUtil
						.isRosterItemOnline(offlineRoster));
	}

	@SuppressWarnings("unchecked")
	private IRoster getOnlineRoster() {
		IUser user1 = new User(null, "Klaus");
		IUser user2 = new User(null, "Susi");
		IUser user3 = new User(null, "Marie");
		IUser user4 = new User(null, "Peter");

		IRoster roster = new Roster(null);

		IRosterGroup group1 = new RosterGroup(roster, "group1");
		IRosterGroup group2 = new RosterGroup(roster, "group2");

		roster.getItems().add(group1);
		roster.getItems().add(group2);

		IPresence presence1 = new Presence(IPresence.Type.AVAILABLE);
		IPresence presence2 = new Presence(IPresence.Type.UNAVAILABLE);

		new RosterEntry(group1, user1, presence1);
		new RosterEntry(group1, user2, presence2);

		new RosterEntry(group2, user2, presence2);
		new RosterEntry(group2, user3, presence2);
		new RosterEntry(group2, user4, presence2);
		new RosterEntry(roster, user4, presence1);

		Assert.assertNotNull(roster.getItems());
		// roster must contain 2 groups
		Assert.assertEquals(2, roster.getItems().size());

		Collection items = roster.getItems();
		for (Object rosterItem : items) {
			IRosterGroup group = (IRosterGroup) rosterItem;
			Assert.assertNotNull(group.getEntries());
			Assert.assertFalse(group.getEntries().isEmpty());
		}
		return roster;
	}

	@SuppressWarnings("unchecked")
	private IRoster getOfflineRoster() {
		IUser user1 = new User(null, "Dieter");
		IUser user2 = new User(null, "Verona");

		IRoster roster = new Roster(null);

		IRosterGroup group = new RosterGroup(roster, "group3");
		roster.getItems().add(group);

		IPresence presence2 = new Presence(IPresence.Type.UNAVAILABLE);
		new RosterEntry(group, user1, presence2);
		new RosterEntry(roster, user2, presence2);

		return roster;
	}
}
