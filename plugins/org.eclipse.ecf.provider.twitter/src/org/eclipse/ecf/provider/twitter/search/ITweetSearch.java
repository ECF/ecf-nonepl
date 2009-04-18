/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.provider.twitter.search;

import org.eclipse.ecf.core.util.ECFException;

/**
 * 
 *@since 3.0
 */
public interface ITweetSearch {

	/**
	 * Execute the search for a specific criteria, blocking until the search returns.
	 * This method can apply search to match the specific criteria in case if the 
	 * provider is not able to do it completely
	 * @param criteria Will not be <code>null</code>.
	 * @return List of {@link ITweetItem} Contain the search results 
	 * @throws ECFException 
	 */
	public IResultTweetList search(String criteria) throws ECFException;

	/**
	 * Execute the search for a specific criteria, not blocking until the search returns.
	 * This method can apply search to match the specific criteria in case if the 
	 * provider is not able to do it completely.
	 * The provider is free to call the methods below with an arbitrary thread, so the
	 * implementation of these methods must be appropriately prepared.
	 * @param criteria Must not be <code>null</code>.
	 * @param listener the listener {@link ITweetSearchListener} to search. Must not be <code>null</code>. 
	 */
	public void search(String criteria, ITweetSearchListener listener);

}
