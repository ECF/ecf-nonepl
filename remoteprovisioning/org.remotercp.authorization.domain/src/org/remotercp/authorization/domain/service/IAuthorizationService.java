package org.remotercp.authorization.domain.service;

import org.eclipse.ecf.core.identity.ID;

public interface IAuthorizationService {

	public boolean isAdmin(ID userID);
}
