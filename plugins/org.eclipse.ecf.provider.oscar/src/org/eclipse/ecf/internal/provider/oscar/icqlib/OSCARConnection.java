/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.icqlib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.oscar.Messages;
import org.eclipse.ecf.internal.provider.oscar.OSCARPlugin;
import org.eclipse.ecf.internal.provider.oscar.util.MessagePropertiesSerializer;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.oscar.identity.OSCARID;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.events.LoginErrorEvent;
import ru.caffeineim.protocols.icq.integration.events.StatusEvent;
import ru.caffeineim.protocols.icq.integration.listeners.*;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.tool.OscarInterface;

public class OSCARConnection implements ISynchAsynchConnection, OurStatusListener {

	private static final String ICQ_DEFAULT_HOST = "login.icq.com"; //$NON-NLS-1$

	private static final int ICQ_DEFAULT_PORT = 5190;

	public static final boolean DEBUG = Boolean.getBoolean("icqlib.debug"); //$NON-NLS-1$

	public static final boolean DUMP = Boolean.getBoolean("icqlib.dump"); //$NON-NLS-1$

	public static final String OBJECT_PROPERTY_NAME = OSCARConnection.class.getName() + ".object"; //$NON-NLS-1$

	private OscarConnection connection = null;

	private Map properties = null;

	private boolean isStarted = false;

	private boolean isConnected = false;

	private boolean isConnectError = false;

	private Namespace namespace = null;

	private String host = ICQ_DEFAULT_HOST;

	private int port = ICQ_DEFAULT_PORT;

	private SyncObject sync = new SyncObject();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == null)
			return null;

		if (adapter.isInstance(this))
			return this;

		final IAdapterManager adapterManager = OSCARPlugin.getDefault().getAdapterManager();
		return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
	}

	public OSCARConnection(Namespace namespace, String host, int port) {
		this.namespace = namespace;
		this.host = host == null ? ICQ_DEFAULT_HOST : host;
		this.port = port == 0 ? ICQ_DEFAULT_PORT : port;
	}

	private OSCARID getOSCARID(ID remote) throws ECFException {
		OSCARID uin = null;
		try {
			uin = (OSCARID) remote;
		} catch (final ClassCastException e) {
			throw new ECFException(e);
		}
		return uin;
	}

	public synchronized Object connect(ID remote, Object data, int timeout) throws ECFException {
		if (connection != null)
			throw new ECFException(Messages.OSCAR_CONNECTION_EXCEPTION_ALREADY_CONNECTED);

		final OSCARID id = getOSCARID(remote);

		String uin = id.getUin();

		try {
			connection = new OscarConnection(host, port, uin, (String) data);
			connection.getPacketAnalyser().setDebug(DEBUG);
			connection.getPacketAnalyser().setDump(DUMP);
			connection.addOurStatusListener(this);

			connection.connect();
			// Wait while connecting
			synchronized (getSyncObject()) {
				getSyncObject().wait();
			}

			if (isConnectError)
				throw new ContainerConnectException(Messages.OSCAR_CONNECTION_EXCEPTION_LOGIN_FAILED);

			isConnected = true;
		} catch (final ContainerConnectException e) {
			throw e;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new ContainerConnectException(Messages.OSCAR_CONNECTION_EXCEPTION_LOGIN_FAILED, e);
		}

		return null;
	}

	public synchronized void disconnect() {
		if (isStarted())
			stop();

		if (connection != null) {
			connection.removeOurStatusListener(this);
			connection.close();
			isConnected = false;
			connection = null;
		}
	}

	public synchronized void start() {
		isStarted = true;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public synchronized void stop() {
		isStarted = false;
	}

	public synchronized boolean isConnected() {
		return isConnected;
	}

	public synchronized ID getLocalID() {
		if (!isConnected())
			return null;

		return IDFactory.getDefault().createID(namespace.getName(), new Object[] {connection.getUserId()});
	}

	public synchronized void sendAsynch(ID receiver, byte[] data) throws IOException {
		if (data == null)
			throw new IOException(Messages.OSCAR_CONNECTION_EXCEPTION_NO_DATA);

		// XXX add virtual chatrooms (as XMPP has)?
		try {
			OscarInterface.sendExtendedMessage(connection, receiver.getName(), MessagePropertiesSerializer.serialize(
				null, serializeData(data)));
		} catch (Exception e) {
			throw new IOException(Messages.OSCAR_CHAT_EXCEPTION_SEND_FAILED);
		}
	}

	private Map serializeData(byte[] data) {
		Map properties = new HashMap();
		properties.put(OBJECT_PROPERTY_NAME, data);

		return properties;
	}

	public synchronized Object sendSynch(ID receiver, byte[] data) throws IOException {
		if (data == null)
			throw new IOException(Messages.OSCAR_CONNECTION_EXCEPTION_NO_DATA);
		return null;
	}

	public Map getProperties() {
		return properties;
	}

	public void addListener(IConnectionListener listener) {
		// XXX
	}

	public void removeListener(IConnectionListener listener) {
		// XXX
	}

	public void setConnectionFor(IOSCARConnectable connectable) {
		connectable.setConnection(connection);
	}

	public void addMessagingListener(MessagingListener listener) {
		connection.addMessagingListener(listener);
	}

	public void addUserStatusListener(UserStatusListener listener) {
		connection.addUserStatusListener(listener);
	}

	public void addOurStatusListener(OurStatusListener listener) {
		connection.addOurStatusListener(listener);
	}

	public void addXStatusListener(XStatusListener listener) {
		connection.addXStatusListener(listener);
	}

	public void addMetaAckListener(MetaAckListener listener) {
		connection.addMetaAckListener(listener);
	}

	public void addMetaInfoListener(MetaInfoListener listener) {
		connection.addMetaInfoListener(listener);
	}

	public void addContactListListener(ContactListListener listener) {
		connection.addContactListListener(listener);
	}

	public void onLogin() {
		// Connecting - continue main thread
		synchronized (getSyncObject()) {
			isConnectError = false;
			getSyncObject().notifyAll();
		}

		// TODO for testing
		OscarInterface.changeStatus(connection, new StatusModeEnum(StatusModeEnum.ONLINE));
		//ContactList.sendContatListRequest(connection);
	}

	public void onAuthorizationFailed(LoginErrorEvent e) {
		// Connecting abort - continue main thread
		synchronized (getSyncObject()) {
			isConnectError = true;
			getSyncObject().notifyAll();
		}
	}

	public void onLogout(Exception e) {
		disconnect();
	}

	public void onStatusResponse(StatusEvent e) {
		// XXX
	}

	private SyncObject getSyncObject() {
		return sync;
	}

	private static class SyncObject {
		// Empty object - for synchronization only
	}
}
