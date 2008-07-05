package org.remotercp.provisioning.editor.ui.tree;

import java.util.ArrayList;

import static junit.framework.Assert.*;

import org.eclipse.jface.viewers.TreeNode;
import org.junit.Test;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.ui.AbstractFeaturesGenerator;

public class CommonFeaturesTreeNodeTest extends AbstractFeaturesGenerator {

	@Test
	public void testAddChild() {
		SerializedFeatureWrapper feature = getFeaturesWrapper(1, "Feature 1",
				"com.eclipse.feature1", "1.1");
		
		SerializedFeatureWrapper newFeature = getFeaturesWrapper(1, "Feature new",
				"com.eclipse.featureNew", "1.1");

		CommonFeaturesUserTreeNode userNode1 = new CommonFeaturesUserTreeNode(
				feature);

		CommonFeaturesUserTreeNode userNode2 = new CommonFeaturesUserTreeNode(
				feature);
		CommonFeaturesUserTreeNode userNode3 = new CommonFeaturesUserTreeNode(
				feature);
		
		CommonFeaturesUserTreeNode userNode4 = new CommonFeaturesUserTreeNode(
				newFeature);

		java.util.List<CommonFeaturesUserTreeNode> children = new ArrayList<CommonFeaturesUserTreeNode>();
		children.add(userNode1);
		children.add(userNode2);
		children.add(userNode3);

		CommonFeaturesTreeNode treeNode = new CommonFeaturesTreeNode(feature);
		treeNode.setChildren(children.toArray(new TreeNode[children.size()]));

		assertEquals(3, treeNode.getChildren().length);
		
		treeNode.addChild(userNode4);
		assertEquals(4, treeNode.getChildren().length);
		CommonFeaturesUserTreeNode child = treeNode.getChildren()[3];
		assertEquals("com.eclipse.featureNew", ((SerializedFeatureWrapper)child.getValue()).getIdentifier());

	}

}
