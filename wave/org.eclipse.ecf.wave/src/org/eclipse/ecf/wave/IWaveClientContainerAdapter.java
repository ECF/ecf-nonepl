/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.wave;

import org.eclipse.ecf.core.identity.Namespace;

public interface IWaveClientContainerAdapter {

	public IWaveClientView getIndexWaveClientView();
	public Namespace getWaveNamespace();
	public Namespace getWaveletNamespace();
	public Namespace getParticipantNamespace();
	public Namespace getDocumentNamespace();
}
