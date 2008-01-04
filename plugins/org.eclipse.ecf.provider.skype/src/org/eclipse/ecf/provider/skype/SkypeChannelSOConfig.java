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

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.provider.generic.SOConfig;
import org.eclipse.ecf.provider.generic.SOContainer;

import com.skype.Application;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 *
 */
public class SkypeChannelSOConfig extends SOConfig {

	private Application application = null;

	private final String REMOVE_PREFIX = "org.eclipse."; //$NON-NLS-1$
	private final int MAXAPPNAMELENGTH = 32;

	/**
	 * Clean app name so that it's not too long.  The Skype docs say max length of
	 * app name is 32 bytes.  See reference here:
	 * 
	 * https://developer.skype.com/Docs/ApiDoc/Application_to_application_commands
	 */
	protected String cleanAppName(String appName) {
		if (appName.length() <= MAXAPPNAMELENGTH)
			return appName;
		else if (appName.startsWith(REMOVE_PREFIX)) {
			return cleanAppName(appName.substring(REMOVE_PREFIX.length()));
		} else
			return appName.substring(0, MAXAPPNAMELENGTH);
	}

	/**
	 * @param sharedObjectID
	 * @param homeContainerID
	 * @param cont
	 * @param dict
	 * @throws ECFException 
	 */
	public SkypeChannelSOConfig(ID sharedObjectID, ID homeContainerID, SOContainer cont, Map dict) throws ECFException {
		super(sharedObjectID, homeContainerID, cont, dict);
		// Setup application here
		try {
			application = Skype.addApplication(cleanAppName(sharedObjectID.getName()));
		} catch (final SkypeException e) {
			throw new ECFException(Messages.SkypeChannelSOConfig_EXCEPTION_COULD_NOT_CREATE_APP, e);
		}
	}

	protected void makeActive(IQueueEnqueue queue) {
		isActive = true;
		if (container.getID().equals(homeContainerID)) {
			this.context = new SkypeSOContext(application, sharedObjectID, homeContainerID, container, properties, queue);
		} else
			super.makeActive(queue);
	}

}
