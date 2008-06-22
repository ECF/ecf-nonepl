package org.remotercp.util.roster;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.RosterEntry;

public class AbstractRosterGenerator {

	public IRosterEntry createRosterEntry(String rosterName,
			IRosterGroup group, IPresence.Type presencetype) {
		TestNameSpace namespace = new TestNameSpace();
		ID userID = null;

		try {
			userID = namespace.createInstance(new String[] { rosterName });
		} catch (IDCreateException e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull(userID);

		IUser user = new User(userID, rosterName);

		IPresence presence = new Presence(presencetype);

		RosterEntry rosterEntry = new RosterEntry(group, user, presence);

		return rosterEntry;
	}

	public ID createUserID(String userName) {
		TestNameSpace namespace = new TestNameSpace();
		ID userID = null;
		try {
			userID = namespace.createInstance(new String[] { userName });
		} catch (IDCreateException e) {
			e.printStackTrace();
			fail();
		}

		return userID;
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

		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			TestID compareID = (TestID) o;
			return this.getName().compareTo(compareID.getName());
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
