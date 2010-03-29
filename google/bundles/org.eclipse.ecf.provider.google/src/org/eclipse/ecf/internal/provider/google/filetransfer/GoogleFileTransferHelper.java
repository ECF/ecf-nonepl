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

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.provider.google.identity.GoogleNamespace;
import org.eclipse.ecf.provider.xmpp.identity.*;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;

public class GoogleFileTransferHelper implements ISendFileTransferContainerAdapter {

	public static FileTransferOutInterface fileOutInterface;
	public static FileTransferInInterface fileInInterface;

	Vector<IIncomingFileTransferRequestListener> listeners = new Vector<IIncomingFileTransferRequestListener>();

	public void addListener(IIncomingFileTransferRequestListener listener) {
		listeners.add(listener);
	}

	public FileTransferInInterface getFileInInterface() {
		return fileInInterface;
	}

	public void fireIncomingFileTransferRequest(final String filename, final String senderJid) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			((IIncomingFileTransferRequestListener) it.next()).handleFileTransferRequest(new IFileTransferRequestEvent() {

				private boolean accepted = false;

				public IIncomingFileTransfer accept(File localFileToSave) throws IncomingFileTransferException {
					// TODO Auto-generated method stub
					System.out.println("Came to file accept");
					fileInInterface.acceptFile(true);
					accepted = true;
					return null;
				}

				public IIncomingFileTransfer accept(OutputStream outputStream, IFileTransferListener listener) throws IncomingFileTransferException {
					System.out.println("Came to file accept2");
					// fileInInterface.fileAccept = true;
					// fileInInterface.fileAcceptLock = false;
					fileInInterface.acceptFile(true);
					accepted = true;
					return null;
				}

				public IFileTransferInfo getFileTransferInfo() {
					return null;
				}

				public ID getRequesterID() {
					// TODO Auto-generated method stub
					try {
						return new XMPPID(new GoogleNamespace(), senderJid);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}

				public void reject() {
					// fileInInterface.fileAccept = false;
					// fileInInterface.fileAcceptLock = false;
					fileInInterface.acceptFile(false);
					accepted = true;
				}

				public boolean requestAccepted() {
					// TODO Auto-generated method stub
					return accepted;
				}

			});
		}

	}

	public Namespace getOutgoingNamespace() {
		// TODO Auto-generated method stub
		return IDFactory.getDefault().getNamespaceByName(XMPPFileNamespace.NAME);
	}

	public GoogleFileTransferHelper(IContainer container) {
		ICallSessionRequestListener listener;

		fileOutInterface = FileTransferOutInterface.getDefault(this);
		fileInInterface = FileTransferInInterface.getDefault(this);
	}

	public boolean removeListener(IIncomingFileTransferRequestListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
			return true;
		}
		return false;
	}

	public void sendOutgoingRequest(IFileID targetReceiver, File localFileToSend, IFileTransferListener transferListener, Map options) throws SendFileTransferException {
		sendOutgoingRequest(targetReceiver, new FileTransferInfo(localFileToSend), transferListener, options);
	}

	public void sendOutgoingRequest(IFileID targetReceiver, IFileTransferInfo localFileToSend, IFileTransferListener transferListener, Map options) throws SendFileTransferException {

		if (!(targetReceiver instanceof XMPPFileID))
			throw new SendFileTransferException("target receiver not XMPPFileID type.");

		final XMPPFileID fileID = (XMPPFileID) targetReceiver;
		String receiver = fileID.getXMPPID().getName();
		String filename = fileID.getFilename();
		fileOutInterface.sendFile(receiver, filename);
	}

	public void setConnectContextForAuthentication(IConnectContext connectContext) {
		// TODO Auto-generated method stub

	}

	public void setProxy(Proxy proxy) {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Callback[] createAuthorizationCallbacks() {
		final Callback[] cbs = new Callback[1];
		cbs[0] = new ObjectCallback();
		return cbs;
	}

	protected Object getConnectData(ID remote, IConnectContext joinContext) throws IOException, UnsupportedCallbackException {
		final Callback[] callbacks = createAuthorizationCallbacks();
		if (joinContext != null && callbacks != null && callbacks.length > 0) {
			final CallbackHandler handler = joinContext.getCallbackHandler();
			if (handler != null) {
				handler.handle(callbacks);
			}
			if (callbacks[0] instanceof ObjectCallback) {
				final ObjectCallback cb = (ObjectCallback) callbacks[0];
				return cb.getObject();
			}
		}
		return null;
	}

	public void createConnection(final ID remote, IConnectContext joinContext) {
		try {
			final Object connectData = getConnectData(remote, joinContext);
			fileInInterface.createConnection(remote.getName(), (String) connectData);

			fileOutInterface.createConnection(remote.getName(), (String) connectData);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}

	}

	public void disconnect() {
		fileInInterface.disconnect();
		fileOutInterface.disconnect();
	}

	public void acceptFile(boolean b) {
		fileInInterface.acceptFile(b);
	}

	public void setFileSaveLocation(String direc) {
		fileInInterface.setRootDir(direc);

	}

}
