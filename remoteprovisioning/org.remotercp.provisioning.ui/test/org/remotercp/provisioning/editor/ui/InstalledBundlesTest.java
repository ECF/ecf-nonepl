package org.remotercp.provisioning.editor.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.ArtifactsSetOperationHelper;
import org.remotercp.util.roster.AbstractRosterGenerator;

public class InstalledBundlesTest extends AbstractRosterGenerator {

	private Map<ID, Collection<SerializedBundleWrapper>> userBundles;

	private ID sandra;

	private ID john;

	private ID klaus;

	@Before
	public void setUp() {

		userBundles = new HashMap<ID, Collection<SerializedBundleWrapper>>();
		// differentBundles = new TreeSet<SerializedBundleWrapper>();

		sandra = super.createUserID("Sandra");
		john = super.createUserID("John");
		klaus = super.createUserID("Klaus");

		SerializedBundleWrapper bundle10 = getBundleWrapper(10, Bundle.ACTIVE,
				"org.eclipse.bundle10");
		SerializedBundleWrapper bundle11 = getBundleWrapper(11, Bundle.ACTIVE,
				"org.eclipse.bundle11");
		SerializedBundleWrapper bundle12 = getBundleWrapper(12, Bundle.ACTIVE,
				"org.eclipse.bundle12");
		SerializedBundleWrapper bundle13 = getBundleWrapper(13, Bundle.ACTIVE,
				"org.eclipse.bundle13");
		SerializedBundleWrapper bundle14 = getBundleWrapper(14, Bundle.ACTIVE,
				"org.eclipse.bundle14");
		SerializedBundleWrapper bundle15 = getBundleWrapper(15, Bundle.ACTIVE,
				"org.eclipse.bundle15");
		SerializedBundleWrapper bundle16 = getBundleWrapper(16, Bundle.ACTIVE,
				"org.eclipse.bundle16");
		SerializedBundleWrapper bundle17 = getBundleWrapper(17, Bundle.ACTIVE,
				"org.eclipse.bundle17");
		SerializedBundleWrapper bundle18 = getBundleWrapper(18, Bundle.ACTIVE,
				"org.eclipse.bundle18");
		SerializedBundleWrapper bundle19 = getBundleWrapper(19, Bundle.ACTIVE,
				"org.eclipse.bundle19");
		SerializedBundleWrapper bundle20 = getBundleWrapper(20, Bundle.ACTIVE,
				"org.eclipse.bundle20");

		Collection<SerializedBundleWrapper> johnsBundles = new ArrayList<SerializedBundleWrapper>();
		johnsBundles.add(bundle10);
		johnsBundles.add(bundle11);
		johnsBundles.add(bundle12);
		johnsBundles.add(bundle13);
		johnsBundles.add(bundle14);
		johnsBundles.add(bundle15);
		johnsBundles.add(bundle16);
		johnsBundles.add(bundle17);

		Collection<SerializedBundleWrapper> sandrasBundles = new ArrayList<SerializedBundleWrapper>();
		sandrasBundles.add(bundle10);
		sandrasBundles.add(bundle11);
		sandrasBundles.add(bundle12);
		sandrasBundles.add(bundle13);
		sandrasBundles.add(bundle14);
		sandrasBundles.add(bundle15);
		sandrasBundles.add(bundle16);
		sandrasBundles.add(bundle17);
		sandrasBundles.add(bundle18);
		sandrasBundles.add(bundle19);
		sandrasBundles.add(bundle20);

		Collection<SerializedBundleWrapper> klausBundles = new ArrayList<SerializedBundleWrapper>();
		klausBundles.add(bundle11);
		klausBundles.add(bundle12);
		klausBundles.add(bundle13);
		klausBundles.add(bundle14);
		klausBundles.add(bundle15);
		klausBundles.add(bundle16);
		klausBundles.add(bundle17);

		userBundles.put(sandra, sandrasBundles);
		userBundles.put(klaus, klausBundles);
		userBundles.put(john, johnsBundles);
	}

	protected SerializedBundleWrapper getBundleWrapper(long id, int state,
			String name) {
		SerializedBundleWrapper bundle = new SerializedBundleWrapper();
		bundle.setBundleId(id);
		bundle.setState(state);
		bundle.setIdentifier(name);

		return bundle;
	}

	@Test
	public void testCommonBundles() {
		ArtifactsSetOperationHelper<SerializedBundleWrapper> helper = new ArtifactsSetOperationHelper<SerializedBundleWrapper>();

		Collection<SerializedBundleWrapper> klausBundles = userBundles
				.get(klaus);
		Collection<SerializedBundleWrapper> sandraBundles = userBundles
				.get(sandra);
		Collection<SerializedBundleWrapper> johnBundles = userBundles.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausBundles, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraBundles, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnBundles, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		helper.handleInstalledArtifacts(services,
				SerializedBundleWrapper.class, new MyProgressMonitor());

		Set<SerializedBundleWrapper> commonBundles = helper
				.getCommonArtifacts();

		assertEquals(7, commonBundles.size());
		// this bundles must be in commonBundles
		SerializedBundleWrapper bundle11 = getBundleWrapper(11, Bundle.ACTIVE,
				"org.eclipse.bundle11");
		SerializedBundleWrapper bundle12 = getBundleWrapper(12, Bundle.ACTIVE,
				"org.eclipse.bundle12");
		SerializedBundleWrapper bundle13 = getBundleWrapper(13, Bundle.ACTIVE,
				"org.eclipse.bundle13");
		SerializedBundleWrapper bundle14 = getBundleWrapper(14, Bundle.ACTIVE,
				"org.eclipse.bundle14");
		SerializedBundleWrapper bundle15 = getBundleWrapper(15, Bundle.ACTIVE,
				"org.eclipse.bundle15");
		SerializedBundleWrapper bundle16 = getBundleWrapper(16, Bundle.ACTIVE,
				"org.eclipse.bundle16");
		SerializedBundleWrapper bundle17 = getBundleWrapper(17, Bundle.ACTIVE,
				"org.eclipse.bundle17");

		assertTrue(commonBundles.contains(bundle11));
		assertTrue(commonBundles.contains(bundle12));
		assertTrue(commonBundles.contains(bundle13));
		assertTrue(commonBundles.contains(bundle14));
		assertTrue(commonBundles.contains(bundle15));
		assertTrue(commonBundles.contains(bundle16));
		assertTrue(commonBundles.contains(bundle17));

		// this bundles do not have to be in the collection
		SerializedBundleWrapper bundle10 = getBundleWrapper(10, Bundle.ACTIVE,
				"org.eclipse.bundle10");
		SerializedBundleWrapper bundle18 = getBundleWrapper(18, Bundle.ACTIVE,
				"org.eclipse.bundle18");
		SerializedBundleWrapper bundle19 = getBundleWrapper(19, Bundle.ACTIVE,
				"org.eclipse.bundle19");
		SerializedBundleWrapper bundle20 = getBundleWrapper(20, Bundle.ACTIVE,
				"org.eclipse.bundle20");

		assertFalse(commonBundles.contains(bundle10));
		assertFalse(commonBundles.contains(bundle18));
		assertFalse(commonBundles.contains(bundle19));
		assertFalse(commonBundles.contains(bundle20));

	}

	@Test
	public void testDifferentBundles() {
		ArtifactsSetOperationHelper<SerializedBundleWrapper> helper = new ArtifactsSetOperationHelper<SerializedBundleWrapper>();

		Collection<SerializedBundleWrapper> klausBundles = userBundles
				.get(klaus);
		Collection<SerializedBundleWrapper> sandraBundles = userBundles
				.get(sandra);
		Collection<SerializedBundleWrapper> johnBundles = userBundles.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausBundles, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraBundles, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnBundles, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		helper.handleInstalledArtifacts(services,
				SerializedBundleWrapper.class, new MyProgressMonitor());

		Set<SerializedBundleWrapper> differentBundles = helper
				.getDifferentArtifacts();

		// this bundles have to be in the collection
		SerializedBundleWrapper bundle10 = getBundleWrapper(10, Bundle.ACTIVE,
				"org.eclipse.bundle10");
		SerializedBundleWrapper bundle18 = getBundleWrapper(18, Bundle.ACTIVE,
				"org.eclipse.bundle18");
		SerializedBundleWrapper bundle19 = getBundleWrapper(19, Bundle.ACTIVE,
				"org.eclipse.bundle19");
		SerializedBundleWrapper bundle20 = getBundleWrapper(20, Bundle.ACTIVE,
				"org.eclipse.bundle20");

		assertTrue(differentBundles.contains(bundle10));
		assertTrue(differentBundles.contains(bundle18));
		assertTrue(differentBundles.contains(bundle19));
		assertTrue(differentBundles.contains(bundle20));

		// this bundles don't have to be in the set
		SerializedBundleWrapper bundle11 = getBundleWrapper(11, Bundle.ACTIVE,
				"org.eclipse.bundle11");
		SerializedBundleWrapper bundle12 = getBundleWrapper(12, Bundle.ACTIVE,
				"org.eclipse.bundle12");
		SerializedBundleWrapper bundle13 = getBundleWrapper(13, Bundle.ACTIVE,
				"org.eclipse.bundle13");
		SerializedBundleWrapper bundle14 = getBundleWrapper(14, Bundle.ACTIVE,
				"org.eclipse.bundle14");
		SerializedBundleWrapper bundle15 = getBundleWrapper(15, Bundle.ACTIVE,
				"org.eclipse.bundle15");
		SerializedBundleWrapper bundle16 = getBundleWrapper(16, Bundle.ACTIVE,
				"org.eclipse.bundle16");
		SerializedBundleWrapper bundle17 = getBundleWrapper(17, Bundle.ACTIVE,
				"org.eclipse.bundle17");

		assertFalse(differentBundles.contains(bundle11));
		assertFalse(differentBundles.contains(bundle12));
		assertFalse(differentBundles.contains(bundle13));
		assertFalse(differentBundles.contains(bundle14));
		assertFalse(differentBundles.contains(bundle15));
		assertFalse(differentBundles.contains(bundle16));
		assertFalse(differentBundles.contains(bundle17));

	}

	@Test
	public void testDifferentBundlesToUserRelationship() {
		Collection<SerializedBundleWrapper> klausBundles = userBundles
				.get(klaus);
		Collection<SerializedBundleWrapper> sandraBundles = userBundles
				.get(sandra);
		Collection<SerializedBundleWrapper> johnBundles = userBundles.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausBundles, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraBundles, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnBundles, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		ArtifactsSetOperationHelper<SerializedBundleWrapper> helper = new ArtifactsSetOperationHelper<SerializedBundleWrapper>();
		helper.handleInstalledArtifacts(services,
				SerializedBundleWrapper.class, new MyProgressMonitor());

		Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser = helper
				.getDifferentArtifactToUser();

		// in total this bundles are different
		String bundle10 = "org.eclipse.bundle10";
		String bundle18 = "org.eclipse.bundle18";
		String bundle19 = "org.eclipse.bundle19";
		String bundle20 = "org.eclipse.bundle20";

		assertEquals(4, differentBundleToUser.size());
		if (differentBundleToUser.keySet() == null) {
			fail();
		}

		// john has bundles 10 - 17
		Collection<ID> userJohn = getBundle(bundle10, differentBundleToUser);
		assertTrue(userJohn.contains(john));
		Collection<ID> userJohn1 = getBundle(bundle18, differentBundleToUser);
		assertFalse(userJohn1.contains(john));
		Collection<ID> userJohn2 = getBundle(bundle19, differentBundleToUser);
		assertFalse(userJohn2.contains(john));
		Collection<ID> userJohn3 = getBundle(bundle20, differentBundleToUser);
		assertFalse(userJohn3.contains(john));

		// klaus has bundles 11 - 17, so he doesn't have to apper
		for (Collection<ID> userIDs : differentBundleToUser.values()) {
			assertFalse(userIDs.contains(klaus));
		}

		// sandra has bundles 10 - 20
		Collection<ID> userSandra1 = getBundle(bundle10, differentBundleToUser);
		assertTrue(userSandra1.contains(sandra));

		Collection<ID> userSandra2 = getBundle(bundle18, differentBundleToUser);
		assertTrue(userSandra2.contains(sandra));

		Collection<ID> userSandra3 = getBundle(bundle19, differentBundleToUser);
		assertTrue(userSandra3.contains(sandra));

		Collection<ID> userSandra4 = getBundle(bundle20, differentBundleToUser);
		assertTrue(userSandra4.contains(sandra));

	}

	private Collection<ID> getBundle(String id,
			Map<SerializedBundleWrapper, Collection<ID>> differentBundleToUser) {

		for (SerializedBundleWrapper bundle : differentBundleToUser.keySet()) {
			if (bundle.getIdentifier().equals(id)) {
				return differentBundleToUser.get(bundle);
			}
		}
		return null;
	}

	private class TestRemoteServiceListImpl implements
			IInstalledFeaturesService {

		private final Collection<SerializedBundleWrapper> bundles;
		private final ID userID;

		public TestRemoteServiceListImpl(
				Collection<SerializedBundleWrapper> bundles, ID userID) {
			this.bundles = bundles;
			this.userID = userID;
		}

		public Collection<SerializedBundleWrapper> getInstalledBundles() {
			return bundles;
		}

		public ID getUserID() {
			return userID;
		}

		public String getUserInfo() {
			return null;
		}

		public Collection<SerializedFeatureWrapper> getInstalledFeatures() {
			return null;
		}

	}

	private class MyProgressMonitor implements IProgressMonitor {

		public void beginTask(String name, int totalWork) {

		}

		public void done() {

		}

		public void internalWorked(double work) {

		}

		public boolean isCanceled() {
			return false;
		}

		public void setCanceled(boolean value) {

		}

		public void setTaskName(String name) {

		}

		public void subTask(String name) {

		}

		public void worked(int work) {

		}

	}

}
