package org.remotercp.provisioning.editor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.junit.Before;
import org.junit.Test;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.FeaturesSetOperationHelper;
import org.remotercp.util.roster.AbstractRosterGenerator;

public class InstalledFeaturesTest extends AbstractRosterGenerator {

	private Map<ID, Collection<SerializedFeatureWrapper>> userFeatures;

	private ID sandra;

	private ID john;

	private ID klaus;

	@Before
	public void setUp() {

		userFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();

		sandra = super.createUserID("Sandra");
		john = super.createUserID("John");
		klaus = super.createUserID("Klaus");

		SerializedFeatureWrapper feature10 = getFeaturesWrapper(10,
				"org.eclipse.feature10", "1.0");
		SerializedFeatureWrapper feature11 = getFeaturesWrapper(11,
				"org.eclipse.feature11", "1.0");
		SerializedFeatureWrapper feature12 = getFeaturesWrapper(12,
				"org.eclipse.feature12", "1.0");
		SerializedFeatureWrapper feature13 = getFeaturesWrapper(13,
				"org.eclipse.feature13", "1.0");
		SerializedFeatureWrapper feature14 = getFeaturesWrapper(14,
				"org.eclipse.feature14", "1.0");
		SerializedFeatureWrapper feature15 = getFeaturesWrapper(15,
				"org.eclipse.feature15", "1.0");
		SerializedFeatureWrapper feature16 = getFeaturesWrapper(16,
				"org.eclipse.feature16", "1.0");
		SerializedFeatureWrapper feature17 = getFeaturesWrapper(17,
				"org.eclipse.feature17", "1.0");
		SerializedFeatureWrapper feature18 = getFeaturesWrapper(18,
				"org.eclipse.feature18", "1.0");
		SerializedFeatureWrapper feature19 = getFeaturesWrapper(19,
				"org.eclipse.feature19", "1.0");
		SerializedFeatureWrapper feature20 = getFeaturesWrapper(20,
				"org.eclipse.feature20", "1.0");

		Collection<SerializedFeatureWrapper> johnsFeatures = new ArrayList<SerializedFeatureWrapper>();
		johnsFeatures.add(feature10);
		johnsFeatures.add(feature11);
		johnsFeatures.add(feature12);
		johnsFeatures.add(feature13);
		johnsFeatures.add(feature14);
		johnsFeatures.add(feature15);
		johnsFeatures.add(feature16);
		johnsFeatures.add(feature17);

		Collection<SerializedFeatureWrapper> sandrasFeatures = new ArrayList<SerializedFeatureWrapper>();
		sandrasFeatures.add(feature10);
		sandrasFeatures.add(feature11);
		sandrasFeatures.add(feature12);
		sandrasFeatures.add(feature13);
		sandrasFeatures.add(feature14);
		sandrasFeatures.add(feature15);
		sandrasFeatures.add(feature16);
		sandrasFeatures.add(feature17);
		sandrasFeatures.add(feature18);
		sandrasFeatures.add(feature19);
		sandrasFeatures.add(feature20);

		Collection<SerializedFeatureWrapper> klausFeatures = new ArrayList<SerializedFeatureWrapper>();
		klausFeatures.add(feature11);
		klausFeatures.add(feature12);
		klausFeatures.add(feature13);
		klausFeatures.add(feature14);
		klausFeatures.add(feature15);
		klausFeatures.add(feature16);
		klausFeatures.add(feature17);

		userFeatures.put(sandra, sandrasFeatures);
		userFeatures.put(klaus, klausFeatures);
		userFeatures.put(john, johnsFeatures);
	}

	protected SerializedFeatureWrapper getFeaturesWrapper(int id, String name,
			String version) {
		SerializedFeatureWrapper bundle = new SerializedFeatureWrapper();
		bundle.setIdentifier(name);
		// label is the displayed name in UI, for now it doesn matter what it is
		bundle.setLabel(name);
		bundle.setVersion(version);

		return bundle;
	}

	@Test
	public void testCommonFeatures() {
		FeaturesSetOperationHelper helper = new FeaturesSetOperationHelper();

		Collection<SerializedFeatureWrapper> klausFeatures = userFeatures
				.get(klaus);
		Collection<SerializedFeatureWrapper> sandraFeatures = userFeatures
				.get(sandra);
		Collection<SerializedFeatureWrapper> johnFeatures = userFeatures
				.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausFeatures, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraFeatures, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnFeatures, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		helper.handleInstalledFeatures(services, new MyProgressMonitor());

		Set<SerializedFeatureWrapper> commonFeatures = helper
				.getCommonArtifacts();

		assertEquals(7, commonFeatures.size());
		// this bundles must be in commonBundles
		SerializedFeatureWrapper feature11 = getFeaturesWrapper(11,
				"org.eclipse.feature11", "1.0");
		SerializedFeatureWrapper feature12 = getFeaturesWrapper(12,
				"org.eclipse.feature12", "1.0");
		SerializedFeatureWrapper feature13 = getFeaturesWrapper(13,
				"org.eclipse.feature13", "1.0");
		SerializedFeatureWrapper feature14 = getFeaturesWrapper(14,
				"org.eclipse.feature14", "1.0");
		SerializedFeatureWrapper featue15 = getFeaturesWrapper(15,
				"org.eclipse.feature15", "1.0");
		SerializedFeatureWrapper feature16 = getFeaturesWrapper(16,
				"org.eclipse.feature16", "1.0");
		SerializedFeatureWrapper feature17 = getFeaturesWrapper(17,
				"org.eclipse.feature17", "1.0");

		assertTrue(commonFeatures.contains(feature11));
		assertTrue(commonFeatures.contains(feature12));
		assertTrue(commonFeatures.contains(feature13));
		assertTrue(commonFeatures.contains(feature14));
		assertTrue(commonFeatures.contains(featue15));
		assertTrue(commonFeatures.contains(feature16));
		assertTrue(commonFeatures.contains(feature17));

		// this bundles do not have to be in the collection
		SerializedFeatureWrapper feature10 = getFeaturesWrapper(10,
				"org.eclipse.feature10", "1.0");
		SerializedFeatureWrapper feature18 = getFeaturesWrapper(18,
				"org.eclipse.feature18", "1.0");
		SerializedFeatureWrapper feature19 = getFeaturesWrapper(19,
				"org.eclipse.feature19", "1.0");
		SerializedFeatureWrapper feature20 = getFeaturesWrapper(20,
				"org.eclipse.feature20", "1.0");

		assertFalse(commonFeatures.contains(feature10));
		assertFalse(commonFeatures.contains(feature18));
		assertFalse(commonFeatures.contains(feature19));
		assertFalse(commonFeatures.contains(feature20));

	}

	@Test
	public void testDifferentBundles() {
		FeaturesSetOperationHelper helper = new FeaturesSetOperationHelper();

		Collection<SerializedFeatureWrapper> klausFeatures = userFeatures
				.get(klaus);
		Collection<SerializedFeatureWrapper> sandraFeatures = userFeatures
				.get(sandra);
		Collection<SerializedFeatureWrapper> johnFeatures = userFeatures
				.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausFeatures, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraFeatures, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnFeatures, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		helper.handleInstalledFeatures(services, new MyProgressMonitor());

		Set<SerializedFeatureWrapper> differentFeatures = helper
				.getDifferentArtifacts();

		// this bundles have to be in the collection
		SerializedFeatureWrapper feature10 = getFeaturesWrapper(10,
				"org.eclipse.feature10", "1.0");
		SerializedFeatureWrapper feature18 = getFeaturesWrapper(18,
				"org.eclipse.feature18", "1.0");
		SerializedFeatureWrapper feature19 = getFeaturesWrapper(19,
				"org.eclipse.feature19", "1.0");
		SerializedFeatureWrapper feature20 = getFeaturesWrapper(20,
				"org.eclipse.feature20", "1.0");

		assertTrue(differentFeatures.contains(feature10));
		assertTrue(differentFeatures.contains(feature18));
		assertTrue(differentFeatures.contains(feature19));
		assertTrue(differentFeatures.contains(feature20));

		// this bundles don't have to be in the set
		SerializedFeatureWrapper feature11 = getFeaturesWrapper(11,
				"org.eclipse.feature11", "1.0");
		SerializedFeatureWrapper feature12 = getFeaturesWrapper(12,
				"org.eclipse.feature12", "1.0");
		SerializedFeatureWrapper features13 = getFeaturesWrapper(13,
				"org.eclipse.feature13", "1.0");
		SerializedFeatureWrapper feature14 = getFeaturesWrapper(14,
				"org.eclipse.feature14", "1.0");
		SerializedFeatureWrapper feature15 = getFeaturesWrapper(15,
				"org.eclipse.feature15", "1.0");
		SerializedFeatureWrapper feature16 = getFeaturesWrapper(16,
				"org.eclipse.feature16", "1.0");
		SerializedFeatureWrapper feature17 = getFeaturesWrapper(17,
				"org.eclipse.feature17", "1.0");

		assertFalse(differentFeatures.contains(feature11));
		assertFalse(differentFeatures.contains(feature12));
		assertFalse(differentFeatures.contains(features13));
		assertFalse(differentFeatures.contains(feature14));
		assertFalse(differentFeatures.contains(feature15));
		assertFalse(differentFeatures.contains(feature16));
		assertFalse(differentFeatures.contains(feature17));

	}

	@Test
	public void testDifferentFeaturesToUserRelationship() {
		Collection<SerializedFeatureWrapper> klausFeatures = userFeatures
				.get(klaus);
		Collection<SerializedFeatureWrapper> sandraFeatures = userFeatures
				.get(sandra);
		Collection<SerializedFeatureWrapper> johnFeatures = userFeatures
				.get(john);

		IInstalledFeaturesService klausService = new TestRemoteServiceListImpl(
				klausFeatures, klaus);
		IInstalledFeaturesService sandraService = new TestRemoteServiceListImpl(
				sandraFeatures, sandra);
		IInstalledFeaturesService johnService = new TestRemoteServiceListImpl(
				johnFeatures, john);

		List<IInstalledFeaturesService> services = new ArrayList<IInstalledFeaturesService>();
		services.add(klausService);
		services.add(sandraService);
		services.add(johnService);

		FeaturesSetOperationHelper helper = new FeaturesSetOperationHelper();
		helper.handleInstalledFeatures(services, new MyProgressMonitor());

		Map<SerializedFeatureWrapper, Collection<ID>> differentBundleToUser = helper
				.getDifferentArtifactToUser();

		// in total this bundles are different
		String feature10 = "org.eclipse.feature10";
		String feature18 = "org.eclipse.feature18";
		String feature19 = "org.eclipse.feature19";
		String feature20 = "org.eclipse.feature20";

		assertEquals(4, differentBundleToUser.size());
		if (differentBundleToUser.keySet() == null) {
			fail();
		}

		// john has bundles 10 - 17
		Collection<ID> userJohn = getFeature(feature10, differentBundleToUser);
		assertTrue(userJohn.contains(john));
		Collection<ID> userJohn1 = getFeature(feature18, differentBundleToUser);
		assertFalse(userJohn1.contains(john));
		Collection<ID> userJohn2 = getFeature(feature19, differentBundleToUser);
		assertFalse(userJohn2.contains(john));
		Collection<ID> userJohn3 = getFeature(feature20, differentBundleToUser);
		assertFalse(userJohn3.contains(john));

		// klaus has bundles 11 - 17, so he doesn't have to apper
		for (Collection<ID> userIDs : differentBundleToUser.values()) {
			assertFalse(userIDs.contains(klaus));
		}

		// sandra has bundles 10 - 20
		Collection<ID> userSandra1 = getFeature(feature10,
				differentBundleToUser);
		assertTrue(userSandra1.contains(sandra));

		Collection<ID> userSandra2 = getFeature(feature18,
				differentBundleToUser);
		assertTrue(userSandra2.contains(sandra));

		Collection<ID> userSandra3 = getFeature(feature19,
				differentBundleToUser);
		assertTrue(userSandra3.contains(sandra));

		Collection<ID> userSandra4 = getFeature(feature20,
				differentBundleToUser);
		assertTrue(userSandra4.contains(sandra));

	}

	private Collection<ID> getFeature(String id,
			Map<SerializedFeatureWrapper, Collection<ID>> differentBundleToUser) {

		for (SerializedFeatureWrapper bundle : differentBundleToUser.keySet()) {
			if (bundle.getLabel().equals(id)) {
				return differentBundleToUser.get(bundle);
			}
		}
		return null;
	}

	@Test
	public void testDifferentFeatureVersions() {
		FeaturesSetOperationHelper helper = new FeaturesSetOperationHelper();

		SerializedFeatureWrapper feature1 = getFeaturesWrapper(1,
				"org.eclipse.feature1", "1.0");
		SerializedFeatureWrapper feature2 = getFeaturesWrapper(1,
				"org.eclipse.feature2", "1.0");
		SerializedFeatureWrapper feature3 = getFeaturesWrapper(1,
				"org.eclipse.feature3", "1.0");
		SerializedFeatureWrapper feature4 = getFeaturesWrapper(1,
				"org.eclipse.feature4", "1.0");
		SerializedFeatureWrapper feature5 = getFeaturesWrapper(1,
				"org.eclipse.feature5", "1.0");

		Set<SerializedFeatureWrapper> allFeatures = new HashSet<SerializedFeatureWrapper>();
		allFeatures.add(feature1);
		allFeatures.add(feature2);
		allFeatures.add(feature3);
		allFeatures.add(feature4);
		allFeatures.add(feature5);

		ID klaus = createUserID("Klaus");

		SerializedFeatureWrapper klausFeature1 = getFeaturesWrapper(1,
				"org.eclipse.feature1", "1.1");
		SerializedFeatureWrapper klausFeature2 = getFeaturesWrapper(1,
				"org.eclipse.feature2", "1.0");
		SerializedFeatureWrapper klausFeature3 = getFeaturesWrapper(1,
				"org.eclipse.feature3", "1.3");

		Collection<SerializedFeatureWrapper> klausCollection = new ArrayList<SerializedFeatureWrapper>();
		klausCollection.add(klausFeature1);
		klausCollection.add(klausFeature2);
		klausCollection.add(klausFeature3);

		Map<ID, Collection<SerializedFeatureWrapper>> klausFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();
		klausFeatures.put(klaus, klausCollection);

		// klaus has 2 features with different versions
		Map<SerializedFeatureWrapper, Collection<ID>> differentFeatureVersionsToUser = helper
				.getDifferentFeatureVersionsToUser(klausFeatures, allFeatures);
		assertEquals(2, differentFeatureVersionsToUser.size());

		// make sure that both feature1 and feature3 are in the map
		Collection<ID> u1 = getFeature(klausFeature1.getIdentifier(), differentFeatureVersionsToUser);
		assertNotNull(u1);
		assertEquals(true, u1.contains(klaus));
		
		Collection<ID> u2 = getFeature(klausFeature3.getIdentifier(), differentFeatureVersionsToUser);
		assertNotNull(u2);
		assertEquals(true, u2.contains(klaus));
		
		Collection<ID> u3 = getFeature(klausFeature2.getIdentifier(), differentFeatureVersionsToUser);
		assertNull(u3);
	}

	private class TestRemoteServiceListImpl implements
			IInstalledFeaturesService {

		private final Collection<SerializedFeatureWrapper> features;
		private final ID userID;

		public TestRemoteServiceListImpl(
				Collection<SerializedFeatureWrapper> features, ID userID) {
			this.features = features;
			this.userID = userID;
		}

		public Collection<SerializedBundleWrapper> getInstalledBundles() {
			return null;
		}

		public ID getUserID() {
			return userID;
		}

		public String getUserInfo() {
			return null;
		}

		public Collection<SerializedFeatureWrapper> getInstalledFeatures() {
			return features;
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
