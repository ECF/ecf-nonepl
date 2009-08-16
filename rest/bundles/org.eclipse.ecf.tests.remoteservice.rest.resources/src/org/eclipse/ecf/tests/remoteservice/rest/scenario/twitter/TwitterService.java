/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.tests.remoteservice.rest.scenario.twitter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.remoteservice.rest.IRestResponseProcessor;
import org.eclipse.ecf.tests.remoteservice.rest.twitter.ITwitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterService implements ITwitter, IUserTimeline, IRestResponseProcessor {

	private JSONArray jsonArray;

	public IUserTimeline getTimeline() {
		return this;
	}

	public IUserStatus[] getUserStatuses() {
		List statuses = new ArrayList();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String source = jsonObject.getString("source");
				String text = jsonObject.getString("text");
				String createdString = jsonObject.getString("created_at");
				IUserStatus status = new UserStatus(createdString, source, text);
				statuses.add(status);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		IUserStatus[] result = new IUserStatus[statuses.size()];
		statuses.toArray(result);
		return result;
	}

	public void processResource(Object response) {
		if(response instanceof JSONArray) {
			jsonArray = (JSONArray) response;
		}
	}

}
