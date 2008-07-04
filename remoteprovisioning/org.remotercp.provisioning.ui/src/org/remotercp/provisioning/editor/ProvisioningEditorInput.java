package org.remotercp.provisioning.editor;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ProvisioningEditorInput implements IEditorInput {

	public static final int BUNDLE = 0;

	public static final int FEATURE = 1;

	private int artifact;

	private ID[] userIDs;

	public ProvisioningEditorInput(ID[] userIds) {
		this.userIDs = userIds;
	}

	public void setArtifactToShow(int artifact) {
		this.artifact = artifact;
	}

	public int getArtifactToShow() {
		return this.artifact;
	}

	public ID[] getUserIDs() {
		return userIDs;
	}

	public boolean exists() {
		// nothing to do
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// nothing to do
		return null;
	}

	public String getName() {
		return "Provisioning Editor";
	}

	public IPersistableElement getPersistable() {
		// nothing to do
		return null;
	}

	public String getToolTipText() {
		return "Open the provisioning editor";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		// nothing to do
		return null;
	}

	public boolean equals(Object obj) {
		ProvisioningEditorInput input = (ProvisioningEditorInput) obj;
		if (input.hashCode() == hashCode()) {
			return true;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int hashCode = 0;
		for (ID userId : this.userIDs) {
			hashCode += userId.getName().hashCode();
		}
		return hashCode;
	}

}
