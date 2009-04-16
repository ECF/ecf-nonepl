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


/**
 * 
 *@since 3.0
 */
public interface ITweetSearchListener {
	
	/**
	 * Catch the event fired and proceed to complete the search.
	 * Handle the search asynchronously. Notify that the search was completed 
	 * for the specific criteria.
	 * @param event the object that deal with the results in a non-blocking way
	 */
	public void handleTweetSearchEvent(ITweetSearchCompleteEvent event);

}
