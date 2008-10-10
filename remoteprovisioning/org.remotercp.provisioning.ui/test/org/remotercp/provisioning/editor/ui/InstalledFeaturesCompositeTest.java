package org.remotercp.provisioning.editor.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.DifferentFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.InstalledFeaturesTreeCreator;
import org.remotercp.provisioning.editor.ui.tree.InstalledFeaturesTest;

public class InstalledFeaturesCompositeTest extends InstalledFeaturesTest {

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

	public void setUp() {

		userFeatures = new HashMap<ID, Collection<SerializedFeatureWrapper>>();
		allFeatures = new ArrayList<SerializedFeatureWrapper>();

		sandra = super.createUserID("Sandra");
		john = super.createUserID("John");
		klaus = super.createUserID("Klaus");

		feature10 = getFeaturesWrapper(10, "Feature 10",
				"org.eclipse.feature10", "1.0");
		feature11 = getFeaturesWrapper(11, "Feature 11",
				"org.eclipse.feature11", "1.0");
		feature12 = getFeaturesWrapper(12, "Feature 12",
				"org.eclipse.feature12", "1.0");
		feature13 = getFeaturesWrapper(13, "Feature 13",
				"org.eclipse.feature13", "1.0");
		feature14 = getFeaturesWrapper(14, "Feature 14",
				"org.eclipse.feature14", "1.0");
		feature15 = getFeaturesWrapper(15, "Feature 15",
				"org.eclipse.feature15", "1.0");
		feature16 = getFeaturesWrapper(16, "Feature 16",
				"org.eclipse.feature16", "1.0");
		feature17 = getFeaturesWrapper(17, "Feature 17",
				"org.eclipse.feature17", "1.0");
		feature18 = getFeaturesWrapper(18, "Feature 18",
				"org.eclipse.feature18", "1.0");
		feature19 = getFeaturesWrapper(19, "Feature 19",
				"org.eclipse.feature19", "1.0");
		feature20 = getFeaturesWrapper(20, "Feature 20",
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

		String url = "http://eugenda.eu.funpic.de/upload/";
		SerializedFeatureWrapper feature = new SerializedFeatureWrapper();
		feature.setIdentifier(identyfier);
		feature.setLabel(name);
		feature.setVersion(version);
		try {
			feature.setUpdateUrl(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return feature;

	}

	public Collection<CommonFeaturesTreeNode> getDummyCommonFeatures() {
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

	public Collection<DifferentFeaturesTreeNode> getDummyDifferentFeatures() {
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

		public void startServices() {
			// TODO Auto-generated method stub

		}

	}

	public ID getJohn() {
		return john;
	}

	public ID getSandra() {
		return sandra;
	}

	public ID getKlaus() {
		return klaus;
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
