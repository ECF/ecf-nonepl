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

import java.util.List;

/**
 * 
 *@since 3.0
 */
public interface ITweetSearchCompleteEvent {

	/**
	 * 
	 * @return List of {@link ITweetItem} Contain the search results
	 */
	public List getSearchResult();

}
