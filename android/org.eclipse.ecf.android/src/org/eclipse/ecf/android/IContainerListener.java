/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


/**
 * Listener for objects that wish to receive events from an IContainer
 * instances.
 * 
 * @see IContainer#addListener(IContainerListener)
 * <p>
 * </p>
 * Note these methods will be called asynchronously when notifications of remote
 * changes are received by the provider implementation code. The provider is
 * free to call the methods below with an arbitrary thread, so the
 * implementation of these methods must be appropriately prepared.
 * <p>
 * </p>
 * </pre>
 * 
 * Further, the code in the implementations of these methods should <b>not block</b> via 
 * I/O operations or blocking UI calls.
 */
public interface IContainerListener {
	/**
	 * Handle event from IContainer
	 * 
	 * @param event
	 */
	public void handleEvent(IContainerEvent event);
}