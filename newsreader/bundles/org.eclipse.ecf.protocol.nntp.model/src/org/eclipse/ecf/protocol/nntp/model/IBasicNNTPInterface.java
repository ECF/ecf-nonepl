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

import java.util.Date;

/**
 * This class provides methods that are used for data transfer with the NNTP server. 
 * 
 * @author wimj@weltevree.com
 * 
 */

public interface IBasicNNTPInterface {

	/**
	 * Replies to the given article. The subject is composed according to the
	 * etiquette "Re: " + subject if the subject of the given article does not
	 * already start with this string.
	 * 
	 * @see IServerConnection#replyToArticle(IArticle, StringBuffer)
	 * 
	 * @param article
	 * @param buffer
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public void replyToArticle(IArticle article, String buffer)
			throws NNTPIOException, UnexpectedResponseException;

	/**
	 * Posts the article in the given newsgroup with the given subject and the
	 * given body.
	 * 
	 * @param newsgroups
	 * @param subject
	 * @param body
	 * @throws {@link NNTPIOException}
	 * @throws {@link UnexpectedResponseException}
	 */
	public void postNewArticle(INewsgroup[] newsgroups, String subject,
			String body) throws NNTPIOException, UnexpectedResponseException;

	/**
	 * Gets the complete follow up tree. This includes the responses to
	 * responses.
	 * 
	 * @param article
	 * @return
	 * @throws NNTPIOException
	 * @throws StoreException 
	 */
	public IArticle[] getAllFollowUps(IArticle article) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * Fetches the bodytext for this article and stores it in the article.
	 * 
	 * @param article
	 * @throws UnexpectedResponseException
	 * @throws NNTPIOException
	 */
	public String[] getArticleBody(IArticle article) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * This method sets the NNTP server in reader mode. Make sure to get or
	 * flush the response afterwards.
	 * 
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException 
	 * 
	 * @see <a href="hhttp://tools.ietf.org/html/rfc3977#section-5.3">RFC3977
	 *      Section 5.3</a>
	 * @see #getResponse()
	 * @see #flush()
	 * 
	 */
	public void setModeReader(IServer server) throws NNTPIOException, UnexpectedResponseException;

	/**
	 * Gets a list of newsgroups.
	 * 
	 * @return
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 */
	public INewsgroup[] listNewsgroups(IServer server) throws NNTPIOException,
			NNTPIOException, UnexpectedResponseException;

	/**
	 * Gets a list of new newsgroups since the supplied date.
	 * 
	 * @return
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 */
	public INewsgroup[] listNewsgroups(IServer server, Date since) throws NNTPIOException,
			UnexpectedResponseException;

	/**
	 * Posts an article
	 * 
	 * @param article
	 * @return
	 */
	boolean postArticle(IArticle article);

	/**
	 * Cancels an article
	 * 
	 * @param article
	 * @return
	 */
	boolean cancelArticle(IArticle article);

	/**
	 * Gets the article from the newsgroup or the store based on the passed
	 * articleId and the fetchType.
	 * 
	 * @param newsgroup
	 * @param articleId
	 *            the group article id which is used in combination with the
	 *            fetchType.
	 * @param fetchType
	 *            which is one of the fetch types: {@link SALVO#FETCH_NEWEST},
	 *            {@link SALVO#FETCH_NEXT_NEWER}, {@link SALVO#FETCH_NEXT_OLDER}
	 *            , {@link SALVO#FETCH_OLDEST}, {@link SALVO#FETCH_THIS}
	 * @return the article or null if it was not found. Get the last
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 */
	public IArticle fetchArticle(INewsgroup newsgroup, int articleId,
			int fetchType) throws NNTPIOException, UnexpectedResponseException;

	/**
	 * Gets the overview headers from the server.
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc3977#section-8.4">RFC3977
	 *      Section 8.4</a>
	 * 
	 * @return String[]
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 */
	public String[] getOverviewHeaders(IServer server) throws NNTPIOException,
			UnexpectedResponseException;

}