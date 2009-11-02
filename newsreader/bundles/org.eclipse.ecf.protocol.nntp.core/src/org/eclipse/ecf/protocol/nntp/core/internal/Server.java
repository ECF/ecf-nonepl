/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.core.internal;

import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerConnection;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;

public class Server implements IServer {

	private final String address;

	private final int port;

	private final boolean secure;

	private IServerConnection connection;

	private String organization;

	private String[] overviewHeaders;

	/**
	 * Note that this is an internal class. A server factory cannot be far away.
	 * 
	 * @param address
	 * @param port
	 * @param secure
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public Server(String address, int port, boolean secure)
			throws NNTPIOException, UnexpectedResponseException {
		this.address = address;
		this.port = port;
		this.secure = secure;

	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
		return address;
	}

	public String toString() {
		return getAddress() + "::" + getPort() + "::"
				+ getServerConnection().getUser() + "::"
				+ getServerConnection().getEmail() + "::"
				+ getServerConnection().getLogin() + "::" + isSecure();
	}

	public boolean equals(Object obj) {
		if (obj instanceof IServer)
			return toString().equals(obj.toString());
		return super.equals(obj);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public boolean isSecure() {
		return secure;
	}

	public IServerConnection getServerConnection() {
		return connection;
	}

	public boolean isAnonymous() {
		return getServerConnection().getLogin() == null;
	}

	public void init() throws NNTPException {
		connection.connect();
		connection.setModeReader(this);

	}

	public void setServerConnection(IServerConnection connection) {
		this.connection = connection;
	}

	public String getOrganization() {
		return organization;
	}

	public String[] getOverviewHeaders() {
		return overviewHeaders;
	}

	public void setOverviewHeaders(String[] headers) {
		overviewHeaders = headers;
	}
}
