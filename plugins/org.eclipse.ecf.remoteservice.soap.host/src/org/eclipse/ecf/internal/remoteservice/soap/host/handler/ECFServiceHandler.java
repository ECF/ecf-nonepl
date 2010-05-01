/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.internal.remoteservice.soap.host.handler;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * This handler can be used to check, intercept the calls to the remote service
 * @since 3.4
 *
 */
public class ECFServiceHandler extends BasicHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Check if the service is already on the engine, otherwise throws an exception
	 */
	public void invoke(MessageContext msgContext) throws AxisFault {
		// If there's already a targetService then just return.
		if (msgContext.getService() != null)
			return;
		else{
			//The specific service wasnt published as WS
			String path = (String) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO);
			throw new AxisFault("Remote service was not found: "+path);
		}
		
	}


}
