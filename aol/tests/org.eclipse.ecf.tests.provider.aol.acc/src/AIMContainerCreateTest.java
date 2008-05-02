import junit.framework.TestCase;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;

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

/**
 *
 */
public class AIMContainerCreateTest extends TestCase {

	public static final String CONTAINER_FACTORY_NAME = "ecf.aol.acc";

	public void testContainerCreate() throws Exception {
		System.out.println(System.getProperties());
		final IContainer container = ContainerFactory.getDefault().createContainer(CONTAINER_FACTORY_NAME);
		assertNotNull(container);

		container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), "scottslewis"), ConnectContextFactory.createPasswordConnectContext("naqeta"));

		synchronized (this) {
			Thread.sleep(6000);
		}
		container.disconnect();
	}

	public void testContainerConnect() throws Exception {

	}
}
