/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google.filetransfer;

public class FileTransferInInterface {

	private String username;
	private String password;
	private static GoogleFileThread thread;
	private GoogleFileTransferHelper transferHelper;

	private static String LOGOUT = "LOGOUT";
	private static FileTransferInInterface self = null;
	public String rootDir;
	public boolean fileAcceptLock;
	public boolean fileAccept;

	private native void nativeListen(String username, String password, String rootdir);

	private native void nativeFileAccept(boolean bool);

	private native void nativeDisconnect();

	/**
	 * @param args
	 */

	public void acceptFile(boolean bool) {
		thread.fileAccept(bool);
	}

	public boolean callbackFileReceive(String jid, String filename) {

		System.out.println("JID: " + jid + " Filename: " + filename);
		transferHelper.fireIncomingFileTransferRequest(filename, jid);
		// transferHelper.acceptFile(true);
		// acceptFile(true);
		return false;

	}

	public void callback(String arg) {
		if (arg.equals(LOGOUT)) {
			run();
		}
		System.out.println("C++:" + arg);

	}

	static {
		System.loadLibrary("filein");

	}

	class GoogleFileThread extends Thread {

		public void run() {
			nativeListen(username, password, rootDir);
		}

		public void fileAccept(boolean accept) {
			nativeFileAccept(accept);
		}

		public GoogleFileThread() {
		}

		public void disconnect() {
			nativeDisconnect();
		}

	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
		if (thread != null) {
		}
	}

	public static FileTransferInInterface getDefault(GoogleFileTransferHelper googleFileTransferHelper) {
		if (self == null) {
			self = new FileTransferInInterface(googleFileTransferHelper);
		}
		return self;
	}

	public void createConnection(String username, String password) {
		this.username = username;
		this.password = password;
		run();
	}

	public FileTransferInInterface(GoogleFileTransferHelper googleFileTransferHelper) {
		this.transferHelper = googleFileTransferHelper;
	}

	public void run() {
		thread = new GoogleFileThread();
		thread.start();
	}

	public void disconnect() {
		thread.disconnect();
	}

}
