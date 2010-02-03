/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;


public class TCPServerSOContainerGroup extends SOContainerGroup implements ISocketAcceptHandler {

	public static final String INVALID_CONNECT = Messages.TCPServerSOContainerGroup_Invalid_Connect_Request;
	public static final String DEFAULT_GROUP_NAME = TCPServerSOContainerGroup.class.getName();
	private int port;
	private Server listener;
	private boolean isOnTheAir = false;
	private final ThreadGroup threadGroup;

	public TCPServerSOContainerGroup(String name, ThreadGroup group, int port) {
		super(name);
		threadGroup = group;
		this.port = port;
	}

	public TCPServerSOContainerGroup(String name, int port) {
		this(name, null, port);
	}

	public TCPServerSOContainerGroup(int port) {
		this(DEFAULT_GROUP_NAME, null, port);
	}

	public synchronized void putOnTheAir() throws IOException {
		listener = new Server(threadGroup, port, this);
		port = listener.getLocalPort();
		isOnTheAir = true;
	}

	public synchronized boolean isOnTheAir() {
		return isOnTheAir;
	}

	private void setSocketOptions(Socket aSocket) throws SocketException {
		aSocket.setTcpNoDelay(true);
	}

	public void handleAccept(Socket aSocket) throws Exception {
		// Set socket options
		setSocketOptions(aSocket);
		final ObjectOutputStream oStream = new ObjectOutputStream(aSocket.getOutputStream());
		oStream.flush();
		final ObjectInputStream iStream = new ObjectInputStream(aSocket.getInputStream());
		final ConnectRequestMessage req = (ConnectRequestMessage) iStream.readObject();
		if (req == null)
			throw new InvalidObjectException(INVALID_CONNECT + Messages.TCPServerSOContainerGroup_Exception_Connect_Request_Null);
		final URI uri = req.getTarget();
		if (uri == null)
			throw new InvalidObjectException(INVALID_CONNECT + Messages.TCPServerSOContainerGroup_Target_Null);
		final String path = uri.getPath();
		if (path == null)
			throw new InvalidObjectException(INVALID_CONNECT + Messages.TCPServerSOContainerGroup_Target_Path_Null);
		final TCPServerSOContainer srs = (TCPServerSOContainer) get(path);
		if (srs == null)
			throw new InvalidObjectException(Messages.TCPServerSOContainerGroup_Container_For_Target + path + Messages.TCPServerSOContainerGroup_Not_Found);
		// Create our local messaging interface
		final Client newClient = new Client(aSocket, iStream, oStream, srs.getReceiver());
		// No other threads can access messaging interface until space has
		// accepted/rejected
		// connect request
		synchronized (newClient) {
			// Call checkConnect
			final Serializable resp = srs.handleConnectRequest(aSocket, path, req.getData(), newClient);
			// Create connect response wrapper and send it back
			oStream.writeObject(new ConnectResultMessage(resp));
			oStream.flush();
		}
	}

	public synchronized void takeOffTheAir() {
		if (listener != null) {
			try {
				listener.close();
			} catch (final IOException e) {
// TODO				traceStack("Exception in closeListener", e); //$NON-NLS-1$
			}
			listener = null;
		}
		isOnTheAir = false;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return super.toString() + ";port:" + port; //$NON-NLS-1$
	}
}