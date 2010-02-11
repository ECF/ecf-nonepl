/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/

package org.eclipse.ecf.internal.provider.oscar.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.oscar.ui.messages"; //$NON-NLS-1$

	public static String OSCAR_CONNECT_WIZARD_PAGE_WIZARD_TITLE;
	public static String OSCAR_CONNECT_WIZARD_PAGE_WIZARD_DESCRIPTION;
	public static String OSCAR_CONNECT_WIZARD_PAGE_LABEL_USERID;
	public static String OSCAR_CONNECT_WIZARD_PAGE_LABEL_PASSWORD;

	private Messages() {
		// Empty constructor
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
