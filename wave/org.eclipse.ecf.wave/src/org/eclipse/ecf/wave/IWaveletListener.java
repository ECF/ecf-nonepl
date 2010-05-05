/*******************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt, Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.wave;

import org.waveprotocol.wave.federation.Proto.ProtocolHashedVersion;
import org.waveprotocol.wave.model.operation.Operation;
import org.waveprotocol.wave.model.wave.data.WaveletData;

public interface IWaveletListener {
	public void notify(IWavelet wavelet, String author, Operation<WaveletData> operation);

	public void commit(IWavelet wavelet, ProtocolHashedVersion commitNotice);
}
