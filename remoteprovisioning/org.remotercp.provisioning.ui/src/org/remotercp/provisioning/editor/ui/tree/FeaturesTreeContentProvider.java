package org.remotercp.provisioning.editor.ui.tree;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;

public class FeaturesTreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		return ((TreeNode) parentElement).getChildren();
	}

	public Object getParent(Object element) {
		return ((TreeNode) element).getParent();
	}

	public boolean hasChildren(Object element) {
		boolean hasChildren = ((TreeNode) element).hasChildren();
		return hasChildren;
	}

	public Object[] getElements(Object inputElement) {
		return ((List) inputElement).toArray();
	}

	public void dispose() {
		// do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing

	}

}
