/*******************************************************************************
 * Copyright (c) 2007 Moritz Post and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.ecf.tests.internal.provider.jingle;

import org.eclipse.osgi.util.NLS;

/**
 * The configuration options to use in the code.
 */
public class Config extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.ecf.tests.internal.provider.jingle.config"; //$NON-NLS-1$

	public static String XMPP_CONTAINER_NAME;
	public static String XMPP_NAMESPACE;
	public static String XMPP_ACCOUNT_USER;
	public static String XMPP_ACCOUNT_PASSWORD;
	public static String XMPP_ACCOUNT_REMOTE_USER;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Config.class);
	}

	private Config() {
		// empty constructor
	}
}
