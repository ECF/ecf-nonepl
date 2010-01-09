/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.oscar.identity;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.oscar.Messages;

public class OSCARID extends BaseID {

	private static final long serialVersionUID = -4541816266791917289L;

	private static final String OSCAR_ID_TO_STRING_FMT = "OSCARID[{0}]"; //$NON-NLS-1$

	protected String uin;

	public OSCARID(Namespace namespace, String uin) throws URISyntaxException {
		super(namespace);
		this.uin = uin;

		validateUin();
	}

	private void validateUin() throws URISyntaxException {
		if (uin == null || uin.length() < 1)
			throw new URISyntaxException(uin, Messages.OSCARID_EXCEPTION_USERNAME_NOT_NULL);

		char[] chars = uin.toCharArray();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] < '0' || chars[i] > '9')
				throw new URISyntaxException(uin, Messages.OSCARID_EXCEPTION_INVALID_UID, i);
	}

	protected int namespaceCompareTo(BaseID o) {
		return getName().compareTo(o.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof OSCARID))
			return false;
		final OSCARID other = (OSCARID) o;
		return getUin().equals(other.getUin());
	}

	protected String namespaceGetName() {
		return getUin();
	}

	protected int namespaceHashCode() {
		return getUin().hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceToExternalForm()
	 */
	protected String namespaceToExternalForm() {
		return getNamespace().getScheme() + "://" + getUin(); //$NON-NLS-1$
	}

	public String getUin() {
		return uin;
	}

	public String getName() {
		return uin;
	}

	public String toString() {
		return MessageFormat.format(OSCAR_ID_TO_STRING_FMT, new Object[] {getUin()});
	}

	public Object getAdapter(Class clazz) {
		if (clazz.isInstance(this))
			return this;

		return super.getAdapter(clazz);
	}
}
