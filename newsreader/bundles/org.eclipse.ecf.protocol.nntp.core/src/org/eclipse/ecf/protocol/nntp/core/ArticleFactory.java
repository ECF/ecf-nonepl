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
package org.eclipse.ecf.protocol.nntp.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.eclipse.ecf.protocol.nntp.core.internal.Article;
import org.eclipse.ecf.protocol.nntp.core.internal.StringUtils;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;


public class ArticleFactory {

	/**
	 * Factory method to get the article from a XOVER response. The overview
	 * header must have already been set in the newsgroup.
	 * 
	 * @see INewsgroup#getOverviewHeaders()
	 * @param responseValue
	 * @param newsgroup
	 * @return the new article or null if the article could not be created
	 * @throws UnexpectedResponseException 
	 * @throws NNTPIOException 
	 */
	public static IArticle createArticle(String xoverResponseValue,
			INewsgroup newsgroup) throws NNTPIOException, UnexpectedResponseException {

		String[] values = StringUtils.split(xoverResponseValue, "\t");

		String[] headers = newsgroup.getServer().getServerConnection().getOverviewHeaders(newsgroup.getServer());
		if ((values.length - 1) != headers.length) {
			Debug.log(ArticleFactory.class,
					"Invalid match between overview header and XOVER response");
			return null;
		}
		IArticle article = new Article(Integer.valueOf(values[0]).intValue(),
				newsgroup, true);
		for (int ix = 1; ix < values.length; ix++) {
			article.setHeaderAttributeValue(headers[ix - 1], values[ix]);
		}

		return article;

	}

	public static IArticle createArticle(File file) {
		if (file.exists()) {
			try {
				FileInputStream f_in = new FileInputStream(file);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);
				Object obj = obj_in.readObject();
				if (obj instanceof IArticle) {
					return (IArticle) obj;
				}
			} catch (Exception e) {
				Debug.log(IArticle.class, e);
			}
		}
		return null;
	}

}
