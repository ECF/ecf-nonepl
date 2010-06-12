/*******************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.util.Map;

import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface IFrameworkManagerAsync extends IAsyncRemoteServiceProxy {

	public Map getFrameworkPropertiesAsync(IAsyncCallback<Map> callback);

	public IFuture getFrameworkPropertiesAsync();

}
