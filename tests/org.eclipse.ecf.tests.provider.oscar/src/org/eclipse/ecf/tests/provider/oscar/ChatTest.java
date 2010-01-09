/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.oscar;

import org.eclipse.ecf.tests.presence.AbstractChatTest;

public class ChatTest extends AbstractChatTest {

	protected String getClientContainerName() {
		return OSCAR.CONTAINER_NAME;
	}
}
