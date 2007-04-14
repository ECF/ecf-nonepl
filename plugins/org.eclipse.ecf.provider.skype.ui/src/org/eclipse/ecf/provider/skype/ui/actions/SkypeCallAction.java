package org.eclipse.ecf.provider.skype.ui.actions;

import org.eclipse.ecf.call.ui.actions.AbstractCallAction;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.provider.skype.ui.Activator;
import org.eclipse.ecf.internal.provider.skype.ui.Messages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SkypeCallAction extends AbstractCallAction {

	static ImageDescriptor skypeIcon = AbstractUIPlugin
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					Messages.SkypeCallAction_Call_Image_Icon_Name);

	public SkypeCallAction(ID skypeReceiver, String text, String tooltip) {
		this.setCallReceiver(skypeReceiver);
		this.setText(text);
		this.setToolTipText(tooltip);
		this.setImageDescriptor(skypeIcon);
	}

	protected IContainer getContainer() {
		return SkypeOpenAction.getContainer();
	}

}