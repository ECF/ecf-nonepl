/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;


public class RemoteServiceReferenceImpl implements IRemoteServiceReference {

	protected RemoteServiceRegistrationImpl registration;

	protected String clazz = null;

	public RemoteServiceReferenceImpl(RemoteServiceRegistrationImpl registration) {
		this.registration = registration;
	}

	public Object getProperty(String key) {
		return registration.getProperty(key);
	}

	public String[] getPropertyKeys() {
		return registration.getPropertyKeys();
	}

	public ID getContainerID() {
		return registration.getContainerID();
	}

	public boolean isActive() {
		return (registration != null);
	}

	protected synchronized void setInactive() {
		registration = null;
		clazz = null;
	}

	protected RemoteServiceRegistrationImpl getRegistration() {
		return registration;
	}

	protected void setRemoteClass(String clazz) {
		this.clazz = clazz;
	}

	protected String getRemoteClass() {
		return clazz;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("RemoteServiceReferenceImpl["); //$NON-NLS-1$
		buf.append("class=").append(getRemoteClass()).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("registration=").append(getRegistration()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return buf.toString();
	}

	/**
	 * @since 3.0
	 */
	public IRemoteServiceID getID() {
		return registration.getID();
	}
}
