/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.SharedObjectDescription;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.provider.datashare.SharedObjectDatashareContainerAdapter;

/**
 *
 */
public class SkypeChannelContainerAdapter extends
		SharedObjectDatashareContainerAdapter {

	ISharedObjectContainer container = null;
	
	/**
	 * @param container
	 */
	public SkypeChannelContainerAdapter(ISharedObjectContainer container) {
		this.container = container;
	}

	protected SharedObjectDescription createChannelSharedObjectDescription(
			final IChannelConfig channelConfig) throws ECFException {
		
		return new SharedObjectDescription(SkypeA2AChannel.class, channelConfig.getID(), channelConfig.getProperties());
	}
}
