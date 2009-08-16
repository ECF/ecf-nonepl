/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.tests.remoteservice.rest.scenario.googlesearch;

import java.net.MalformedURLException;
import java.net.URL;

public class SearchResult implements ISearchResult {
	
	private URL url;
	private String content;
	private String title;
	
	public SearchResult( String url, String title, String content) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.title = title;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public URL getURL() {
		return url;
	}

}
