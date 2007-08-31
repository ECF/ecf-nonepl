/*******************************************************************************
 * Copyright (c) 2007 Moritz Post and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.ecf.tests.internal.provider.jingle;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.osgi.util.NLS;

/**
 * This singleton class provides a connected instance of an
 * {@link XMPPContainer}. It is connected to a remote xmpp server. The
 * connection details have to be provided in the messages.properties file.
 * 
 * @author Moritz Post
 */
public class XMPPContainerFactory {

	/** The instance of the {@link XMPPContainer}. */
	private XMPPContainer xmppContainer;

	/**
	 * The {@link Exception} which might have been thrown when the
	 * {@link XMPPContainer} was created.
	 */
	private Exception exception;

	/**
	 * A container for the only existing instance of this singleton.
	 */
	private static class XMPPContainerFactoryHolder {
		private final static XMPPContainerFactory SINGLETON_INSTANCE = new XMPPContainerFactory();
	}

	/**
	 * Private constructor protects public usage.
	 */
	private XMPPContainerFactory() {
		try {
			xmppContainer = (XMPPContainer) ContainerFactory.getDefault().createContainer(
					Config.bind(Config.XMPP_CONTAINER_NAME, null));
			// create id for a user to login
			IIDFactory idFactory = IDFactory.getDefault();
			Namespace xmppNamespace = idFactory.getNamespaceByName(NLS.bind(
					Config.XMPP_NAMESPACE, null));
			ID targetID = idFactory.createID(xmppNamespace, NLS.bind(Config.XMPP_ACCOUNT_USER,
					null));
			IConnectContext createPasswordConnectContext = ConnectContextFactory
					.createPasswordConnectContext(NLS.bind(Config.XMPP_ACCOUNT_PASSWORD, null));

			// connect via the container
			xmppContainer.connect(targetID, createPasswordConnectContext);
		} catch (Exception e) {
			this.exception = e;
		}
	}

	/**
	 * Returns the only instance of the {@links XMPPContainerFactory}.
	 */
	public static XMPPContainerFactory getInstance() {
		return XMPPContainerFactoryHolder.SINGLETON_INSTANCE;
	}

	/**
	 * This method provides an {@link XMPPContainer} which is connected to a
	 * remote XMPP server. The connection details have to provided in the
	 * messages.properties file.
	 * 
	 * @return a connected {@link XMPPContainer}
	 * @throws an
	 *             Exception is thrown when the {@link XMPPContainer} could not
	 *             be created. The Exception is of type:
	 *             {@link ContainerCreateException}, {@link IDCreateException}
	 *             or {@link ContainerConnectException}
	 */
	public XMPPContainer getXMPPContainer() throws Exception {
		if (exception != null) {
			throw exception;
		}
		return xmppContainer;
	}

}
