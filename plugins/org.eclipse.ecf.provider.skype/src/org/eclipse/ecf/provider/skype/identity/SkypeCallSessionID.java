/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype.identity;

import org.eclipse.ecf.core.identity.GUID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class SkypeCallSessionID extends GUID {

	/**
	 * @param n
	 * @throws IDCreateException
	 */
	protected SkypeCallSessionID(Namespace n) throws IDCreateException {
		super(n);
	}

	private static final long serialVersionUID = -5095585845284335840L;

}
