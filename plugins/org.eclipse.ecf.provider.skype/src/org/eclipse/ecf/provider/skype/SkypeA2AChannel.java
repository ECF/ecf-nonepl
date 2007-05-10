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

import org.eclipse.ecf.core.sharedobject.ISharedObjectTransactionConfig;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.provider.datashare.BaseChannel;

/**
 *
 */
public class SkypeA2AChannel extends BaseChannel {

	/**
	 * @param config
	 * @param listener
	 */
	public SkypeA2AChannel(ISharedObjectTransactionConfig config,
			IChannelListener listener) {
		super(config, listener);
	}

}
