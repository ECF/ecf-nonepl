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
 * This class provides methods that both the server side and the store side
 * need. The server side needs to fetch the information and the store side needs
 * to store the information.
 * 
 * @author wimj@weltevree.com
 * 
 */
public interface IInputOutputSystem {

	/**
	 * Refreshes the attributes from the server and places it in the passed
	 * newsgroup or moves the information from the passed newsgroup into the
	 * store.
	 * 
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public void updateAttributes(INewsgroup newsgroup) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * Retrieves the body of this article.
	 * 
	 * @param article
	 * @return the body, may not be null
	 * @throws UnexpectedResponseException
	 * @throws NNTPIOException
	 */
	public String[] getArticleBody(IArticle article) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * This method goes to the server and asks for the active newsgroup
	 * attributes. These attributes are then placed back into the newsgroup.
	 * 
	 * @param server
	 * @param newsgroup
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public void setWaterMarks(INewsgroup newsgroup) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * Gets the newsgroup article array with the most new article id in element
	 * 0.
	 * 
	 * @param connection
	 * @return
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public IArticle[] getArticles(INewsgroup newsgroup, int from, int to)
			throws NNTPIOException, UnexpectedResponseException, StoreException;

	/**
	 * Fetch the followups of this article.
	 * 
	 * @param article
	 * @return the follow ups
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public IArticle[] getFollowUps(IArticle article) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * Gets the article from the newsgroup or the store based on the passed
	 * articleId and the fetchType.
	 * 
	 * @param newsgroup
	 * @param articleId
	 *            the group article id which is used in combination with the
	 *            fetchType.
	 * @return the article or null if it was not found.
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 */
	public IArticle getArticle(INewsgroup newsgroup, int articleId)
			throws NNTPIOException, UnexpectedResponseException;

	/**
	 * Gets the article by URL from the newsgroup or the store based on the
	 * passed articleId and the fetchType.
	 * 
	 * @param URL
	 *            - news://server/newsgroup?articleInteger
	 * @return the article or null if it was not found.
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 * @throws NNTPException 
	 */
	public IArticle getArticle(String URL) throws NNTPIOException,
			UnexpectedResponseException, NNTPException;

}
