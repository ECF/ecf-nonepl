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

package org.eclipse.ecf.android;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class Server extends ServerSocket {

	public static final int DEFAULT_BACKLOG = 50;

	protected static final String TAG = "Server";

	ISocketAcceptHandler acceptHandler;
	Thread listenerThread;
	ThreadGroup threadGroup;

	public Server(ThreadGroup group, int port, ISocketAcceptHandler handler) throws IOException {
		super(port, DEFAULT_BACKLOG);
		if (handler == null)
			throw new InstantiationError(Messages.Server_Listener_Not_Null);
		acceptHandler = handler;
		threadGroup = group;
		listenerThread = setupListener();
		listenerThread.start();
	}

	public Server(int port, ISocketAcceptHandler handler) throws IOException {
		this(null, port, handler);
	}

	protected Thread setupListener() {
		return new Thread(threadGroup, new Runnable() {
			public void run() {
				while (true) {
					try {
						handleAccept(accept());
					} catch (Exception e) {
						Log.e(TAG, "Exception in accept", e);
						// If we get an exception on accept(), we should just exit
						break;
					}
				}
				Log.i(TAG, "Closing listener normally."); 
			}
		}, "ServerApplication(" + getLocalPort() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void handleAccept(final Socket aSocket) {
		new Thread(threadGroup, new Runnable() {
			public void run() {
				try {
					Log.i("Server", "accept:" + aSocket.getInetAddress());
					acceptHandler.handleAccept(aSocket);
				} catch (Exception e) {
					Log.e(Server.TAG, "Unexpected exception in handleAccept...closing", e);
					try {
						aSocket.close();
					} catch (IOException e1) {
						Log.e(TAG, "accept.close", e1);					}
				}
			}
		}).start();
	}

	@Override
	public synchronized void close() throws IOException {
		super.close();
		if (listenerThread != null) {
			listenerThread.interrupt();
			listenerThread = null;
		}
		if (threadGroup != null) {
			threadGroup.interrupt();
			threadGroup = null;
		}
		acceptHandler = null;
	}
}