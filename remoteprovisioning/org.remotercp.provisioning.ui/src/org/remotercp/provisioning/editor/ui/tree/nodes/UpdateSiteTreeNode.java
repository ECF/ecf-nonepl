package org.remotercp.provisioning.editor.ui.tree.nodes;

import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IURLEntry;
import org.eclipse.update.internal.ui.model.SiteBookmark;

public class UpdateSiteTreeNode extends AbstractTreeNode {

	public UpdateSiteTreeNode(Object value) {
		super(value);
	}

	@Override
	public String getLabel() {
		String text = "";
		if (getValue() instanceof IFeature) {
			IFeature feature = (IFeature) getValue();
			IURLEntry updateSiteEntry = feature.getUpdateSiteEntry();
			text = updateSiteEntry.getURL().toString();
		} else if (getValue() instanceof SiteBookmark) {
			SiteBookmark bookmark = (SiteBookmark) getValue();
			text = bookmark.getLabel();
		}
		return text;
	}

}
