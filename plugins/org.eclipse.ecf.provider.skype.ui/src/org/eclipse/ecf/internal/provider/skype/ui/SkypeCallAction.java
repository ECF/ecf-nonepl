package org.eclipse.ecf.internal.provider.skype.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.telephony.call.ui.actions.AbstractCallAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SkypeCallAction extends AbstractCallAction {

	static ImageDescriptor skypeIcon = AbstractUIPlugin
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					Messages.SkypeCallAction_Call_Image_Icon_Name);

	IContainer container;
	
	public SkypeCallAction(IContainer container, ID skypeReceiver, String text, String tooltip) {
		this.container = container;
		this.setCallReceiver(skypeReceiver);
		this.setText(text);
		this.setToolTipText(tooltip);
		this.setImageDescriptor(skypeIcon);
	}

	protected IContainer getContainer() {
		return container;
	}

}