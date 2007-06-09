/****************************************************************************
 * Copyright (c) 2004 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jms.identity;

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class JMSID extends StringID {

	private static final long serialVersionUID = 3979266962767753264L;

	/**
	 * @param n
	 * @param s
	 */
	protected JMSID(Namespace n, String s) {
		super(n, s);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("JMSID["); //$NON-NLS-1$
		sb.append(getName()).append("]"); //$NON-NLS-1$
		return sb.toString();
	}

	public String getTopic() {
		return getName().substring(getName().lastIndexOf('/') + 1);
	}
}
