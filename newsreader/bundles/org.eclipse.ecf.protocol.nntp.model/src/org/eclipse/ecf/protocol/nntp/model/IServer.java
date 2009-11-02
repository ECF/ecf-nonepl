/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.model;

/**
 * @author jongw
 * 
 */
public interface IServer {

	/**
	 * Returns the port.
	 * 
	 * @return int
	 */
	public int getPort();

	/**
	 * Returns the TCP/IP address.
	 * 
	 * @return String
	 */
	public String getAddress();

	/**
	 * Returns the {@link IServerConnection} object used for communications.
	 * 
	 * @return the {@link IServerConnection} object used for communcations.
	 *         Cannot be null.
	 */
	public IServerConnection getServerConnection();

	/**
	 * Sets the server connection. Please note that the
	 * {@link #getServerConnection()} method is guaranteed not to return null.
	 * Server factories would call this method.
	 * 
	 * @param connection
	 */
	public void setServerConnection(IServerConnection connection);

	/**
	 * Initializes the server. You can setup the connection here but note that
	 * news servers are very impatient and close the connection as soon as they
	 * can. You could do other one time setup here like getting the overview
	 * headers, query for capabilities etcetera.
	 * 
	 * @throws NNTPException
	 */
	public void init() throws NNTPException;

	/**
	 * Indicates that this server communicates over a secure layer (SSL/TSL).
	 * 
	 * @return
	 */
	public boolean isSecure();

	/**
	 * Returns true if this is an anonymous server connection.
	 * 
	 * @return boolean
	 */
	public boolean isAnonymous();

	/**
	 * Gets the organization this user belongs to.
	 * 
	 * @return
	 */
	public String getOrganization();

	/**
	 * Gets the overview headers from this server, could be null if they were
	 * not set before.
	 * 
	 * @return the list over overview headers, or null
	 */
	public String[] getOverviewHeaders();

	/**
	 * Sets the overview headers for quick reference. This should be set by the
	 * {@link IServerConnection} if it is fetched for the first time.
	 * 
	 * @param headers
	 */
	public void setOverviewHeaders(String[] headers);

}
