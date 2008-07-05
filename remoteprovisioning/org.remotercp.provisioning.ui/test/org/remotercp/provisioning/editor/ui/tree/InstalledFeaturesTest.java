package org.remotercp.provisioning.editor.ui.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.junit.Before;
import org.junit.Test;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.util.roster.AbstractRosterGenerator;

public class InstalledFeaturesTest extends AbstractRosterGenerator {

	private Map<ID, Collection<SerializedFeatureWrapper>> userFeatures;

	private Collection<SerializedFeatureWrapper> allFeatures;

	private ID sandra;

	private ID john;

	private ID klaus;

	private SerializedFeatureWrapper feature10;

	private SerializedFeatureWrapper feature11;

	private SerializedFeatureWrapper feature12;

	private SerializedFeatureWrapper feature13;

	private SerializedFeatureWrapper feature14;

	private SerializedFeatureWrapper feature15;

	private SerializedFeatureWrapper feature16;

	private SerializedFeatureWrapper feature17;

	private SerializedFeatureWrapper feature18;

	private SerializedFeatureWrapper feature19;

	private SerializedFeatureWrapper feature20;

	@Before
	public void setUp() {

		userFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();
		allFeatures = new ArrayList<SerializedFeatureWrapper>();

		sandra = super.createUserID("Sandra");
		john = super.createUserID("John");
		klaus = super.createUserID("Klaus");

		feature10 = getFeaturesWrapper(10, "Featre 10",
				"org.eclipse.feature10", "1.0");
		feature11 = getFeaturesWrapper(11, "Featre 11",
				"org.eclipse.feature11", "1.0");
		feature12 = getFeaturesWrapper(12, "Featre 12",
				"org.eclipse.feature12", "1.0");
		feature13 = getFeaturesWrapper(13, "Featre 13",
				"org.eclipse.feature13", "1.0");
		feature14 = getFeaturesWrapper(14, "Featre 14",
				"org.eclipse.feature14", "1.0");
		feature15 = getFeaturesWrapper(15, "Featre 15",
				"org.eclipse.feature15", "1.0");
		feature16 = getFeaturesWrapper(16, "Featre 16",
				"org.eclipse.feature16", "1.0");
		feature17 = getFeaturesWrapper(17, "Featre 17",
				"org.eclipse.feature17", "1.0");
		feature18 = getFeaturesWrapper(18, "Featre 18",
				"org.eclipse.feature18", "1.0");
		feature19 = getFeaturesWrapper(19, "Featre 19",
				"org.eclipse.feature19", "1.0");
		feature20 = getFeaturesWrapper(20, "Featre 20",
				"org.eclipse.feature20", "1.0");

		allFeatures.add(feature10);
		allFeatures.add(feature11);
		allFeatures.add(feature12);
		allFeatures.add(feature13);
		allFeatures.add(feature14);
		allFeatures.add(feature15);
		allFeatures.add(feature16);
		allFeatures.add(feature17);
		allFeatures.add(feature18);
		allFeatures.add(feature19);
		allFeatures.add(feature20);

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
			String identyfier, String version) {
		SerializedFeatureWrapper bundle = new SerializedFeatureWrapper();
		bundle.setIdentifier(identyfier);
		bundle.setLabel(name);
		bundle.setVersion(version);

		return bundle;
	}

	@Test
	public void testGetFeatureFromCollection() {
		InstalledFeaturesTreeCreator creator = new InstalledFeaturesTreeCreator();
		SerializedFeatureWrapper feature1 = creator.getFeatureFromCollection(
				feature10, allFeatures);
		assertNotNull(feature1);
		assertTrue(feature1.getIdentifier().equals(feature10.getIdentifier()));

		SerializedFeatureWrapper feature2 = creator.getFeatureFromCollection(
				feature20, allFeatures);
		assertNotNull(feature2);
		assertTrue(feature2.getIdentifier().equals(feature20.getIdentifier()));

		SerializedFeatureWrapper feature3 = getFeaturesWrapper(100, "Test",
				"org.eclipse.feature100", "1.0");
		SerializedFeatureWrapper featureFromCollection = creator
				.getFeatureFromCollection(feature3, allFeatures);
		assertNull(featureFromCollection);
	}

	@Test
	public void testIsFeatureVersionDifferent() {
		InstalledFeaturesTreeCreator creator = new InstalledFeaturesTreeCreator();

		Collection<CommonFeaturesUserTreeNode> nodes = new ArrayList<CommonFeaturesUserTreeNode>();
		CommonFeaturesUserTreeNode node1 = new CommonFeaturesUserTreeNode(
				feature10);
		CommonFeaturesUserTreeNode node2 = new CommonFeaturesUserTreeNode(
				feature11);
		CommonFeaturesUserTreeNode node3 = new CommonFeaturesUserTreeNode(
				feature12);
		CommonFeaturesUserTreeNode node4 = new CommonFeaturesUserTreeNode(
				feature13);
		CommonFeaturesUserTreeNode node5 = new CommonFeaturesUserTreeNode(
				feature14);

		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);

		SerializedFeatureWrapper feature1 = getFeaturesWrapper(1, "Feature 10",
				"org.eclipse.feature10", "1.0");
		boolean featureVersionDifferent = creator.isFeatureVersionDifferent(
				nodes, feature1);
		assertFalse(featureVersionDifferent);

		SerializedFeatureWrapper feature2 = getFeaturesWrapper(1, "Feature 10",
				"org.eclipse.feature10", "1.1");
		featureVersionDifferent = creator.isFeatureVersionDifferent(nodes,
				feature2);
		assertTrue(featureVersionDifferent);

		// SerializedFeatureWrapper feature3 = getFeaturesWrapper(1,
		// "NEew Feature", "org.eclipse.feature99", "1.0");
		// featureVersionDifferent = creator.isFeatureVersionDifferent(nodes,
		// feature3);
		// assertFalse(featureVersionDifferent);
	}

	@Test
	public void testCreateCommonFeaturesNodes() {

		Collection<CommonFeaturesTreeNode> commonFeatureNodes = getDummyCommonFeatures();

		assertNotNull(commonFeatureNodes);
		// common nodes are 12, 13, 14, 15, 16, 17
		boolean feature10exist = false;
		boolean feature11exist = false;
		boolean feature12exist = false;
		boolean feature13exist = false;
		boolean feature14exist = false;
		boolean feature15exist = false;
		boolean feature16exist = false;
		boolean feature17exist = false;
		boolean feature18exist = false;
		boolean feature19exist = false;
		boolean feature20exist = false;

		for (CommonFeaturesTreeNode node : commonFeatureNodes) {
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();
			if (feature.equals(feature10)) {
				feature10exist = true;
			}
			if (feature.equals(feature11)) {
				feature11exist = true;
			}
			if (feature.equals(feature12)) {
				feature12exist = true;
			}
			if (feature.equals(feature13)) {
				feature13exist = true;
			}
			if (feature.equals(feature14)) {
				feature14exist = true;
			}
			if (feature.equals(feature15)) {
				feature15exist = true;
			}
			if (feature.equals(feature16)) {
				feature16exist = true;
			}
			if (feature.equals(feature17)) {
				feature17exist = true;
			}
			if (feature.equals(feature18)) {
				feature18exist = true;
			}
			if (feature.equals(feature19)) {
				feature19exist = true;
			}
			if (feature.equals(feature20)) {
				feature20exist = true;
			}
		}

		assertFalse(feature10exist);
		assertFalse(feature11exist);

		assertTrue(feature12exist);
		assertTrue(feature13exist);
		assertTrue(feature14exist);
		assertTrue(feature15exist);
		assertTrue(feature16exist);
		assertTrue(feature17exist);

		assertFalse(feature18exist);
		assertFalse(feature19exist);
		assertFalse(feature20exist);

	}

	@Test
	public void testCreateDifferentFeaturesNodes() {

		Collection<DifferentFeaturesTreeNode> differentFeatureNodes = getDummyDifferentFeatures();

		assertNotNull(differentFeatureNodes);
		// common nodes are 12, 13, 14, 15, 16, 17
		boolean feature10exist = false;
		boolean feature11exist = false;
		boolean feature12exist = false;
		boolean feature13exist = false;
		boolean feature14exist = false;
		boolean feature15exist = false;
		boolean feature16exist = false;
		boolean feature17exist = false;
		boolean feature18exist = false;
		boolean feature19exist = false;
		boolean feature20exist = false;

		for (DifferentFeaturesTreeNode node : differentFeatureNodes) {
			DifferentFeaturesUserTreeNode[] children = node.getChildren();

			assertNotNull(children);

			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();

			for (int child = 0; child < children.length; child++) {
				DifferentFeaturesUserTreeNode childNode = children[child];
				ID userID = (ID) childNode.getValue();

				// feature
				if (feature.equals(feature10) || feature.equals(feature11)) {
					if (userID.equals(sandra)) {
						assertTrue(childNode.hasUserFeatureInstalled());
					}

					if (userID.equals(john)) {
						assertTrue(childNode.hasUserFeatureInstalled());
					}

					if (userID.equals(klaus)) {
						assertFalse(childNode.hasUserFeatureInstalled());
					}
				}

				// only sandra has installed features 18, 19, 20
				if (feature.equals(feature18) || feature.equals(feature19)
						|| feature.equals(feature20)) {
					if (userID.equals(sandra)) {
						assertTrue(childNode.hasUserFeatureInstalled());
					}

					if (userID.equals(john)) {
						assertFalse(childNode.hasUserFeatureInstalled());
					}

					if (userID.equals(klaus)) {
						assertFalse(childNode.hasUserFeatureInstalled());
					}
				}
			}

			if (feature.equals(feature10)) {
				feature10exist = true;
			}
			if (feature.equals(feature11)) {
				feature11exist = true;
			}
			if (feature.equals(feature12)) {
				feature12exist = true;
			}
			if (feature.equals(feature13)) {
				feature13exist = true;
			}
			if (feature.equals(feature14)) {
				feature14exist = true;
			}
			if (feature.equals(feature15)) {
				feature15exist = true;
			}
			if (feature.equals(feature16)) {
				feature16exist = true;
			}
			if (feature.equals(feature17)) {
				feature17exist = true;
			}
			if (feature.equals(feature18)) {
				feature18exist = true;
			}
			if (feature.equals(feature19)) {
				feature19exist = true;
			}
			if (feature.equals(feature20)) {
				feature20exist = true;
			}
		}

		assertTrue(feature10exist);
		assertTrue(feature11exist);

		assertFalse(feature12exist);
		assertFalse(feature13exist);
		assertFalse(feature14exist);
		assertFalse(feature15exist);
		assertFalse(feature16exist);
		assertFalse(feature17exist);

		assertTrue(feature18exist);
		assertTrue(feature19exist);
		assertTrue(feature20exist);
	}

	@Test
	public void testDifferenFeatureVersions() {
		InstalledFeaturesTreeCreator creator = new InstalledFeaturesTreeCreator();

		Collection<SerializedFeatureWrapper> klausFeatures = new ArrayList<SerializedFeatureWrapper>();
		klausFeatures.add(feature14);
		klausFeatures.add(feature15);
		klausFeatures.add(feature16);
		klausFeatures.add(feature17);

		// change version
		SerializedFeatureWrapper klausFeature12 = getFeaturesWrapper(1,
				"Feature 12", "org.eclipse.feature12", "1.1");
		klausFeatures.add(klausFeature12);

		SerializedFeatureWrapper klausFeature13 = getFeaturesWrapper(1,
				"Feature 13", "org.eclipse.feature13", "1.2");
		klausFeatures.add(klausFeature13);

		Collection<SerializedFeatureWrapper> sandraFeatures = new ArrayList<SerializedFeatureWrapper>();
		sandraFeatures.add(feature10);
		sandraFeatures.add(feature11);
		sandraFeatures.add(feature12);
		sandraFeatures.add(feature13);
		sandraFeatures.add(feature14);
		sandraFeatures.add(feature15);

		SerializedFeatureWrapper sandraFeature16 = getFeaturesWrapper(1,
				"Feature 16", "org.eclipse.feature16", "1.4");
		sandraFeatures.add(sandraFeature16);

		sandraFeatures.add(feature17);
		sandraFeatures.add(feature18);
		sandraFeatures.add(feature19);
		sandraFeatures.add(feature20);

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

		creator.handleInstalledFeatures(services, new MyProgressMonitor());

		Collection<CommonFeaturesTreeNode> commonFeatureNodes = creator
				.getCommonFeaturesNodes();

		assertNotNull(commonFeatureNodes);

		for (CommonFeaturesTreeNode node : commonFeatureNodes) {
			SerializedFeatureWrapper feature = (SerializedFeatureWrapper) node
					.getValue();

			CommonFeaturesUserTreeNode[] children = node.getChildren();

			for (CommonFeaturesUserTreeNode child : children) {
				ID userId = child.getUserId();

				if (feature.equals(feature10) || feature.equals(feature11)
						|| feature.equals(feature15)
						|| feature.equals(feature17)
						|| feature.equals(feature18)
						|| feature.equals(feature19)
						|| feature.equals(feature20)) {
					assertFalse(((CommonFeaturesTreeNode) child.getParent()).isVersionDifferent);
				}

				if (feature.equals(feature12)) {
					assertTrue(((CommonFeaturesTreeNode) child.getParent()).isVersionDifferent);

					// klaus has different feature12 version
					if (userId.equals(klaus)) {
						assertEquals("1.1", ((SerializedFeatureWrapper) child
								.getValue()).getVersion());
					}
				}

				if (feature.equals(feature13)) {
					assertTrue(((CommonFeaturesTreeNode) child.getParent()).isVersionDifferent);

					if (userId.equals(klaus)) {
						assertEquals("1.2", ((SerializedFeatureWrapper) child
								.getValue()).getVersion());
					}
				}

				if (feature.equals(feature16)) {
					assertTrue(((CommonFeaturesTreeNode) child.getParent()).isVersionDifferent);
					if (userId.equals(sandra)) {
						assertEquals("1.4", ((SerializedFeatureWrapper) child
								.getValue()).getVersion());
					}
				}
			}
		}
	}

	// private SerializedFeatureWrapper getFeatureByIdentyfier(String
	// identyfier,
	// Collection<SerializedFeatureWrapper> features) {
	//
	// for (SerializedFeatureWrapper feature : features) {
	// if (feature.getIdentifier().equals(identyfier)) {
	// return feature;
	// }
	// }
	// return null;
	// }

	protected Collection<CommonFeaturesTreeNode> getDummyCommonFeatures() {
		InstalledFeaturesTreeCreator creator = new InstalledFeaturesTreeCreator();

		Collection<SerializedFeatureWrapper> klausFeatures = new ArrayList<SerializedFeatureWrapper>();
		klausFeatures.add(feature14);
		klausFeatures.add(feature15);
		klausFeatures.add(feature16);
		klausFeatures.add(feature17);

		// change version
		SerializedFeatureWrapper klausFeature12 = getFeaturesWrapper(1,
				"Feature 12", "org.eclipse.feature12", "1.1");
		klausFeatures.add(klausFeature12);

		SerializedFeatureWrapper klausFeature13 = getFeaturesWrapper(1,
				"Feature 13", "org.eclipse.feature13", "1.2");
		klausFeatures.add(klausFeature13);

		Collection<SerializedFeatureWrapper> sandraFeatures = new ArrayList<SerializedFeatureWrapper>();
		sandraFeatures.add(feature10);
		sandraFeatures.add(feature11);
		sandraFeatures.add(feature12);
		sandraFeatures.add(feature13);
		sandraFeatures.add(feature14);
		sandraFeatures.add(feature15);

		SerializedFeatureWrapper sandraFeature16 = getFeaturesWrapper(1,
				"Feature 16", "org.eclipse.feature16", "1.4");
		sandraFeatures.add(sandraFeature16);

		sandraFeatures.add(feature17);
		sandraFeatures.add(feature18);
		sandraFeatures.add(feature19);
		sandraFeatures.add(feature20);

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

		creator.handleInstalledFeatures(services, new MyProgressMonitor());

		Collection<CommonFeaturesTreeNode> commonFeatureNodes = creator
				.getCommonFeaturesNodes();

		return commonFeatureNodes;
	}

	protected Collection<DifferentFeaturesTreeNode> getDummyDifferentFeatures() {
		InstalledFeaturesTreeCreator creator = new InstalledFeaturesTreeCreator();

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

		creator.handleInstalledFeatures(services, new MyProgressMonitor());

		Collection<DifferentFeaturesTreeNode> differentFeatureNodes = creator
				.getDifferentFeaturesNodes();

		return differentFeatureNodes;
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
