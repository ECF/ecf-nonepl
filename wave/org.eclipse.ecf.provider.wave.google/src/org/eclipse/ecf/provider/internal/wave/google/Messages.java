/****************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sebastian Schmidt <mail@schmidt-seb.de> - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.internal.wave.google;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.wave.google.messages"; //$NON-NLS-1$

	public static String WaveBackendNamespace_ParameterIsNull;
	public static String WaveBackendNamespace_InvalidParameter;
	public static String WaveBackendNamespace_InvalidEmailParameter;
	public static String WaveBackendNamespace_InvalidServerParameter;

	private Messages() {
		// private null constructor
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
