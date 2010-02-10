/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.oscar;

import java.io.IOException;
import java.net.ConnectException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.internal.provider.oscar.*;
import org.eclipse.ecf.internal.provider.oscar.Messages;
import org.eclipse.ecf.internal.provider.oscar.icqlib.OSCARConnection;
import org.eclipse.ecf.internal.provider.oscar.icqlib.event.OSCARIncomingMessageEvent;
import org.eclipse.ecf.internal.provider.oscar.icqlib.event.OSCARIncomingObjectEvent;
import org.eclipse.ecf.internal.provider.oscar.icqlib.listener.*;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.search.IUserSearchManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.ecf.provider.comm.AsynchEvent;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.*;
import org.eclipse.osgi.util.NLS;

public class OSCARContainer extends ClientSOContainer implements IPresenceService {

	private String host;

	private int port;

	private OSCARChatManager chatManager = null;

	private Roster roster;

	private OSCARRosterManager rosterManager;

	protected OSCARContainer(SOContainerConfig config) throws Exception {
		super(config);

		chatManager = new OSCARChatManager();
		roster = new Roster(this);
		rosterManager = new OSCARRosterManager(roster, this);
	}

	public OSCARContainer() throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createGUID()));
	}

	public OSCARContainer(String name) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createStringID(name)));
	}

	public OSCARContainer(String name, String host) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createStringID(name)));
		this.host = host;
	}

	public OSCARContainer(String name, String host, int port) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createStringID(name)));
		this.port = port;
		this.host = host;
	}

	protected ISynchAsynchConnection createConnection(ID targetID, Object data) {
		return new OSCARConnection(getConnectNamespace(), host, port);
	}

	public IAccountManager getAccountManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IChatManager getChatManager() {
		return chatManager;
	}

	public IChatRoomManager getChatRoomManager() {
		// OSCAR do not support chat rooms
		return null;
	}

	public IUserSearchManager getUserSearchManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IRosterManager getRosterManager() {
		return rosterManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.SOContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(OSCARPlugin.getDefault().getNamespaceIdentifier());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#getConnectData(org.eclipse.ecf.core.identity.ID,
	 * 			org.eclipse.ecf.core.security.IConnectContext)
	 */
	protected Object getConnectData(ID remote, IConnectContext joinContext) throws IOException,
			UnsupportedCallbackException {
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

	protected Callback[] createAuthorizationCallbacks() {
		final Callback[] cbs = new Callback[1];
		cbs[0] = new ObjectCallback();
		return cbs;
	}

	public OSCARConnection getOSCARConnection() {
		return (OSCARConnection) super.getConnection();
	}

	protected ID handleConnectResponse(ID originalTarget, Object serverData) throws Exception {
		if (originalTarget != null && !originalTarget.equals(getID())) {
			addNewRemoteMember(originalTarget, null);

			final OSCARConnection conn = getOSCARConnection();

			// add listeners to connection
			conn.addMessagingListener(new OSCARChatMessagingListener(chatManager, getConnectNamespace()));
			conn.addMessagingListener(new OSCARSOMessagingListener(receiver, getOSCARConnection()));
			conn.addContactListListener(new OSCARRosterListener(rosterManager));

			// setting connection to managers
			conn.setConnectionFor(chatManager);

			return originalTarget;
		}

		throw new ConnectException(Messages.OSCAR_CONTAINER_EXCEPTION_INVALID_RESPONSE_FROM_SERVER);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.SOContainer#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class clazz) {
		if (clazz.equals(IPresenceContainerAdapter.class))
			return this;
		//if (clazz.equals(ISendFileTransferContainerAdapter.class))
		//	return outgoingFileTransferContainerAdapter;

		return super.getAdapter(clazz);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#connect(org.eclipse.ecf.core.identity.ID,
	 * 			org.eclipse.ecf.core.security.IConnectContext)
	 */
	public void connect(ID remote, IConnectContext joinContext) throws ContainerConnectException {
		try {
			super.connect(remote, joinContext);
			OSCARPlugin.getDefault().registerService(this);
		} catch (final ContainerConnectException e) {
			disconnect();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#disconnect()
	 */
	public void disconnect() {
		final ID groupID = getConnectedID();
		fireContainerEvent(new ContainerDisconnectingEvent(this.getID(), groupID));
		synchronized (getConnectLock()) {
			// If we are currently connected
			if (isConnected() && OSCARPlugin.getDefault() != null)
				OSCARPlugin.getDefault().unregisterService(this);

			this.connection = null;
			this.remoteServerID = null;

			// remove connection from managers
			chatManager.setConnection(null);

			// disconnect roster manager
			rosterManager.disconnect();
		}

		// notify listeners
		fireContainerEvent(new ContainerDisconnectedEvent(this.getID(), groupID));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#processAsynch(org.eclipse.ecf.provider.comm.AsynchEvent)
	 */
	protected void processAsynch(AsynchEvent e) {
		try {
			if (e instanceof OSCARIncomingMessageEvent) {
				// TODO Handle simple OSCAR message
				//handleXMPPMessage((Packet) e.getData());
				return;
			} else if (e instanceof OSCARIncomingObjectEvent) {
				// get ECF Shared object from message
				final Object obj = e.getData();
				// This should be a ContainerMessage
				final Object cm = deserializeContainerMessage((byte[]) obj);
				if (cm == null)
					throw new IOException(Messages.OSCAR_CONTAINER_EXCEPTION_DESERIALIZED_OBJECT_NULL);
				final ContainerMessage contMessage = (ContainerMessage) cm;
				final Object data = contMessage.getData();
				if (data instanceof ContainerMessage.CreateMessage) {
					handleCreateMessage(contMessage);
				} else if (data instanceof ContainerMessage.CreateResponseMessage) {
					handleCreateResponseMessage(contMessage);
				} else if (data instanceof ContainerMessage.SharedObjectMessage) {
					handleSharedObjectMessage(contMessage);
				} else if (data instanceof ContainerMessage.SharedObjectDisposeMessage) {
					handleSharedObjectDisposeMessage(contMessage);
				} else {
					debug(NLS.bind(Messages.OSCAR_CONTAINER_UNRECOGONIZED_CONTAINER_MESSAGE, contMessage));
				}
			} else {
				// Unexpected event type...
				OSCARPlugin.log(NLS.bind(Messages.OSCAR_CONTAINER_UNEXPECTED_EVENT, e), null);
			}
		} catch (final Exception except) {
			OSCARPlugin.log(NLS.bind(Messages.OSCAR_CONTAINER_EXCEPTION_HANDLING_ASYCH_EVENT, e), except);
		}
	}
}
