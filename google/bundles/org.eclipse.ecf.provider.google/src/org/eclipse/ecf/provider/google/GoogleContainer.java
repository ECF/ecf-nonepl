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
package org.eclipse.ecf.provider.google;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddException;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.internal.provider.google.*;
import org.eclipse.ecf.internal.provider.google.filetransfer.GoogleFileTransferHelper;
import org.eclipse.ecf.internal.provider.google.voice.GoogleCallSessionContainerAdapter;
import org.eclipse.ecf.internal.provider.xmpp.Messages;
import org.eclipse.ecf.internal.provider.xmpp.XMPPContainerContext;
import org.eclipse.ecf.internal.provider.xmpp.events.*;
import org.eclipse.ecf.internal.provider.xmpp.smack.ECFConnection;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.*;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.osgi.util.NLS;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.XHTMLExtension;

/**
 * @since 3.0
 */
public class GoogleContainer extends XMPPContainer implements IPresenceService {

	public static final int DEFAULT_KEEPALIVE = 30000;

	public static final String CONNECT_NAMESPACE = GooglePlugin.getDefault()
			.getNamespaceIdentifier();

	public static final String CONTAINER_HELPER_ID = GoogleContainer.class
			.getName()
			+ ".googlehandler"; //$NON-NLS-1$

	protected static final String GOOGLE_SERVICENAME = "gmail.com"; //$NON-NLS-1$

	private static final String[] googleHosts = { GOOGLE_SERVICENAME,
			"talk.google.com", "googlemail.com" }; //$NON-NLS-1$ //$NON-NLS-2$

	public static final String XMPP_GOOGLE_OVERRIDE_PROP_NAME = "ecf.xmpp.google.override"; //$NON-NLS-1$

	private static Set googleNames = new HashSet();

	static {
		for (int i = 0; i < googleHosts.length; i++)
			googleNames.add(googleHosts[i]);
		final String override = System
				.getProperty(XMPP_GOOGLE_OVERRIDE_PROP_NAME);
		if (override != null)
			googleNames.add(override.toLowerCase());
	}

	protected int keepAlive = 0;

	GoogleContainerAccountManager accountManager = null;

	GoogleContainerPresenceHelper presenceHelper = null;

	protected ID presenceHelperID = null;

	private GoogleContainerNotificationManager notificationManager;

	private GoogleContainerMailManager mailManager;

	private GoogleCallSessionContainerAdapter callSessionContainerAdapter;

	private GoogleFileTransferHelper fileTransferHelper;

	private IUserSettingsSerializer userSettingSerializer;

	protected GoogleContainer(SOContainerConfig config, int keepAlive)
			throws Exception {
		super(config, keepAlive);

		this.keepAlive = keepAlive;
		accountManager = new GoogleContainerAccountManager(this);
		this.presenceHelperID = IDFactory.getDefault().createStringID(
				CONTAINER_HELPER_ID);
		presenceHelper = new GoogleContainerPresenceHelper(this);
		notificationManager = new GoogleContainerNotificationManager();
		mailManager = new GoogleContainerMailManager(this);
		callSessionContainerAdapter = new GoogleCallSessionContainerAdapter(
				this);
		fileTransferHelper = new GoogleFileTransferHelper(this);
	}

	public void setUserSettingSerializer(IUserSettingsSerializer serializer) {
		this.userSettingSerializer = serializer;
		fileTransferHelper.setFileSaveLocation(serializer
				.getSetting("FILESAVELOC_SETTING"));
	}

	public GoogleContainer() throws Exception {
		this(DEFAULT_KEEPALIVE);
	}

	public GoogleContainer(int ka) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createGUID()), ka);
	}

	public GoogleContainer(String userhost, int ka) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createStringID(
				userhost)), ka);
	}

	public IRosterManager getRosterManager() {
		return presenceHelper.getRosterManager();
	}

	public GoogleContainerNotificationManager getNotificationManager() {
		return notificationManager;

	}

	public IUserSettingsSerializer getUserSettingSerializer() {
		return userSettingSerializer;
	}

	public ISendFileTransferContainerAdapter getFileTransferAdaptor() {
		return fileTransferHelper;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public IChatManager getChatManager() {
		return presenceHelper.getChatManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				GooglePlugin.getDefault().getNamespaceIdentifier());
	}

	public GoogleCallSessionContainerAdapter getCallSessionContainerAdapter() {
		return callSessionContainerAdapter;
	}

	public void connect(ID remote, IConnectContext joinContext)
			throws ContainerConnectException {

		try {
			callSessionContainerAdapter.createVoiceConnection(remote,
					joinContext);

			fileTransferHelper
					.setFileSaveLocation(getUserSettingSerializer() == null ? System
							.getProperty("user.home")
							: getUserSettingSerializer().getSetting(
									"FILESAVELOC_SETTING"));

			fileTransferHelper.createConnection(remote, joinContext);
			getSharedObjectManager().addSharedObject(presenceHelperID,
					presenceHelper, null);
			super.connect(remote, joinContext);
			GooglePlugin.getDefault().registerService(this);
			accountManager.initialize();

		} catch (final ContainerConnectException e) {
			disconnect();
			throw e;
		} catch (final SharedObjectAddException e1) {
			disconnect();
			throw new ContainerConnectException(NLS.bind(
					Messages.XMPPContainer_EXCEPTION_ADDING_SHARED_OBJECT,
					presenceHelperID), e1);
		} catch (final Exception e2) {
			e2.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#disconnect()
	 */
	public void disconnect() {
		super.disconnect();
		final ID groupID = getConnectedID();

		fireContainerEvent(new ContainerDisconnectingEvent(this.getID(),
				groupID));
		synchronized (getConnectLock()) {
			// If we are currently connected
			if (isConnected()) {
				GooglePlugin.getDefault().unregisterService(this);
				final ISynchAsynchConnection conn = getConnection();
				synchronized (conn) {
					synchronized (getGroupMembershipLock()) {
						handleLeave(groupID, conn);
					}
				}
			}
			this.connection = null;
			remoteServerID = null;
			accountManager.setConnection(null);
			callSessionContainerAdapter.disconnect();
			fileTransferHelper.disconnect();
			presenceHelper.disconnect();
			getSharedObjectManager().removeSharedObject(presenceHelperID);
		}
		// notify listeners
		fireContainerEvent(new ContainerDisconnectedEvent(this.getID(), groupID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.ClientSOContainer#dispose()
	 */
	public void dispose() {
		// chatRoomManager.dispose();
		accountManager.dispose();
		// callSessionContainerAdapter.disconnect();
		// fileTransferHelper.disconnect();
		// outgoingFileTransferContainerAdapter.dispose();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.generic.SOContainer#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class clazz) {
		if (clazz.equals(IPresenceContainerAdapter.class)) {
			return this;
		} else if (clazz.equals(ICallSessionContainerAdapter.class)) {
			return callSessionContainerAdapter;
		} else if (clazz.equals(ISendFileTransferContainerAdapter.class)) {
			return fileTransferHelper;
		}

		return super.getAdapter(clazz);
	}

	public GoogleContainerPresenceHelper getPresenceHelper() {
		return presenceHelper;
	}

	protected ID handleConnectResponse(ID originalTarget, Object serverData)
			throws Exception {
		if (originalTarget != null && !originalTarget.equals(getID())) {

			super.handleConnectResponse(originalTarget, serverData);
			final ECFConnection conn = getECFConnection();
			accountManager.setConnection(conn.getXMPPConnection());
			presenceHelper.setUser(new User(originalTarget));
			return originalTarget;

		} else
			throw new ConnectException(
					Messages.XMPPContainer_EXCEPTION_INVALID_RESPONSE_FROM_SERVER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.generic.ClientSOContainer#createConnection(org
	 * .eclipse.ecf.core.identity.ID, java.lang.Object)
	 */
	protected ISynchAsynchConnection createConnection(ID remoteSpace,
			Object data) throws ConnectionCreateException {
		return new ECFConnection(true, getConnectNamespace(), receiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.generic.ClientSOContainer#getConnectData(org
	 * .eclipse.ecf.core.identity.ID,
	 * org.eclipse.ecf.core.security.IConnectContext)
	 */
	protected Object getConnectData(ID remote, IConnectContext joinContext)
			throws IOException, UnsupportedCallbackException {
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

	// chk

	protected Object createConnectData(ID target, Callback[] cbs, Object data) { // first
		// one
		// is
		// password
		// callback
		if (cbs.length > 0) {
			if (cbs[0] instanceof ObjectCallback) {
				final ObjectCallback cb = (ObjectCallback) cbs[0];
				return cb.getObject();
			}
		}
		return data;
	}

	// chk

	protected Callback[] createAuthorizationCallbacks() {
		final Callback[] cbs = new Callback[1];
		cbs[0] = new ObjectCallback();
		return cbs;
	}

	protected int getConnectTimeout() {
		return keepAlive;
	}

	protected org.jivesoftware.smack.Roster getRoster() throws IOException {
		final ECFConnection connection = getECFConnection();
		if (connection != null) {
			return connection.getRoster();
		} else
			return null;
	}

	protected void deliverEvent(Event evt) {
		final SOWrapper wrap = getSharedObjectWrapper(presenceHelperID);
		if (wrap != null)
			wrap.deliverEvent(evt);
	}

	protected void handleXMPPMessage(Packet aPacket) throws IOException {
		System.out.println("RECEIVE: " + aPacket.toXML());
		if (!handleAsExtension(aPacket)) {
			if (aPacket instanceof IQ) {
				deliverEvent(new IQEvent((IQ) aPacket));
			} else if (aPacket instanceof Message) {
				deliverEvent(new MessageEvent((Message) aPacket));
			} else if (aPacket instanceof Presence) {
				deliverEvent(new PresenceEvent((Presence) aPacket));
			} else {
				log(NLS.bind(Messages.XMPPContainer_UNEXPECTED_XMPP_MESSAGE,
						aPacket.toXML()), null);
			}
		}
	}

	protected boolean handleAsExtension(Packet packet) {
		final Iterator i = packet.getExtensions();
		for (; i.hasNext();) {
			final Object extension = i.next();
			if (extension instanceof XHTMLExtension) {
				final XHTMLExtension xhtmlExtension = (XHTMLExtension) extension;
				deliverEvent(new MessageEvent((Message) packet, xhtmlExtension
						.getBodies()));
				return true;
			}
			if (packet instanceof Presence && extension instanceof MUCUser) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.generic.SOContainer#createSharedObjectContext
	 * (org.eclipse.ecf.provider.generic.SOConfig,
	 * org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue)
	 */
	protected SOContext createSharedObjectContext(SOConfig soconfig,
			IQueueEnqueue queue) {
		return new XMPPContainerContext(soconfig.getSharedObjectID(), soconfig
				.getHomeContainerID(), this, soconfig.getProperties(), queue);
	}

	public ECFConnection getECFConnection() {
		return (ECFConnection) super.getConnection();

	}

	public XMPPConnection getXMPPConnection() {
		final ECFConnection conn = getECFConnection();
		if (conn == null)
			return null;
		else
			return conn.getXMPPConnection();

	}

	public GoogleContainerMailManager getMailManager() {
		return mailManager;
	}

	public void setFileSaveLocation(String res) {
		getUserSettingSerializer().setSetting("FILESAVELOC_SETTING", res);
	}
}