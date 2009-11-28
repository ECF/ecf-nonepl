/*******************************************************************************
 *  Copyright (c) 2009 Weltevree Beheer BV, Nederland
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Wim Jongman - initial API and implementation
 *
 *
 *******************************************************************************/
package org.eclipse.ecf.services.quotes;

/**
 * This class creates quotes.
 * 
 * @author Wim Jongman
 * 
 */
public interface QuoteService {


	/**
	 * A short and descriptive name of the service. This could be for instance
	 * "Star Wars" or "20th Century Politics".
	 * 
	 * @return
	 */
	public String getServiceName();

	/**
	 * An extended description of the service.
	 * 
	 * @return
	 */
	public String getServiceDescription();

	/**
	 * Returns a random quote.
	 * 
	 * @return
	 */
	public String getRandomQuote();
 
	/**
	 * Returns all quotes.
	 * 
	 * @return
	 */
	public String[] getAllQuotes();

	
}
