package org.remotercp.provisioning.editor.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesTreeNode;
import org.remotercp.provisioning.editor.ui.tree.CommonFeaturesUserTreeNode;

public class FeaturesVersionsCompositeTest extends AbstractFeaturesGenerator {

	@Test
	public void testGetFeatureVersions() {

		Display display = new Display();
		Shell shell = new Shell(display);

		FeaturesVersionsComposite comp = new FeaturesVersionsComposite(shell,
				SWT.None) {
			@Override
			protected void createPartControl(Composite parent, int style) {
				// do nothing
			}
		};

		SerializedFeatureWrapper feature1 = getFeaturesWrapper(1, "Feature1",
				"org.eclipse.feature1", "1.0");

		SerializedFeatureWrapper feature2 = getFeaturesWrapper(1, "Feature1",
				"org.eclipse.feature1", "1.1");

		SerializedFeatureWrapper feature3 = getFeaturesWrapper(1, "Feature1",
				"org.eclipse.feature1", "1.2");

		SerializedFeatureWrapper feature4 = getFeaturesWrapper(1, "Feature1",
				"org.eclipse.feature1", "1.2");

		SerializedFeatureWrapper feature5 = getFeaturesWrapper(1, "Feature2",
				"org.eclipse.feature2", "2.0");

		SerializedFeatureWrapper feature6 = getFeaturesWrapper(1, "Feature2",
				"org.eclipse.feature2", "2.1");

		SerializedFeatureWrapper feature7 = getFeaturesWrapper(1, "Feature2",
				"org.eclipse.feature2", "2.2");

		SerializedFeatureWrapper feature8 = getFeaturesWrapper(1, "Feature3",
				"org.eclipse.feature3", "3.0");

		SerializedFeatureWrapper feature9 = getFeaturesWrapper(1, "Feature3",
				"org.eclipse.feature3", "3.0");

		CommonFeaturesUserTreeNode userNode1 = new CommonFeaturesUserTreeNode(
				feature1);
		CommonFeaturesUserTreeNode userNode2 = new CommonFeaturesUserTreeNode(
				feature2);
		CommonFeaturesUserTreeNode userNode3 = new CommonFeaturesUserTreeNode(
				feature3);
		CommonFeaturesUserTreeNode userNode4 = new CommonFeaturesUserTreeNode(
				feature4);
		List<TreeNode> children = new ArrayList<TreeNode>();
		children.add(userNode1);
		children.add(userNode2);
		children.add(userNode3);
		children.add(userNode4);
		CommonFeaturesTreeNode treeNode = new CommonFeaturesTreeNode(feature1);
		treeNode.setChildren(children.toArray(new TreeNode[children.size()]));

		String greatestFeatureVersion = comp
				.getGreatestFeatureVersion(treeNode);
		assertNotNull(greatestFeatureVersion);
		assertEquals("1.2", greatestFeatureVersion);

		userNode1 = new CommonFeaturesUserTreeNode(feature5);
		userNode2 = new CommonFeaturesUserTreeNode(feature6);
		userNode3 = new CommonFeaturesUserTreeNode(feature7);

		children.clear();
		children.add(userNode1);
		children.add(userNode2);
		children.add(userNode3);

		treeNode.setChildren(children.toArray(new TreeNode[children.size()]));
		greatestFeatureVersion = comp.getGreatestFeatureVersion(treeNode);
		assertNotNull(greatestFeatureVersion);
		assertEquals("2.2", greatestFeatureVersion);

		children.clear();
		userNode1 = new CommonFeaturesUserTreeNode(feature8);
		userNode2 = new CommonFeaturesUserTreeNode(feature9);
		children.add(userNode1);
		children.add(userNode2);

		treeNode.setChildren(children.toArray(new TreeNode[children.size()]));
		greatestFeatureVersion = comp.getGreatestFeatureVersion(treeNode);
		assertNotNull(greatestFeatureVersion);
		assertEquals("3.0", greatestFeatureVersion);
	}
}
