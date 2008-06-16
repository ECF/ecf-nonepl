package org.remotercp.util.roster;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
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
import org.junit.Before;
import org.junit.Test;

public class TestRosterUtil {

	private IRoster roster;

	private IRosterGroup group1;

	private IRosterGroup group2;

	private List<IRosterEntry> rosterEntries;

	@Test
	public void testUserPresenceInContextMenu() {

		List<IRosterEntry> rosterEntries = RosterUtil.getRosterEntries(roster);
		assertEquals(4, rosterEntries.size());

		List<IRosterEntry> group1Entries = RosterUtil.getRosterEntries(group1);
		assertEquals(2, group1Entries.size());

		List<IRosterEntry> group2Entries = RosterUtil.getRosterEntries(group2);
		assertEquals(3, group2Entries.size());
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
		TestNameSpace namespace = new TestNameSpace();
		ID user1ID = null;
		ID user2ID = null;
		ID user3ID = null;
		ID user4ID = null;

		try {
			user1ID = namespace.createInstance(new String[] { "klaus" });
			user2ID = namespace.createInstance(new String[] { "susi" });
			user3ID = namespace.createInstance(new String[] { "marie" });
			user4ID = namespace.createInstance(new String[] { "peter" });
		} catch (IDCreateException e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull(user1ID);
		assertNotNull(user2ID);
		assertNotNull(user3ID);
		assertNotNull(user4ID);

		IUser user1 = new User(user1ID, "Klaus");
		IUser user2 = new User(user2ID, "Susi");
		IUser user3 = new User(user3ID, "Marie");
		IUser user4 = new User(user4ID, "Peter");

		roster = new Roster(null);

		group1 = new RosterGroup(roster, "group1");
		group2 = new RosterGroup(roster, "group2");

		roster.getItems().add(group1);
		roster.getItems().add(group2);

		IPresence presence1 = new Presence(IPresence.Type.AVAILABLE);
		IPresence presence2 = new Presence(IPresence.Type.UNAVAILABLE);

		RosterEntry rosterEntry1 = new RosterEntry(group1, user1, presence1);
		RosterEntry rosterEntry2 = new RosterEntry(group1, user2, presence2);

		RosterEntry rosterEntry3 = new RosterEntry(group2, user2, presence2);
		RosterEntry rosterEntry4 = new RosterEntry(group2, user3, presence2);
		RosterEntry rosterEntry5 = new RosterEntry(group2, user4, presence2);
		RosterEntry rosterEntry6 = new RosterEntry(group2, user4, presence2);

		RosterEntry rosterEntry7 = new RosterEntry(roster, user4, presence1);

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

	private class TestID extends BaseID {

		private static final long serialVersionUID = 1L;

		private String username;

		public TestID(TestNameSpace namespace, String username) {
			super(namespace);
			this.username = username;
		}

		@Override
		protected int namespaceCompareTo(BaseID o) {
			return 0;
		}

		@Override
		protected boolean namespaceEquals(BaseID o) {
			return o.getName().equals(username);
		}

		@Override
		protected String namespaceGetName() {
			return username;
		}

		@Override
		protected int namespaceHashCode() {
			return username.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof TestID) {
				TestID id = (TestID) o;
				return username.equals(id.username);
			} else {
				fail();
				return false;
			}
		}
	}

	private class TestNameSpace extends Namespace {

		private static final long serialVersionUID = 1L;

		@Override
		public ID createInstance(Object[] parameters) throws IDCreateException {
			String username = (String) parameters[0];
			return new TestID(this, username);
		}

		@Override
		public String getScheme() {
			return null;
		}

	}
}
