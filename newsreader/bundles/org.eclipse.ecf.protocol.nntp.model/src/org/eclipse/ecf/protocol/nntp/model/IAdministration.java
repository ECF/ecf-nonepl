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
 * This interface is responsible for maintaining the subscribe and unsubscribe
 * administration. It is mainly part of the {@link IStore} functions.
 * 
 * @author Wim Jongman
 * 
 */
public interface IAdministration {

	/**
	 * Stores this newsgroup in the list of groups this user subscribes to in
	 * this server and adds this group to the passed server instance by calling
	 * {@link IServer#subscribeNewsgroup(INewsgroup)}. If the group was added to
	 * the list of groups an {@link SALVO#EVENT_ADD_GROUP} event is fired.
	 * 
	 * @param server
	 *            may not be null
	 * @param group
	 *            may not be null
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public void subscribeNewsgroup(INewsgroup group) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * Subscribes to this server. The back end is responsible of storing the
	 * password in a correct manner.
	 * 
	 * @param server
	 * @param passWord
	 */
	public void subscribeServer(IServer server, String passWord);

	/**
	 * Removes this newsgroup from the list of groups this user subscribes to in
	 * the server. You should also removes this group from the server by calling
	 * {@link IServer#unsubscribeNewsgroup(ISubscribedNewsgroup)}.
	 * 
	 * @param group
	 *            may not be null
	 * @param permanent
	 *            you may clear irreversible
	 */
	public void unsubscribeNewsgroup(INewsgroup group, boolean permanent);

	/**
	 * Removes this server from the list of servers this user subscribes to.
	 * 
	 * @param server
	 *            may not be null
	 * @param permanent
	 *            true to permanently remove the server or false to keep the
	 *            historical data from which this server can be resurrected.
	 */
	public void unsubscribeServer(IServer server, boolean permanent);

	/**
	 * Retrieves the list of newsgroups this user is subscribed of the passed
	 * {@link IServer}.
	 * 
	 * @param server
	 * @return a Collection of newsgroups, can be empty may not be null.
	 */
	public INewsgroup[] getSubscribedNewsgroups(IServer server);

	/**
	 * Retrieves a list with all the servers this client subscribes to. This is
	 * store only.
	 * 
	 * @return
	 */
	public IServer[] getSubscribedServers();

}
