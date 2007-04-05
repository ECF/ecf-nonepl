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

package org.eclipse.ecf.internal.provider.skype.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.skype.ui.messages"; //$NON-NLS-1$
	public static String SkypeOpenAction_3;
	public static String SkypeOpenAction_4;
	public static String SkypeOpenAction_Initiate_Skype_Call_Message;
	public static String SkypeOpenAction_Initiate_Skype_Call_Title;
	public static String SkypeOpenAction_Message_Title_Call_Failed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
