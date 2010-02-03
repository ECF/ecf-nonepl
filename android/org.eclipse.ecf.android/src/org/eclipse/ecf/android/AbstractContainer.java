/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Abstract implementation of IContainer. Provides implementations of listener
 * methods that subsclasses may use to avoid having to implement them
 * themselves. This class may be subclassed as needed.
 * 
 */
public abstract class AbstractContainer implements IContainer {

	private final List containerListeners = new ArrayList(5);

	public void addListener(IContainerListener l) {
		synchronized (containerListeners) {
			containerListeners.add(l);
		}
	}

	public void removeListener(IContainerListener l) {
		synchronized (containerListeners) {
			containerListeners.remove(l);
		}
	}

	public void dispose() {
		fireContainerEvent(new ContainerDisposeEvent(getID()));
		synchronized (containerListeners) {
			containerListeners.clear();
		}
	}

	/**
	 * Fires a container event
	 * 
	 * @param event
	 */
	protected void fireContainerEvent(IContainerEvent event) {
		List toNotify = null;
		// Copy array
		synchronized (containerListeners) {
			toNotify = new ArrayList(containerListeners);
		}
		// Notify all in toNotify
		for (Iterator i = toNotify.iterator(); i.hasNext();) {
			IContainerListener l = (IContainerListener) i.next();
			l.handleEvent(event);
		}
	}

	public Object getAdapter(Class serviceType) {
		if (serviceType == null)
			return null;
		if (serviceType.isInstance(this)) {
			return this;
		}
		IAdapterManager adapterManager=null; // TODO [pierre} getAdpaterManager(); ECFPlugin.getDefault().getAdapterManager();
		return (adapterManager == null) ? null : adapterManager.loadAdapter(this, serviceType.getName());
	}

	protected String getPasswordFromConnectContext(IConnectContext connectContext) throws ContainerConnectException {
		String pw = null;
		try {
			Callback[] callbacks = new Callback[1];
			callbacks[0] = new ObjectCallback();
			if (connectContext != null) {
				CallbackHandler handler = connectContext.getCallbackHandler();
				if (handler != null) {
					handler.handle(callbacks);
				}
			}
			ObjectCallback cb = (ObjectCallback) callbacks[0];
			pw = (String) cb.getObject();
		} catch (Exception e) {
			throw new ContainerConnectException(Messages.AbstractContainer_Exception_Callback_Handler, e);
		}
		return pw;
	}

}
