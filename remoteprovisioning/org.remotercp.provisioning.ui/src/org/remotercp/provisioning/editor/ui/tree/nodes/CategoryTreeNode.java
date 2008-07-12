package org.remotercp.provisioning.editor.ui.tree.nodes;

import org.eclipse.update.core.ICategory;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IURLEntry;

public class CategoryTreeNode extends AbstractTreeNode implements
		Comparable<CategoryTreeNode> {

	public CategoryTreeNode(Object value) {
		super(value);
	}

	@Override
	public String getLabel() {
		IFeature feature = (IFeature) getValue();
		IURLEntry updateSiteEntry = feature.getUpdateSiteEntry();
		ICategory[] categories = feature.getSite().getCategories();
		/*
		 * TODO: How do you exactly determine to which category a feature
		 * belongs???
		 */
		if (categories != null) {
			return categories[0].getLabel();
		} else {
			return updateSiteEntry.getAnnotation();
		}
	}

	public int compareTo(CategoryTreeNode node) {
		return getLabel().compareTo(node.getLabel());
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CategoryTreeNode) {
			CategoryTreeNode node = (CategoryTreeNode) object;
			if (node.getLabel().equals(getLabel())) {
				return true;
			}
		}
		return false;
	}
}
