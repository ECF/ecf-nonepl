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

package org.eclipse.ecf.provider.jms.container;

import java.util.Map;

import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jms.identity.JMSID;

/**
 * JMS Container configuration.
 */
public class JMSContainerConfig extends SOContainerConfig {

	private int keepAlive = AbstractJMSServer.DEFAULT_KEEPALIVE;

	/**
	 * @param id.
	 *            The new JMSID for this container. Must not be
	 *            <code>null</code>.
	 * @param keepAlive.
	 *            Keep alive for this JMS container.
	 * @param props.
	 *            Any properties for this config. May be <code>null</code>.
	 */
	public JMSContainerConfig(JMSID id, int keepAlive, Map props) {
		super(id, props);
		this.keepAlive = keepAlive;
	}

	/**
	 * @param id.
	 *            The new JMSID for this container. Must not be
	 *            <code>null</code>.
	 * @param keepAlive.
	 *            Keep alive for this JMS container.
	 */
	public JMSContainerConfig(JMSID id, int keepAlive) {
		super(id);
		this.keepAlive = keepAlive;
	}

	/**
	 * @param id.
	 *            The new JMSID for this container. Must not be
	 *            <code>null</code>.
	 */
	public JMSContainerConfig(JMSID id) {
		this(id, AbstractJMSServer.DEFAULT_KEEPALIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.jms.container.IJMSContainerConfig#getKeepAlive()
	 */
	public int getKeepAlive() {
		return keepAlive;
	}

}