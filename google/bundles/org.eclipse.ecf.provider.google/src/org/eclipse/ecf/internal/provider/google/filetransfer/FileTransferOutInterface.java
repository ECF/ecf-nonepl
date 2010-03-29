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

public class FileTransferOutInterface {

	private String username;
	private String password;
	private static FileTransferOutInterface self = null;

	private native void nativeSendFile(String username, String password, String recvJid, String filename, String rootdir);

	/**
	 * @param args
	 */

	static {
		System.loadLibrary("fileout");

	}

	class GoogleFileThread extends Thread {
		private String recvJid;
		private String filename;
		private String rootdir;

		public void run() {
			// filename = copyPasteToLocalFolder();
			nativeSendFile(username, password, recvJid, filename, rootdir);

		}

		public GoogleFileThread(String recvJid, String filename, String rootdir) {
			this.recvJid = recvJid;
			this.filename = filename;
			this.rootdir = rootdir;
		}

	}

	public static FileTransferOutInterface getDefault(GoogleFileTransferHelper googleOutgoingFileTransferHelper) {
		if (self == null) {
			self = new FileTransferOutInterface(googleOutgoingFileTransferHelper);
		}
		return self;
	}

	public String copyPasteToLocalFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public void createConnection(String username, String password) {
		this.username = username;
		this.password = password;
	}

	private FileTransferOutInterface(GoogleFileTransferHelper googleOutgoingFileTransferHelper) {
	}

	public void sendFile(String recvJid, String filename) {

		String rootdir = filename.substring(0, filename.lastIndexOf("\\") + 1);
		filename = filename.substring(filename.lastIndexOf(System.getProperty("file.separator")) + 1, filename.length());

		GoogleFileThread thread = new GoogleFileThread(recvJid, filename, rootdir);
		thread.run();
	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

}
