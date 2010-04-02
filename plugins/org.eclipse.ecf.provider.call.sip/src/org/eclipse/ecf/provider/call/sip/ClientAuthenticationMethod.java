/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.call.sip;


public interface ClientAuthenticationMethod {
    
    /**
     * Initialize the Client authentication method. This has to be
     * done outside the constructor.
     * @throws Exception if the parameters are not correct.
     */
    public void initialize(String realm,String userName,String uri,String nonce
    ,String password,String method,String cnonce,String algorithm) throws Exception;
    
    
    /**
     * generate the response
     * @returns null if the parameters given in the initialization are not
     * correct.
     */
    public String generateResponse();
    
}

