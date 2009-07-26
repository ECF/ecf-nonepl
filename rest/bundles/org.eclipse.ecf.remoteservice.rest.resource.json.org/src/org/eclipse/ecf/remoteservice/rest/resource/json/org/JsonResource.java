/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.remoteservice.rest.resource.json.org;

import java.text.ParseException;

import org.eclipse.ecf.remoteservice.rest.resource.IRestResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonResource implements IRestResource {

	public Object createRepresentation(String responseBody) throws ParseException {
		String parseErrorMsg = "Could not parse JSON Response";	 
		try {
			return new JSONObject(responseBody);
		} catch (JSONException e) {
			try {
				return new JSONArray(responseBody);
			} catch (JSONException e1) {
				throw new ParseException(parseErrorMsg, 0);
			}			
		}
	}

	public String getIdentifier() {
		return "ecf.rest.resource.json.org";
	}

}

