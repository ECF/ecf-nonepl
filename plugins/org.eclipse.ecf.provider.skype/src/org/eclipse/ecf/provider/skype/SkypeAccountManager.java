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

package org.eclipse.ecf.provider.skype;

import java.util.Map;

import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.provider.skype.identity.SkypeUserID;

import com.skype.Profile;

/**
 *
 */
public class SkypeAccountManager implements IAccountManager {

	SkypeContainer container;
	Profile profile;
	SkypeUserID userID;
	User user;
	
	/**
	 * @param skypeContainer
	 * @param skypeProfile
	 * @param userID
	 * @param user
	 */
	public SkypeAccountManager(SkypeContainer skypeContainer,
			Profile skypeProfile, SkypeUserID userID, User user) {
		this.container = skypeContainer;
		this.profile = skypeProfile;
		this.userID = userID;
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#changePassword(java.lang.String)
	 */
	public boolean changePassword(String newpassword) throws ECFException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#createAccount(java.lang.String, java.lang.String, java.util.Map)
	 */
	public boolean createAccount(String username, String password,
			Map attributes) throws ECFException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#deleteAccount()
	 */
	public boolean deleteAccount() throws ECFException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#getAccountAttribute(java.lang.String)
	 */
	public Object getAccountAttribute(String attributeName) throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#getAccountAttributeNames()
	 */
	public String[] getAccountAttributeNames() throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#getAccountCreationInstructions()
	 */
	public String getAccountCreationInstructions() throws ECFException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.IAccountManager#isAccountCreationSupported()
	 */
	public boolean isAccountCreationSupported() throws ECFException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 */
	protected void dispose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public void disconnect() {
	}

}
