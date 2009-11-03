/*******************************************************************************
 * Copyright (c) 2009 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.oscar;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.search.IUserSearchManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;

public class OSCARContainer extends ClientSOContainer implements IPresenceService {

	public static final int DEFAULT_KEEPALIVE = 30000;
	
	protected OSCARContainer(SOContainerConfig config, int keepAlive) throws Exception {
		super(config);
	}
	
	public OSCARContainer() throws Exception {
		this(DEFAULT_KEEPALIVE);
	}
	
	public OSCARContainer(int keepalive) throws Exception {
		this(new SOContainerConfig(IDFactory.getDefault().createGUID()), keepalive);
	}
	
	protected ISynchAsynchConnection createConnection(ID targetID, Object data) throws ConnectionCreateException {
		// TODO Auto-generated method stub
		return null;
	}

	public IAccountManager getAccountManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IChatManager getChatManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IChatRoomManager getChatRoomManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IRosterManager getRosterManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public IUserSearchManager getUserSearchManager() {
		// TODO Auto-generated method stub
		return null;
	}
}
