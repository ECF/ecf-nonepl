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

import org.eclipse.ecf.provider.wave.google.container.WaveClientContainer;
import org.eclipse.ecf.provider.wave.google.identity.WaveID;

public class CommonConstants {
	public static final String WAVE_PROTOCOL_PREFIX = "wave://";

	public static WaveID INDEX_WAVE_ID;
	
	static {
		WaveClientContainer container = new WaveClientContainer();
		INDEX_WAVE_ID = (WaveID) container.getWaveNamespace().createInstance(new String[] { "indexwave", "indexwave" });
	}
}
