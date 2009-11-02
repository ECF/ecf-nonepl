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


public interface IStore extends IStoreEventProvider, IInputOutputSystem, IAdministration {

	/**
	 * @return The last exception or null if there was none.
	 */
	public Exception getLastException();


	/**
	 * Move the articles to the store.
	 * 
	 * @param articles
	 * @return false in case it could not be done.
	 */
	public boolean storeArticles(IArticle[] articles) throws StoreException;

	/**
	 * Stores the article in the store.
	 * 
	 * @param body
	 * @return false in case it could not be done.
	 */
	public boolean storeArticleBody(IArticle article, String[] body);

	/**
	 * Use this method to check if the store is in sync with the server.
	 * 
	 * @return the last (newest) article in the store or null
	 */
	public IArticle getLastArticle(INewsgroup newsgroup);

	/**
	 * Use this method to check if the store is in sync with the server.
	 * 
	 * @return the first (oldest) article in the store or null
	 */
	public IArticle getFirstArticle(INewsgroup newsgroup);

	/**
	 * Returns a meaningful description of this store. <br>
	 * <br>
	 * Examples:
	 * 
	 * <pre>
	 * Local filesystem storage
	 * Derby 4.2.1
	 * </pre>
	 * 
	 * @return String
	 */
	public String getDescription();
	
	/**
	 * Updates the article in the store.
	 * 
	 * @return true if the article could be stored and false if this could not
	 *         be done. In the latter case, see {@link #getLastException()}
	 */
	public void updateArticle(IArticle article);

}
