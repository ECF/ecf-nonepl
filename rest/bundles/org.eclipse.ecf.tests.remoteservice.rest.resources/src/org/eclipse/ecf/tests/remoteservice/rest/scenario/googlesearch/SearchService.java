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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.remoteservice.rest.IRestResponseProcessor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchService implements ISearch, IRestResponseProcessor {

	private JSONObject jsonObject;

	public ISearchResult[] getResults() {
		JSONObject object;
		JSONArray results = null;
		try {
			object = (JSONObject) jsonObject.get("responseData");
			results = (JSONArray) object.get("results");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}		
		List searchResults = new ArrayList();
		for (int i = 0; i < results.length(); i++) {
			try {
				JSONObject jsonObject = results.getJSONObject(i);
				String url = jsonObject.getString("url");
				String title = jsonObject.getString("titleNoFormatting");
				String content = jsonObject.getString("content");
				ISearchResult searchResult = new SearchResult(url, title, content);
				searchResults.add(searchResult);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		ISearchResult[] resultArray = new ISearchResult[searchResults.size()];
		searchResults.toArray(resultArray);
		return resultArray;
	}

	public void processResource(Object response) {
		if(response instanceof JSONObject)
			this.jsonObject = (JSONObject) response;
	}

}
