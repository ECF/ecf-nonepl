/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.android;

import java.io.Serializable;
import java.security.AccessControlException;

import org.eclipse.ecf.android.RegEx.RegularExpression;

import junit.framework.Assert;


public class AddRegistrationRequest implements Serializable {
	private static final long serialVersionUID = -2671778516104780091L;
	ID targetID;
	String service;
	RegularExpression filter;
	AccessControlException acc;
	AddRegistrationRequest parent;

	private boolean done = false;

	/**
	 * @since 3.0
	 */
	public AddRegistrationRequest(ID targetID, String service, RegularExpression filter2, AddRegistrationRequest parent) {
		this.targetID = null;
		Assert.assertNotNull(service);
		this.service = service;
		this.filter = filter2;
		this.parent = parent;
	}

	public String getService() {
		return service;
	}

	public RegularExpression getFilter() {
		return filter;
	}

	public Integer getId() {
		return new Integer(System.identityHashCode(this));
	}

	public void waitForResponse(long timeout) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeout;
		synchronized (this) {
			while (!done && (endTime >= System.currentTimeMillis())) {
				try {
					wait(timeout / 10);
				} catch (InterruptedException e) {
					// just return;
					return;
				}
			}
		}
	}

	public boolean isDone() {
		return done;
	}

	public AccessControlException getException() {
		return acc;
	}

	/**
	 * @since 3.0
	 */
	public void notifyResponse(ID from, AccessControlException exception) {
		if (targetID == null || targetID.equals(from)) {
			this.acc = exception;
			synchronized (this) {
				done = true;
				if (parent != null) {
					parent.notifyResponse(from, exception);
				} else {
					synchronized (this) {
						this.notify();
					}
				}
			}
		}
	}

}