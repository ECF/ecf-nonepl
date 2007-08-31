/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jgroups.identity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.jgroups.Address;

/**
 *
 */
public class JGroupsID extends BaseID {

	private static final long serialVersionUID = -1237654704481532873L;

	private final String channelName;

	private Address address = null;

	public JGroupsID(Namespace ns, String channelName) {
		super(ns);
		this.channelName = channelName;
		Assert.isNotNull(this.channelName);
	}

	public String getChannelName() {
		return channelName;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf.core.identity.BaseID)
	 */
	protected int namespaceCompareTo(BaseID o) {
		if (!(o instanceof JGroupsID))
			return -1;
		return channelName.compareTo(((JGroupsID) o).channelName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceEquals(org.eclipse.ecf.core.identity.BaseID)
	 */
	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof JGroupsID))
			return false;
		return channelName.equals(((JGroupsID) o).channelName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceGetName()
	 */
	protected String namespaceGetName() {
		return channelName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceHashCode()
	 */
	protected int namespaceHashCode() {
		return channelName.hashCode();
	}

	public String toString() {
		final StringBuffer buf = new StringBuffer("JGroupsID[");
		buf.append(getName()).append("]");
		return buf.toString();
	}
}
